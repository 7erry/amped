package com.amped.helloworld;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import io.dropwizard.lifecycle.Managed;

public class ManagedCamel implements Managed {

	private final DefaultCamelContext camelContext;

	public ManagedCamel(RouteBuilder... routes) throws Exception{
		camelContext = new DefaultCamelContext();
		for (RouteBuilder routeBuilder : routes) {
			camelContext.addRoutes(routeBuilder);
		}
	}

	public void start() throws Exception {
		camelContext.start();
	}

	public void stop() throws Exception {
		camelContext.stop();
	}

	public ProducerTemplate createProducer() {
		return camelContext.createProducerTemplate();
	}

}
