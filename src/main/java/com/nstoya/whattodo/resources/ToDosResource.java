package com.nstoya.whattodo.resources;

import com.nstoya.whattodo.core.paging.Paging;
import com.nstoya.whattodo.db.ToDoDAO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.validation.Validator;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;


@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
public class ToDosResource {

    private final Validator validator;
    private final ToDoDAO toDoDAO;

    public ToDosResource(ToDoDAO toDoDAO, Validator validator){
        this.toDoDAO = toDoDAO;
        this.validator = validator;
    }

    @GET
    @UnitOfWork
    public Response getToDos(@QueryParam("page") int page, @QueryParam("page_size") int pageSize, @Context UriInfo uriInfo){
        long total = toDoDAO.getCount();
        long nextPage = Paging.nextPage(total, page, pageSize);
        String nextPageHeader = nextPage == 0 ? "" : "<" + uriInfo.getBaseUri() + "employees?page=" + nextPage + "&pageSize=" + (pageSize != 0 ? pageSize : Paging.STANDARD_PAGE_SIZE) + ">; rel=\"next\", " ;
        return Response
                .status(Response.Status.OK)
                .entity(toDoDAO.find(page, pageSize))
                .header("Link",  nextPageHeader
                        +"<" + uriInfo.getBaseUri() + "employees?page=" + (Paging.lastPage(total, page, pageSize)) + "&pageSize=" + (pageSize != 0 ? pageSize : Paging.STANDARD_PAGE_SIZE) + ">; rel=\"last\"")
                .header("X-Total-Count", total)
                .build();
    }

}
