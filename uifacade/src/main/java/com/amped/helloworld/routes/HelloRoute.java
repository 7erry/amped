package com.amped.helloworld.routes;

import com.amped.helloworld.ManagedHazelcast;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.ExchangePattern;
import io.dropwizard.util.Duration;
import org.apache.camel.component.hazelcast.HazelcastConstants;

public class HelloRoute extends RouteBuilder{
	private ManagedHazelcast hazelcast;

	public void HelloRoute(ManagedHazelcast hazelcast){
	    this.hazelcast = hazelcast;
	}

	@Override
	public void configure() throws Exception {

	onException(NullPointerException.class)
	 .handled(true)
	 .to("log:Exception?showAll=true");

	from("direct:helloworld")
	 .transform().simple("${body.content}")
	 .to("log:helloworld?showAll=true");

	}

}
