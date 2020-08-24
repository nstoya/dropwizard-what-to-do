package com.nstoya.whattodo.resources;

import com.nstoya.whattodo.core.User;
import com.nstoya.whattodo.core.entity.Task;
import com.nstoya.whattodo.core.entity.ToDo;
import com.nstoya.whattodo.core.paging.Paging;
import com.nstoya.whattodo.db.TaskDAO;
import com.nstoya.whattodo.db.ToDoDAO;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

import javax.annotation.security.PermitAll;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
public class ToDosResource {

    private final ToDoDAO toDoDAO;
    private final TaskDAO taskDAO;
    private final Validator validator;

    public ToDosResource(ToDoDAO toDoDAO, TaskDAO taskDAO, Validator validator){
        this.toDoDAO = toDoDAO;
        this.taskDAO = taskDAO;
        this.validator = validator;
    }

    @GET
    @UnitOfWork
    @PermitAll
    public Response getToDos(@Auth User user, @QueryParam("page") int page, @QueryParam("page_size") int pageSize, @Context UriInfo uriInfo){
        long total = toDoDAO.getCount();

        return Response
                .status(Response.Status.OK)
                .entity(toDoDAO.findByPage(page, pageSize))
                .header("Link",  Paging.getLinkHeader(uriInfo, "todos", total, page, pageSize))
                .header("X-Total-Count", total)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    @PermitAll
    public Response getToDo(@Auth User user, @PathParam("id") Long id){
        ToDo toDo =  toDoDAO.getTodo(id);

        return toDoDAO.getTodo(id) != null
                ? Response.status(Response.Status.OK).entity(toDo).build()
                : Response.status(Response.Status.NOT_FOUND).entity("").build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    @PermitAll
    public Response createTodo(@Auth User user, @Valid ToDo toDo) {
        // validation
        Set<ConstraintViolation<Task>> violations = new HashSet<>();

        ArrayList<String> validationMessages = new ArrayList<String>();
        for (int i = 1; i <= toDo.getTasks().size(); i++){
            Set<ConstraintViolation<Task>> taskViolations = validator.validate(toDo.getTasks().get(i-1));
            for (ConstraintViolation<Task> violation : taskViolations) {
                validationMessages.add("tasks [" + i + "] " + violation.getPropertyPath().toString() + ": " + violation.getMessage());
            }
        }

        if (validationMessages.size() > 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationMessages).build();
        }
        return Response.status(Response.Status.CREATED).entity(toDoDAO.create(toDo, taskDAO)).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    @PermitAll
    public Response updateTodoById(@Auth User user, @PathParam("id") Long id, @Valid ToDo toDo) {

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
    public Response removeToDoById(@Auth User user, @PathParam("id") Long id) {
        boolean success = toDoDAO.delete(id);
        //do we want to tell if the object didn't exist anyway?
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
