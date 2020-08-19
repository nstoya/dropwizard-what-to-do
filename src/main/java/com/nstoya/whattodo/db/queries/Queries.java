package com.nstoya.whattodo.db.queries;

import com.nstoya.whattodo.core.paging.Paging;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.Table;
import javax.persistence.criteria.*;
import java.util.List;

public class Queries {

    public static<T> List<T> getByPage(int page, int pageSize, Class<T> clazz, Session session, String restriction){
        String tableName = clazz.getAnnotation(Table.class).name();
        int firstRes = 0;
        if(page > 1){
            firstRes = (page-1) * pageSize;
        }

        Query<T> query = session
                .createNativeQuery("select * from " + tableName + " " + restriction + " order by id asc", clazz)
                .setFirstResult(firstRes)
                .setMaxResults(( pageSize == 0 || pageSize > Paging.MAX_PAGE_SIZE) ? Paging.STANDARD_PAGE_SIZE : pageSize);
        return query.getResultList();
    }

    public static<T> long getCount(Class<T> clazz, Session session, Predicate... expressions){
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQ = builder.createQuery(Long.class);
        Root<T> root = criteriaQ.from(clazz);

        builder.equal(root.get("parent"), null);
        criteriaQ.select(builder.count(root));
        for(Predicate p: expressions){
            criteriaQ.where(p);
        }
        return session.createQuery(criteriaQ).getSingleResult();
    }

    public static<T> boolean deleteById(Class<T> clazz, Integer id, String idField, Session session){
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaDelete<T> cd = builder.createCriteriaDelete(clazz);
        Root<T> root = cd.from(clazz);
        cd.where(builder.equal(root.get(idField), id));
        Transaction transaction = session.beginTransaction();
        int result = session.createQuery(cd).executeUpdate();
        transaction.commit();
        return result == 1;
    }
}
