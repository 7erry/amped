package com.amped.helloworld.resources;

import com.amped.helloworld.core.Person;
import com.amped.helloworld.db.PersonDAO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

import com.wordnik.swagger.annotations.*;

@Path("/people")
@Api(value = "/people", description = "Operations about people")
@Produces({"application/json"})
public class PeopleResource {

    private final PersonDAO peopleDAO;

    public PeopleResource(PersonDAO peopleDAO) {
        this.peopleDAO = peopleDAO;
    }

    @POST
    @Path("/{personId}")
    @ApiOperation(value = "Add a person by ID", notes = "More notes about this method", response = Person.class)
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid ID supplied"),
        @ApiResponse(code = 404, message = "Person not found")
    })
    @UnitOfWork
    public Person createPerson(Person person) {
        return peopleDAO.create(person);
    }

    @GET
    @Path("/")
    @ApiOperation(value = "List all people", notes = "More notes about this method", response = Person.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "People not found")
    })
    @UnitOfWork
    public List<Person> listPeople() {
        return peopleDAO.findAll();
    }

}
