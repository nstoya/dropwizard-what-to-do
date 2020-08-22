package com.nstoya.whattodo.db;

import com.nstoya.whattodo.core.entity.Task;
import com.nstoya.whattodo.core.entity.ToDo;
import com.nstoya.whattodo.core.paging.Paging;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class ToDoDAO extends AbstractDAO<ToDo> {

    public ToDoDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public ToDo create (ToDo toDo, TaskDAO taskDAO){

        ToDo persistedTodo = persist(toDo);
        if(toDo.getTasks() != null){
            for (Task t: toDo.getTasks()){
                t.setParent(toDo);
                taskDAO.create(t);
            }
        }
        return persistedTodo;
    }

    public ToDo update (Long id, ToDo toDo){
        ToDo existingTodo = get(id);
        if(existingTodo != null){
            existingTodo.setName(toDo.getName());
            existingTodo.setDescription(toDo.getDescription());

            if(toDo.getTasks() != null){
                for (Task t: toDo.getTasks()){
                    t.setParent(existingTodo);
                }
                existingTodo.setTasks(toDo.getTasks());
            }
            persist(existingTodo);
            return existingTodo;
        }
        return null;
    }

    public boolean delete (Long id){
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

    public ToDo getTodo(Long id) {
        return get(id);
    }
}
