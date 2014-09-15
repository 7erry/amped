package com.amped.helloworld.resources;

import com.amped.helloworld.core.Person;
import com.amped.helloworld.db.PersonDAO;
import com.amped.helloworld.views.PersonView;
import com.google.common.base.Optional;
import com.sun.jersey.api.NotFoundException;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.wordnik.swagger.annotations.*;

@Path("/person")
@Api(value = "/person", description = "Operations about a person")
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {

    private final PersonDAO peopleDAO;

    public PersonResource(PersonDAO peopleDAO) {
        this.peopleDAO = peopleDAO;
    }

    @GET
    @Path("/{personId}")
    @ApiOperation(value = "Find person by ID", notes = "More notes about this method", response = Person.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Person not found")
    })

    @UnitOfWork
    public Person getPerson(@ApiParam(value = "ID of the person to return", required = true) @PathParam("personId") LongParam personId) {
        return findSafely(personId.get());
    }

	private Person findSafely(long personId) {
		final Optional<Person> person = peopleDAO.findById(personId);
        if (!person.isPresent()) {
            throw new NotFoundException("No such user.");
        }
		return person.get();
	}

    @GET
    @Path("/view_freemarker")
    @ApiOperation(value = "Freemarker view of person", notes = "More notes about this method", response = Person.class)
    @UnitOfWork
    @Produces(MediaType.TEXT_HTML)
    public PersonView getPersonViewFreemarker(@PathParam("personId") LongParam personId) {
        return new PersonView(PersonView.Template.FREEMARKER, findSafely(personId.get()));
    }
    
    @GET
    @Path("/view_mustache")
    @ApiOperation(value = "Mustache view of person", notes = "More notes about this method", response = Person.class)
    @UnitOfWork
    @Produces(MediaType.TEXT_HTML)
    public PersonView getPersonViewMustache(@PathParam("personId") LongParam personId) {
    	return new PersonView(PersonView.Template.MUSTACHE, findSafely(personId.get()));    
    }
}
