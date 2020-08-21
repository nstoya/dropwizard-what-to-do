package com.nstoya.whattodo.db;

import com.nstoya.whattodo.core.entity.Task;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.logging.Logger;

public class TaskDAO extends AbstractDAO<Task> {

    private static final Logger log = Logger.getLogger(TaskDAO.class.getName());

    public TaskDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Task create (Task task){

        Task persistedTask = persist(task);

        return persistedTask;
    }
}
