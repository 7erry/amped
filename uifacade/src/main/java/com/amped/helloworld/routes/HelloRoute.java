package com.amped.helloworld.routes;

import com.amped.helloworld.ManagedHazelcast;

import org.apache.camel.builder.RouteBuilder;

import io.dropwizard.util.Duration;

public class HelloRoute extends RouteBuilder{
	private ManagedHazelcast hazelcast;

	public void HelloRoute(ManagedHazelcast hazelcast){
	    this.hazelcast = hazelcast;
	}

	@Override
	public void configure() throws Exception {
	    // sample camel route using http://camel.apache.org/delayer.html
	    from("direct:start")
		.delay(Duration.seconds(5).toMilliseconds())
		.asyncDelayed()
		.to("log:hello");
		
	}

}
