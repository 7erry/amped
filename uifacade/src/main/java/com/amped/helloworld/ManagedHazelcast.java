package com.amped.helloworld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.config.Config;
//import com.hazelcast.config.NetworkConfig;
//import com.hazelcast.config.JoinConfig;

import io.dropwizard.lifecycle.Managed;

public class ManagedHazelcast implements Managed {

    final static Logger logger = LoggerFactory.getLogger(ManagedHazelcast.class);
	public HazelcastInstance hzInstance;
	private Config cfg;

	public ManagedHazelcast(HelloWorldConfiguration configuration) throws Exception{
	    // @TODO move these to amped.yml
	    final int PORT_NUMBER=5701;
	    final String MULTICAST_ADDRESS="224.2.2.3";
	    final int MULTICAST_TIMEOUT=5;
            final int MINIMUM_NODES=3;

	    cfg = new Config();
/**
            // wait for a minimum number of nodes before going 'active'
	    //config.setProperty("hazelcast.initial.min.cluster.size",MINIMUM_NODES);
	    NetworkConfig network = cfg.getNetworkConfig();
	    network.setPort(PORT_NUMBER);

	    JoinConfig join = network.getJoin();
	    //join.getTcpIpConfig().setEnabled(false);
	    //join.getAwsConfig().setEnabled(false);
	    join.getMulticastConfig().setEnabled(true);

	    //join.getMulticastConfig().setMulticastGroup(MULTICAST_ADDRESS);
	    //join.getMulticastConfig().setMulticastPort(PORT_NUMBER);
	    //join.getMulticastConfig().setMulticastTimeoutSeconds(MULTICAST_TIMEOUT);
**/
	    //logger.info(join.toString());
	}

	public void start() throws Exception {
	    logger.info("Starting Hazelcast");
            hzInstance = Hazelcast.newHazelcastInstance(cfg);
	    while(! hzInstance.getLifecycleService().isRunning() ){ Thread.sleep(150); }
	    logger.info("Hazelcast Started A Cluster of: "+hzInstance.getCluster().getMembers().size());
	}

	public void stop() throws Exception {
            hzInstance.shutdown();
	    logger.info("Hazelcast.shutdown()");
	}

}
