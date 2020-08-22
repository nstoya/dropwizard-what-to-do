package com.nstoya.whattodo.db;
import com.nstoya.whattodo.core.entity.Task;
import com.nstoya.whattodo.core.entity.ToDo;
import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(DropwizardExtensionsSupport.class)
public class ToDoDAOTest {

    public DAOTestExtension toDoDaoTR = DAOTestExtension.newBuilder()
            .addEntityClass(Task.class)
            .addEntityClass(ToDo.class)
            .build();

    private ToDoDAO todoDAO;
    private TaskDAO taskDAO;

    @BeforeEach
    public void setUp() {
        todoDAO = new ToDoDAO(toDoDaoTR.getSessionFactory());
        taskDAO = new TaskDAO(toDoDaoTR.getSessionFactory());
    }

    @Test
    public void createToDo() {
        final ToDo first = toDoDaoTR.inTransaction(() -> todoDAO.create(new ToDo("1", "description 1"), taskDAO));

        ToDo t = new ToDo("2", "description 2");
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Task("2.1", "2.1"));
        tasks.add(new Task("2.2", "2.2"));
        t.setTasks(tasks);
        final ToDo second = toDoDaoTR.inTransaction(() -> todoDAO.create(t, taskDAO));

        assert first.getTasks() == null;
        assert second.getTasks().size() == 2;


    }

    @Test
    public void updateTodo(){
        ToDo t = new ToDo("2", "description 2");
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Task("2.1", "2.1"));
        tasks.add(new Task("2.2", "2.2"));
        t.setTasks(tasks);
        final ToDo second = toDoDaoTR.inTransaction(() -> todoDAO.create(t, taskDAO));

        final ToDo third = toDoDaoTR.inTransaction(() -> todoDAO.update(second.getId(), new ToDo("2", "updatedDescription")));

        //tasks are not deleted
        assert third.getTasks() != null;
        assert third.getTasks().size() == 2;

        t.setTasks(new ArrayList<>());
        final ToDo fourth = toDoDaoTR.inTransaction(() -> todoDAO.update(second.getId(), new ToDo("2", "updatedDescription")));

        //tasks only get removed when an empty array is set
        assert fourth.getTasks() != null;
        assert fourth.getTasks().size() == 0;
    }

    @Test
    public void deleteToDo(){
        ToDo t = new ToDo("2", "description 2");
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Task("2.1", "2.1"));
        tasks.add(new Task("2.2", "2.2"));
        t.setTasks(tasks);
        final ToDo second = toDoDaoTR.inTransaction(() -> todoDAO.create(t, taskDAO));
        boolean deleted = toDoDaoTR.inTransaction(() -> todoDAO.delete(second.getId()));
        final ToDo deletedToDo = toDoDaoTR.inTransaction(() -> todoDAO.getTodo(second.getId()));

        assert deleted;
        assert deletedToDo == null;
    }

    @Test
    public void emptyToDoName(){
        Exception exception = assertThrows(ConstraintViolationException.class, () -> {
            toDoDaoTR.inTransaction(() -> todoDAO.create(new ToDo("", "description"), taskDAO));
        });
        assertTrue(exception.getMessage().contains("is not allowed to be an empty string"));
    }

    @Test
    public void nullToDoName(){
        Exception exception = assertThrows(ConstraintViolationException.class, () -> {
            toDoDaoTR.inTransaction(() -> todoDAO.create(new ToDo(null, "description"), taskDAO));
        });
        assertTrue(exception.getMessage().contains("is mandatory but missing"));
    }
}
