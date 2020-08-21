package com.nstoya.whattodo.db;

import com.nstoya.whattodo.core.entity.Task;
import com.nstoya.whattodo.core.entity.ToDo;
import com.nstoya.whattodo.core.paging.Paging;
import com.nstoya.whattodo.db.queries.Queries;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.internal.expression.ExpressionImpl;

import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.logging.Logger;

public class ToDoDAO extends AbstractDAO<ToDo> {

    private static final Logger log = Logger.getLogger(ToDoDAO.class.getName());

    public ToDoDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }


    public ToDo create (ToDo toDo, TaskDAO taskDAO){

        ToDo persistedTodo = persist(toDo);
        for (Task t: toDo.getTasks()){
            t.setParent(toDo);
            taskDAO.create(t);
        }
        return persistedTodo;
    }

    public ToDo update (Long id, ToDo toDo){

        ToDo existingTodo = get(id);
        if(existingTodo != null){
            existingTodo.setName(toDo.getName());
            existingTodo.setDescription(toDo.getDescription());

            for (Task t: toDo.getTasks()){
                t.setParent(existingTodo);
            }

            existingTodo.setTasks(toDo.getTasks());
            persist(existingTodo);
            return existingTodo;
        }
        return null;
    }

    public boolean delete (Integer id){
        ToDo e = get(id);
        if (e != null){
            currentSession().delete(e);
            return true;
        }else {
            return false;
        }
    }

    public long getCount(){
        CriteriaBuilder builder = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQ = builder.createQuery(Long.class);
        Root<ToDo> root = criteriaQ.from(ToDo.class);
        criteriaQ.select(builder.count(root));
        return currentSession().createQuery(criteriaQ).getSingleResult();
    }

    public List<ToDo> findByPage(int page, int pageSize){


        pageSize = pageSize == 0 ? Paging.STANDARD_PAGE_SIZE : pageSize;
        page = page <= 0 ? 1 : page;

        int firstRes = page > 1 ? (page-1) * pageSize : 0;
        CriteriaBuilder builder = currentSession().getCriteriaBuilder();
        CriteriaQuery<ToDo> criteriaQ = builder.createQuery(ToDo.class);
        Root<ToDo> root = criteriaQ.from(ToDo.class);
        criteriaQ.select(root);
        criteriaQ.orderBy(builder.asc(root.get("id")));
        TypedQuery<ToDo> typedQuery = currentSession().createQuery(criteriaQ);
        typedQuery.setFirstResult(firstRes);
        typedQuery.setMaxResults(pageSize);

        return typedQuery.getResultList();

    }

    public List<ToDo> findById(int page, int pageSize){
        int firstRes = 0;
        if(page > 1){
            firstRes = (page-1) * pageSize;
        }
        Query<ToDo> query = currentSession()
                .createNativeQuery("select * from " + ToDo.class.getAnnotation(Table.class).name() + " where PARENT is null order by id asc", ToDo.class)
                .setFirstResult(firstRes)
                .setMaxResults(( pageSize == 0 || pageSize > Paging.MAX_PAGE_SIZE) ? Paging.STANDARD_PAGE_SIZE : pageSize);

        return query.getResultList();
    }

    public void saveTasks(ToDo persistedTodo, List<Task> notPersistedTasks, TaskDAO taskDAO){
        EntityManager entityManager = currentSession().getEntityManagerFactory().createEntityManager();

        for (Task t : notPersistedTasks){
            t.setParent(persistedTodo);
            taskDAO.create(t);

        }
    }

    public void deleteTasks(ToDo persistedTodo, List<Task> notPersistedTasks, TaskDAO taskDAO){
        for (Task t : notPersistedTasks){
            currentSession().delete(t);
        }
    }
}
