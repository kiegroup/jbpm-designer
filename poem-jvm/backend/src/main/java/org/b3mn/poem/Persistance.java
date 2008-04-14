package org.b3mn.poem;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.cfg.AnnotationConfiguration;


public class Persistance {
	private static final SessionFactory sessionFactory;
	
	static {
        try {
            sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
	
	 public static Session getSession() {
		 Session session = sessionFactory.getCurrentSession();
		 session.beginTransaction();
		 return session;
	 }
	 
	 
	 public static void commit() {
		 sessionFactory.getCurrentSession().getTransaction().commit();
	 }
}
