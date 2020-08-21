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
//         validation
//        Set<ConstraintViolation<ToDo>> violations = validator.validate(toDo);
//
//        if (violations.size() > 0) {
//            ArrayList<String> validationMessages = new ArrayList<String>();
//            for (ConstraintViolation<ToDo> violation : violations) {
//
//               // javax.validation.constraints.NotNull
//                if(violation.getConstraintDescriptor().getAnnotation().annotationType().getName().equals("")){
//
//                }
//                validationMessages.add(violation.getPropertyPath().toString() + ": " + violation.getMessage());
//            }
//            return Response.status(Response.Status.BAD_REQUEST).entity(validationMessages).build();
//        }

        ToDo e = toDoDAO.update(id, toDo);
        if(e != null){
            return Response.ok(e).build();
        } else
            return Response.status(Response.Status.NOT_FOUND).build();
    }

}
