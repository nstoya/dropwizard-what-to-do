package com.nstoya.whattodo.resource;

import com.nstoya.whattodo.auth.WhatToDoAuthorizer;
import com.nstoya.whattodo.auth.WhatToDoOAuthAuthenticator;
import com.nstoya.whattodo.core.User;
import com.nstoya.whattodo.core.entity.Task;
import com.nstoya.whattodo.core.entity.ToDo;
import com.nstoya.whattodo.db.TaskDAO;
import com.nstoya.whattodo.db.ToDoDAO;
import com.nstoya.whattodo.resources.ToDosResource;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

import javax.validation.Validator;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(DropwizardExtensionsSupport.class)
public class ToDosResourceTest {

    private static ToDoDAO TODO_DAO = mock(ToDoDAO.class);
    private static TaskDAO TASK_DAO = mock(TaskDAO.class);


    private static final OAuthCredentialAuthFilter<User> O_AUTH_HANDLER =
            new OAuthCredentialAuthFilter.Builder<User>()
                    .setAuthenticator(new WhatToDoOAuthAuthenticator("test-token"))
                    .setAuthorizer(new WhatToDoAuthorizer())
                    .setPrefix("Bearer")
                    .buildAuthFilter();

    private static ResourceExtension r = ResourceExtension.builder()
            .addProvider(RolesAllowedDynamicFeature.class)
            .addProvider(new AuthDynamicFeature(O_AUTH_HANDLER))
            .addProvider(new AuthValueFactoryProvider.Binder<>(User.class))
        .build();


    private static final ResourceExtension RESOURCES = r.builder().addResource(new ToDosResource(TODO_DAO, TASK_DAO, r.getValidator()))
            .build();

//            ResourceExtension.builder()
//            .addProvider(RolesAllowedDynamicFeature.class)
//            .addProvider(new AuthDynamicFeature(O_AUTH_HANDLER))
//            .addProvider(new AuthValueFactoryProvider.Binder<>(User.class))



    private ArgumentCaptor<ToDo> toDoCaptor = ArgumentCaptor.forClass(ToDo.class);

    private ToDo toDo;
    ArrayList<Task> tasks;

    @BeforeEach
    public void setup() {

        toDo = new ToDo("1", "description 1");
        toDo.setId(1L);
        tasks = new ArrayList<>();
        Task t1 = new Task("1.1", "1.1");
        Task t2 = new Task("1.2", "1.2");
        t1.setId(2L);
        t2.setId(3L);
        tasks.add(t1);
        tasks.add(t2);
        toDo.setTasks(tasks);
    }

    @AfterEach
    public void tearDown() {
        reset(TODO_DAO);
    }



    @Test
    public void authFail(){
        Response response = RESOURCES.target("/todos")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer notValidToken")
                .get();

        assert response.getStatusInfo().equals(Response.Status.UNAUTHORIZED);

    }

    @Test
    public void createToDo(){
        when(TODO_DAO.create(any(ToDo.class), eq(TASK_DAO))).thenReturn(toDo);
        Response response = RESOURCES.target("/todos")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer test-token")
                .post(Entity.entity(toDo, MediaType.APPLICATION_JSON));

        assert response.getStatusInfo().equals(Response.Status.CREATED);
        verify(TODO_DAO).create(toDoCaptor.capture(), eq(TASK_DAO));
        assert toDoCaptor.getValue().getName().equals(toDo.getName());
        assert toDoCaptor.getValue().getDescription().equals(toDo.getDescription());
        assert toDoCaptor.getValue().getTasks().size() == 2;
    }

    @Test
    public void createToDoEmptyName(){
        toDo.setName("");
        Response response = RESOURCES.target("/todos")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer test-token")
                .post(Entity.entity(toDo, MediaType.APPLICATION_JSON));

        assert response.getStatusInfo().getStatusCode() == 422;
    }

    @Test
    public void updateToDo(){
        when(TODO_DAO.update(eq(1L),any(ToDo.class))).thenReturn(toDo);
        Response response = RESOURCES.target("/todos/1")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer test-token")
                .put(Entity.entity(toDo, MediaType.APPLICATION_JSON));

        assert response.getStatusInfo().equals(Response.Status.OK);
        verify(TODO_DAO).update(eq(1L), toDoCaptor.capture());
        assert toDoCaptor.getValue().getName().equals(toDo.getName());
        assert toDoCaptor.getValue().getDescription().equals(toDo.getDescription());
        assert toDoCaptor.getValue().getTasks().size() == 2;
    }

    @Test
    public void getToDos(){

        final List<ToDo> toDos = Collections.singletonList(toDo);
        when(TODO_DAO.findByPage(eq(1), eq(1))).thenReturn(toDos);
        Response response = RESOURCES.target("/todos")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer test-token")
                .get();

        assert response.getStatusInfo().equals(Response.Status.OK);
        assert response.getHeaders().containsKey("Link");
        assert response.getHeaders().containsKey("X-Total-Count");
    }
}
