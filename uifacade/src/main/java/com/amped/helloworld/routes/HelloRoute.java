package com.amped.helloworld.routes;

import org.apache.camel.builder.RouteBuilder;

import io.dropwizard.util.Duration;

public class HelloRoute extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		
		from("direct:start").delay(Duration.seconds(5).toMilliseconds()).asyncDelayed().to("log:hello");
		
	}

}