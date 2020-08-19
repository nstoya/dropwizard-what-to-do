package com.nstoya.whattodo.db;

import com.nstoya.whattodo.core.entity.ToDo;
import com.nstoya.whattodo.db.queries.Queries;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import javax.persistence.criteria.*;
import java.util.List;
import java.util.logging.Logger;

public class ToDoDAO extends AbstractDAO<ToDo> {

    private static final Logger log = Logger.getLogger(ToDoDAO.class.getName());

    public ToDoDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }


    public ToDo create (ToDo toDo){

        return persist(toDo);
    }

    public ToDo update (Long id, ToDo toDo){
        ToDo e = get(id);
        if(e != null){
            e.setName(toDo.getName());
            e.setDescription(toDo.getDescription());
            return persist(e);
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

       // builder.isNull(root.get("parent"));
        criteriaQ.select(builder.count(root));

        return currentSession().createQuery(criteriaQ).getSingleResult();
    }

    public List<ToDo> find(int page, int pageSize){
        String where = "where PARENT = null";
        return Queries.getByPage(page, pageSize, ToDo.class, currentSession(), where);
    }
}
