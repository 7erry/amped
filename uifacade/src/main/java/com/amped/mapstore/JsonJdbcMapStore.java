import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
 
import javax.sql.DataSource;
 
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hazelcast.core.MapStore;
 
/**
 * Can be used to persist a Hazelcast String-Object map into a SQL database with objects stored in JSON format.
 * 
 * <i>This solution uses a table per map and expects the table to already exist.</i>
 * 
 * For example in MySQL you can create a table like this:
 * 
 * <pre>
 *     CREATE TABLE hz_map_yourMap (
 *         key_md5 VARCHAR(32) PRIMARY KEY,
 *         key_org VARCHAR(256) NOT NULL,
 *         classname VARCHAR(256) NOT NULL,
 *         value TEXT NULL,
 *         lastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
 *     )
 * </pre>
 * 
 * where the 'lastUpdated' columns is optional.
 * 
 * @author Ricardo Lindooren
 */
public class JsonJdbcMapStore implements MapStore<String, Object> {
 
    private static final Logger logger = LoggerFactory.getLogger(JsonJdbcMapStore.class);
 
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
 
    private String checkKeySql;
    private String loadSql;
    private String loadAllKeysSql;
    private String storeUpdateSql;
    private String storeInsertSql;
    private String deleteSql;
 
    private DataSource dataSource;
 
    /**
     * 
     * @param dataSource
     *            tip: use a database connection pool like 'org.apache.commons.dbcp.BasicDataSource'
     * @param tableNameForMap
     *            the name of the table that will hold values for the map
     */
    public JsonJdbcMapStore(DataSource dataSource, String tableNameForMap) {
        if (dataSource == null) {
            throw new IllegalArgumentException("Argument dataSource is required");
        }
        this.dataSource = dataSource;
 
        if (StringUtils.isBlank(tableNameForMap)) {
            throw new IllegalArgumentException("Argument tableNameForMap is required, provided: '" + tableNameForMap + "'");
        }
        logger.info("Using table name '{}'", tableNameForMap);
 
        checkKeySql = String.format("select 1 from %s where key_md5=?", tableNameForMap);
        loadSql = String.format("select classname, value from %s where key_md5=?", tableNameForMap);
        loadAllKeysSql = String.format("select key_org from %s", tableNameForMap);
        storeInsertSql = String.format("insert into %s (key_md5, key_org, classname, value) values(?, ?, ?, ?)", tableNameForMap);
        storeUpdateSql = String.format("update %s set classname=?, value=? where key_md5=?", tableNameForMap);
        deleteSql = String.format("delete from %s where key_md5=?", tableNameForMap);
    }
 
    protected String serialize(Object obj) {
        return gson.toJson(obj);
    }
 
    @SuppressWarnings("unchecked")
    protected Object deSerialize(@SuppressWarnings("rawtypes") Class clazz, String serialized) {
        return gson.fromJson(serialized, clazz);
    }
 
    protected String getClassName(Object obj) {
        return obj.getClass().getName();
    }
 
    protected String createMd5(String data) {
        return DigestUtils.md5Hex(data);
    }
 
    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
 
    @Override
    public Object load(String key) {
        if (key == null) {
            logger.warn("Key was null, returning null");
            return null;
        }
        Connection connection = null;
        try {
            connection = getConnection();
            return load(connection, key);
        }
        catch (Exception ex) {
            logger.error("Error while loading value for key '" + key + "'", ex);
        }
        finally {
            cleanUp(connection, null);
        }
 
        return null;
    }
 
    @Override
    public Map<String, Object> loadAll(Collection<String> keys) {
        if (keys == null) {
            return null;
        }
        Map<String, Object> result = new HashMap<String, Object>(keys.size());
 
        Connection connection = null;
        try {
            connection = getConnection();
            for (String key : keys) {
                Object obj = load(connection, key);
                result.put(key, obj);
            }
        }
        catch (Exception ex) {
            logger.error("Error while loading all for keys: " + keys, ex);
        }
        finally {
            cleanUp(connection, null);
        }
        return result;
    }
 
