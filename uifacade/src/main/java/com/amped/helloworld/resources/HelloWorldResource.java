package com.amped.helloworld.resources;

import com.amped.helloworld.ManagedHazelcast;

import com.codahale.metrics.annotation.Timed;
import com.amped.helloworld.core.Saying;
import com.amped.helloworld.core.Template;
import com.google.common.base.Optional;
import io.dropwizard.jersey.caching.CacheControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//hazelcast
import com.hazelcast.core.IAtomicLong;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;

// camel
import org.apache.camel.ProducerTemplate;

// shiro
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;;
import org.apache.shiro.authc.UsernamePasswordToken;;

//swagger
import com.wordnik.swagger.annotations.*;

@Path("/hello-world")
@Api(value = "/hello-world", description = "Dropwizard, Hazelcast, Camel Demo")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {
    private static final Logger logger = LoggerFactory.getLogger(HelloWorldResource.class);

    private final ManagedHazelcast hazelcast;
    private final Template template;

    private ProducerTemplate producer;

    public HelloWorldResource(Template template, ProducerTemplate producer, ManagedHazelcast hazelcast) {
        this.hazelcast = hazelcast;
        this.template = template;
	this.producer = producer;
    }

    @GET
    @ApiOperation(value = "Execute a Saying", notes = "This method uses a hazelcast map.", response = Saying.class)
    @Timed(name = "get-requests")
    @CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.DAYS)
    public Saying sayHello( @QueryParam("name") String name, 
			    @QueryParam("id") Long id) {

	/** Hello Shiro 
	Subject currentUser = SecurityUtils.getSubject();
	if ( !currentUser.isAuthenticated() ) {
	    logger.info("Logging in");
	    UsernamePasswordToken token = new UsernamePasswordToken("lonestarr", "vespa");
	    token.setRememberMe(true);
	    currentUser.login(token);
	} else {
	    logger.info("Already Logged in");
	}
	**/

	String message=""; 
	
	// Example yaml template 
	if(name != null){
	    Optional<String> _name = Optional.of(name);
	    message = template.render(_name);
	}

	// Hello Hazelcast
	Map<Long,Saying> mapSayings = hazelcast.hzInstance.getMap("saying");
	IAtomicLong counter = hazelcast.hzInstance.getAtomicLong("helloworld");

	// attempt to get saying from a hazelcast map
	Saying saying = null;
	if(id != null){
	  saying = (Saying)mapSayings.get(id);
	  if(saying == null)
	    saying = new Saying(counter.incrementAndGet(), message);
	
	  message = saying.content+" "+name;
	}

	logger.info(message);

	// DropWizard example
        return new Saying(counter.incrementAndGet(), message);
    }

    @POST
    @ApiOperation(value = "Create a Saying", notes = "This method adds the input to a hazelcast map" )
    public void receiveHello( @Valid Saying saying ) {
	// store the saying in a hazelcast map
	Map<Long,Saying> mapSayings = hazelcast.hzInstance.getMap("saying");
	mapSayings.put(saying.id, saying);
	logger.info("Received a saying: {}: {}", saying.id,saying.content);
    }
}
