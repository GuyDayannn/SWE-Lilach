package org.hw2;

import java.util.List;
import java.util.Random;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

class App
{
    private static Session session;

    private static SessionFactory getSessionFactory() throws HibernateException {
        Configuration configuration = new Configuration();
        // Add ALL of your entities here. You can also try adding a whole package.
        configuration.addAnnotatedClass(flower.class);
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        return configuration.buildSessionFactory(serviceRegistry);
    }
    public static void GenerateFlowers(){
        String[] names= {"lilach", "Narkis", "Sigalit" , "Hamania", "Vered"};
        String[] colors = {"Red" , "Blue", "Yellow", "Green","Pink"};
        for(int i=0 ; i<5 ; i++){
        flower fl = new flower(names[i],colors[i],12);
        session.save(fl);
        }
        session.flush();
    }


    public static void main( String[] args ) {
        try{
        SessionFactory sessionFactory = getSessionFactory();
        session = sessionFactory.openSession();
        session.beginTransaction();
        request request = new request(session);
        GenerateFlowers();
        session.getTransaction().commit();
        List<flower> fl = request.GetAllCatalog();

        for (flower flo : fl) {
            System.out.println(flo.toString());
        }

        request.UpdateItem(3,20);

            for (flower flo : fl) {
                System.out.println(flo.toString());
            }
        session.clear();

    } catch (Exception exception) {
    if (session != null) {
        session.getTransaction().rollback();
    }
    System.err.println("An error occured, changes have been rolled back.");
    exception.printStackTrace();
    }finally {
    session.close();
}
    }
}
