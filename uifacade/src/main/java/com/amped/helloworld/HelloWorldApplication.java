package com.amped.helloworld;

import com.amped.helloworld.auth.ExampleAuthenticator;
import com.amped.helloworld.cli.RenderCommand;
import com.amped.helloworld.core.Person;
import com.amped.helloworld.core.Template;
import com.amped.helloworld.db.PersonDAO;
import com.amped.helloworld.filter.DateNotSpecifiedFilterFactory;
import com.amped.helloworld.health.TemplateHealthCheck;
import com.amped.helloworld.resources.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.jetty.HttpConnectorFactory;

import java.util.ArrayList;

// swagger
import com.wordnik.swagger.jaxrs.config.*;
import com.wordnik.swagger.jaxrs.listing.ApiListingResourceJSON;
import com.wordnik.swagger.jaxrs.listing.ApiDeclarationProvider;
import com.wordnik.swagger.jaxrs.listing.ResourceListingProvider;
import com.wordnik.swagger.config.*;
import com.wordnik.swagger.reader.*;
import com.wordnik.swagger.jaxrs.reader.DefaultJaxrsApiReader;

// camel
import com.amped.helloworld.routes.HelloRoute;

public class HelloWorldApplication extends Application<HelloWorldConfiguration> {

    final static Logger logger = LoggerFactory.getLogger(HelloWorldApplication.class);

    ManagedHazelcast hazelcast = null;

    public static void main(String[] args) throws Exception {
        new HelloWorldApplication().run(args);
    }

    private final HibernateBundle<HelloWorldConfiguration> hibernateBundle =
            new HibernateBundle<HelloWorldConfiguration>(Person.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(HelloWorldConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        bootstrap.addCommand(new RenderCommand());

        // use assets as root
        bootstrap.addBundle(new AssetsBundle("/assets", "/ui"));

        // our helloworld api's
        bootstrap.addBundle(new MigrationsBundle<HelloWorldConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(HelloWorldConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });

        // demonstrate hibernate
        bootstrap.addBundle(hibernateBundle);

        // demonstrate view
        bootstrap.addBundle(new ViewBundle());
    }

    @Override
    public void run(HelloWorldConfiguration configuration, Environment environment) throws ClassNotFoundException {
  
	// Hibernate DAO
	final PersonDAO dao = new PersonDAO(hibernateBundle.getSessionFactory());
        
	// hello-world configuration template
	final Template template = configuration.buildTemplate();

        // health check template
        environment.healthChecks().register("template", new TemplateHealthCheck(template));

        // protected resource
        environment.jersey().register(new BasicAuthProvider<>(new ExampleAuthenticator(), "SUPER SECRET STUFF"));
        environment.jersey().register(new ProtectedResource());

        // Filtered Resource
        environment.jersey().getResourceConfig().getResourceFilterFactories().add(new DateNotSpecifiedFilterFactory());
        environment.jersey().register(new FilteredResource());

        // view's
        environment.jersey().register(new ViewResource());

        // Hazelcast
        configureHazelcast(configuration, environment);

        // Camel
        configureCamel(environment, template);

        // Rest API's
        environment.jersey().register(new PeopleResource(dao));
        environment.jersey().register(new PersonResource(dao));

        // Swagger
        configureSwagger(configuration, environment);
    }

    void configureCamel(Environment environment, Template template) {
        try {
            ManagedCamel camel = new ManagedCamel(new HelloRoute());
            environment.lifecycle().manage(camel);
            environment.jersey().register(new HelloWorldResource(template, camel.createProducer(),hazelcast));
        } catch (Exception ex) {
            logger.error(ex.getCause().getMessage());
        }
    }

    void configureSwagger(HelloWorldConfiguration configuration,Environment environment) {
	// get the http host and port for swagger
	DefaultServerFactory sf = (DefaultServerFactory)configuration.getServerFactory();
	ArrayList<String> basePath = new ArrayList<String>();
	// lambda example - here we are simply getting the address and port we are binding on for http
	sf.getApplicationConnectors().forEach(n -> {
	    if(n instanceof HttpConnectorFactory){
		String host = ((HttpConnectorFactory)n).getBindHost()!=null ? ((HttpConnectorFactory)n).getBindHost() : "localhost";
	    	basePath.add("http://"+host+":"+((HttpConnectorFactory)n).getPort());
	    }
	});
        logger.info("Swagger UI:\t" + basePath.get(0).toString() + "/ui/index.html\n");

        String API_VERSION = "1.0.0";
        // swagger setup (http://swagger.wordnik.com/)
        environment.jersey().register(new ApiListingResourceJSON());
        environment.jersey().register(new ApiDeclarationProvider());
        environment.jersey().register(new ResourceListingProvider());
        ScannerFactory.setScanner(new DefaultJaxrsScanner());
        ClassReaders.setReader(new DefaultJaxrsApiReader());

        SwaggerConfig swaggerConfig = ConfigFactory.config();
        swaggerConfig.setApiVersion(API_VERSION);
        swaggerConfig.setBasePath(basePath.toString()+"/ui");

    }

    void configureHazelcast(HelloWorldConfiguration configuration, Environment environment) {
        try {
            hazelcast = new ManagedHazelcast(configuration);
            environment.lifecycle().manage(hazelcast);
        } catch (Exception ex) {
            logger.error(ex.getCause().getMessage());
        }
    }

/*
    void configureMongo(HelloWorldConfiguration configuration, Environment environment) throws Exception {
	Mongo mongo = new Mongo(configuration.mongohost, configuration.mongoport);
	DB db = mongo.getDB(configuration.mongodb);
	MongoManaged mongoManaged = new MongoManaged(mongo);
	environment.lifecycle().manage(mongoManaged);
	environment..healthChecks().register("mongo", new MongoHealthCheck(mongo));

	JacksonDBCollection<Saying, String> sayings = JacksonDBCollection.wrap(db.getCollection("sayings"), Saying.class, String.class);
	if (sayings.find().count() == 0)
	    sayings.insert(new Saying("Hello %s", "en"));

	environment.jersey().register(new HelloWorldResource(new MongoSayingRepository(sayings)));

	JacksonDBCollection<Story, String> stories = JacksonDBCollection.wrap(db.getCollection("stories"), Story.class, String.class);

	environment.jersey().register(new StoryResource(stories));
    }
*/
}