    protected Object load(Connection connection, String key) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(loadSql);
            ps.setString(1, createMd5(key));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String className = rs.getString(1);
                String serializedValue = rs.getString(2);
                Object obj = deSerialize(Class.forName(className), serializedValue);
                logger.debug("Loaded object '{}' for key '{}'", obj, key);
                return obj;
            }
        }
        catch (Exception ex) {
            logger.error("Error while loading for key '" + key + "'", ex);
        }
        finally {
            cleanUp(null, ps);
        }
        return null;
    }
 
    @Override
    public Set<String> loadAllKeys() {
        Set<String> allKeys = new HashSet<String>();
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = getConnection();
            ps = connection.prepareStatement(loadAllKeysSql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String key = rs.getString(1);
                logger.debug("Loaded key '{}'", key);
                allKeys.add(key);
            }
        }
        catch (Exception ex) {
            logger.error("Error while loading all keys", ex);
        }
        finally {
            cleanUp(connection, ps);
        }
        return allKeys;
    }
 
    @Override
    public void store(String key, Object value) {
        Connection connection = null;
        try {
            connection = getConnection();
            store(connection, key, value);
        }
        catch (Exception ex) {
            logger.error("Error while storing value '" + value + "' for key '" + key + "'", ex);
        }
        finally {
            cleanUp(connection, null);
        }
    }
 
    @Override
    public void storeAll(Map<String, Object> map) {
        if (map == null) {
            return;
        }
        String key = null;
        Object value = null;
        Connection connection = null;
        try {
            connection = getConnection();
            for (String keyFromMap : map.keySet()) {
                key = keyFromMap;
                value = map.get(key);
                store(connection, key, value);
            }
        }
        catch (Exception ex) {
            logger.error("Error while storing value '" + value + "' for key '" + key + "'", ex);
        }
        finally {
            cleanUp(connection, null);
        }
    }
 
    protected void store(Connection connection, String key, Object value) {
        PreparedStatement ps = null;
        try {
            ps = createStorePreparedStatement(connection, key, value);
            if (ps.executeUpdate() > 0) {
                logger.debug("Stored for key '{}': {}", key, (value != null ? serialize(value) : null));
            }
            else {
                logger.warn("Nothing stored for key '{}': {}", key, (value != null ? serialize(value) : null));
            }
        }
        catch (Exception ex) {
            logger.error("Error while storing value '" + value + "' for key '" + key + "'. ps="+ps, ex);
        }
        finally {
            cleanUp(null, ps);
        }
    }
 
    protected PreparedStatement createStorePreparedStatement(Connection connection, String key, Object value) throws SQLException {
        final String key_md5 = createMd5(key);
        PreparedStatement ps;
        if (keyExists(connection, key)) {
            ps = connection.prepareStatement(storeUpdateSql);
            if (value == null) {
                ps.setNull(1, Types.VARCHAR);
                ps.setNull(2, Types.VARCHAR);
            }
            else {
                ps.setString(1, getClassName(value));
                ps.setString(2, serialize(value));
            }
            ps.setString(3, key_md5);
        }
        else {
            ps = connection.prepareStatement(storeInsertSql);
            ps.setString(1, key_md5);
            ps.setString(2, key);
            if (value == null) {
                ps.setNull(3, Types.VARCHAR);
                ps.setNull(4, Types.VARCHAR);
            }
            else {
                ps.setString(3, getClassName(value));
                ps.setString(4, serialize(value));
            }
        }
        return ps;
    }
 
    @Override
    public void delete(String key) {
        Connection connection = null;
        try {
            connection = getConnection();
            delete(connection, key);
        }
        catch (Exception ex) {
            logger.error("Error while deleting entry for key '" + key + "'", ex);
        }
        finally {
            cleanUp(connection, null);
        }
    }
 
    @Override
    public void deleteAll(Collection<String> keys) {
        if (keys == null) {
            return;
        }
        String key = null;
        Connection connection = null;
        try {
            connection = getConnection();
            for (String keyFromMap : keys) {
                key = keyFromMap;
                delete(connection, key);
            }
        }
        catch (Exception ex) {
            logger.error("Error while deleting entry for key '" + key + "'", ex);
        }
        finally {
            cleanUp(connection, null);
        }
    }
 
    protected void delete(Connection connection, String key) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(deleteSql);
            ps.setString(1, createMd5(key));
            if (ps.executeUpdate() > 0) {
                logger.debug("Deleted for key '{}'", key);
            }
            else {
                logger.warn("Nothing deleted for key '{}'", key);
            }
        }
        catch (Exception ex) {
            logger.error("Error while deleting entry for key '" + key + "'", ex);
        }
        finally {
            cleanUp(null, ps);
        }
    }
 
    protected boolean keyExists(Connection connection, String key) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(checkKeySql);
            ps.setString(1, createMd5(key));
            rs = ps.executeQuery();
            return rs.next();
        }
        catch (Exception ex) {
            logger.error("Error while checking if key '" + key + "' exists", ex);
        }
        finally {
            cleanUp(null, ps);
        }
        return false;
    }
 
    public void cleanUp(Connection connection, PreparedStatement ps) {
        try {
            if (ps != null) {
                logger.debug("Closing prepared statement {}", ps);
                ps.close();
            }
            if (connection != null) {
                logger.debug("Closing connection {}", connection);
                connection.close();
            }
        }
        catch (Exception ex) {
            logger.warn("Error while closing resources", ex);
        }
    }
}
