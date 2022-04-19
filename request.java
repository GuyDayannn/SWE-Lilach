package org.hw2;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;

@Entity
public class request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Session session;

    public request(Session session) {
        super();
        this.session = session;
    }

    public request() {
        super();
        this.session = null;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public List<flower> GetAllCatalog() {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<flower> query = builder.createQuery(flower.class);
        query.from(flower.class);
        return session.createQuery(query).getResultList();
    }
    public void UpdateItem(flower fl, int price){
        fl.setPrice(price);
        Transaction tx = session.beginTransaction();
        session.update(fl);
        tx.commit();
    }
    public void UpdateItem(int id, int price){

        flower fl = GetItem(id);
        fl.setPrice(price);
        Transaction tx = session.beginTransaction();
        session.update(fl);
        tx.commit();
    }
    public flower GetItem(int id){
        flower fl = session.get(flower.class,id);
        return fl;
    }

    public List<flower> GetItemByColor(String color){
        List<flower> flo = GetAllCatalog();
        List<flower> temp = new ArrayList<>();
        for(flower fl : flo) {
            if (fl.color.equals(color)) {
                temp.add(fl);
            }
        }
        return temp;
    }

    public List<flower> GetItemByName(String name){
        List<flower> flo = GetAllCatalog();
        List<flower> temp = new ArrayList<>();
        for(flower fl : flo) {
            if (fl.name.equals(name)) {
                temp.add(fl);
            }
        }
        return temp;
    }

    public List<flower> GetItemByPrice(int price){
        List<flower> flo = GetAllCatalog();
        List<flower> temp = new ArrayList<>();
        for(flower fl : flo) {
            if (fl.price == price) {
                temp.add(fl);
            }
        }
        return temp;
    }
}

