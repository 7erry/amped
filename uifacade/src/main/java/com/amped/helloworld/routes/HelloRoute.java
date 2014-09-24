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

from("direct:put-helloworld")
 .setExchangePattern(ExchangePattern.InOut)
 .setHeader(HazelcastConstants.OBJECT_ID, constant(header("name").toString()))
 .setHeader(HazelcastConstants.OPERATION, constant(HazelcastConstants.PUT_OPERATION))
 .toF("hazelcast:%shello", HazelcastConstants.MAP_PREFIX)
 .to("log:Put-Hello?showAll=true");


from("direct:get-helloworld")
 .setExchangePattern(ExchangePattern.InOut)
 .setHeader(HazelcastConstants.OBJECT_ID, constant(header("name").toString()))
 .setHeader(HazelcastConstants.OPERATION, constant(HazelcastConstants.GET_OPERATION))
 .toF("hazelcast:%shello", HazelcastConstants.MAP_PREFIX)
 .to("log:Get-Hello?showAll=true");

/* subscribe to a map	
fromF("hazelcast:%shello", HazelcastConstants.MAP_PREFIX) 
 .log("String... ${body}") 
 .choice() 
 .when(header(HazelcastConstants.LISTENER_ACTION).isEqualTo(HazelcastConstants.ADDED)) 
  .log("...added") 
  .to("hazelcast:seda:end") 
 .otherwise() 
  .log("...failed to add!"); 
*/	
	}

}
