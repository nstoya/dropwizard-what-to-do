package com.nstoya.whattodo.resources;

import com.nstoya.whattodo.core.entity.ToDo;
import com.nstoya.whattodo.core.paging.Paging;
import com.nstoya.whattodo.db.TaskDAO;
import com.nstoya.whattodo.db.ToDoDAO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.annotation.security.PermitAll;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Set;


@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
public class ToDosResource {

    private final Validator validator;
    private final ToDoDAO toDoDAO;
    private final TaskDAO taskDAO;

    public ToDosResource(ToDoDAO toDoDAO, TaskDAO taskDAO, Validator validator){
        this.toDoDAO = toDoDAO;
        this.validator = validator;
        this.taskDAO = taskDAO;
    }

    @GET
    @UnitOfWork
    @PermitAll
    public Response getToDos(@QueryParam("page") int page, @QueryParam("page_size") int pageSize, @Context UriInfo uriInfo){
        long total = toDoDAO.getCount();

        return Response
                .status(Response.Status.OK)
                .entity(toDoDAO.findByPage(page, pageSize))
                .header("Link",  Paging.getLinkHeader(uriInfo, "todos", total, page, pageSize))
                .header("X-Total-Count", total)
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    @PermitAll
    public Response createTodo(@Valid ToDo toDo) throws URISyntaxException {
        return Response.status(Response.Status.CREATED).entity(toDoDAO.create(toDo, taskDAO)).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    @PermitAll
    public Response updateTodoById(@PathParam("id") Long id, @Valid ToDo toDo) {

        ToDo e = toDoDAO.update(id, toDo);
        if(e != null){
            return Response.ok(e).build();
        } else
            return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    @PermitAll
    public Response removeEmployeeById(@PathParam("id") Long id) {
        boolean success = toDoDAO.delete(id);
        //do we want to tell if the object didn't exist anyway?
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
