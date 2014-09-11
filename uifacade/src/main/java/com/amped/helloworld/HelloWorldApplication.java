package com.amped.helloworld;

import com.amped.helloworld.auth.ExampleAuthenticator;
import com.amped.helloworld.cli.RenderCommand;
import com.amped.helloworld.core.Person;
import com.amped.helloworld.core.Template;
import com.amped.helloworld.db.PersonDAO;
import com.amped.helloworld.filter.DateNotSpecifiedFilterFactory;
import com.amped.helloworld.health.TemplateHealthCheck;
import com.amped.helloworld.resources.*;

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
	bootstrap.addBundle(new AssetsBundle("/assets","/ui"));
	//bootstrap.addBundle(new AssetsBundle("/assets/css", "/css", null, "css"));
	//bootstrap.addBundle(new AssetsBundle("/assets/js", "/js", null, "js"));
	//bootstrap.addBundle(new AssetsBundle("/assets/fonts", "/fonts", null, "fonts"));

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
    public void run(HelloWorldConfiguration configuration,
                    Environment environment) throws ClassNotFoundException {
        final PersonDAO dao = new PersonDAO(hibernateBundle.getSessionFactory());
        final Template template = configuration.buildTemplate();

        environment.healthChecks().register("template", new TemplateHealthCheck(template));
        environment.jersey().getResourceConfig().getResourceFilterFactories().add(new DateNotSpecifiedFilterFactory());

        environment.jersey().register(new BasicAuthProvider<>(new ExampleAuthenticator(),
                                                        "SUPER SECRET STUFF"));

        environment.jersey().register(new HelloWorldResource(template));
        environment.jersey().register(new ViewResource());
        environment.jersey().register(new ProtectedResource());
        environment.jersey().register(new PeopleResource(dao));
        environment.jersey().register(new PersonResource(dao));
        environment.jersey().register(new FilteredResource());

	// Integrate Camel
	try{
          ManagedCamel camel = new ManagedCamel(new HelloRoute());
          environment.lifecycle().manage(camel);
          //environment.jersey().register(new SampleResource(camel.createProducer()));
	}catch(Exception ex){
	  ;
	}

	// Integrate Swagger
	configureSwagger(environment);
    }

    void configureSwagger(Environment environment) {
	String API_VERSION = "1.0.0";
        // swagger setup (http://swagger.wordnik.com/) 
        environment.jersey().register(new ApiListingResourceJSON()); 
        environment.jersey().register(new ApiDeclarationProvider()); 
        environment.jersey().register(new ResourceListingProvider()); 
        ScannerFactory.setScanner(new DefaultJaxrsScanner()); 
        ClassReaders.setReader(new DefaultJaxrsApiReader()); 

        SwaggerConfig swaggerConfig = ConfigFactory.config(); 
        swaggerConfig.setApiVersion(API_VERSION); 
        swaggerConfig.setBasePath("http://local.amplify.com:8080"); 

        /* Allow CORS for Swagger */ 
        //FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class); 
        //filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*"); 
        //filter.setInitParameter("allowedOrigins", "*"); 
        //filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin"); 
        //filter.setInitParameter("allowedMethods", "GET,PUT,POST,DELETE,OPTIONS"); 
        //filter.setInitParameter("preflightMaxAge", "5184000"); // 2 months 
        //filter.setInitParameter("allowCredentials", "true"); 
    }
}
