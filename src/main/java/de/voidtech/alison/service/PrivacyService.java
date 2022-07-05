package main.java.de.voidtech.alison.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import main.java.de.voidtech.alison.entities.IgnoredUser;

@Service
public class PrivacyService {

	@Autowired
	private SessionFactory sessionFactory;
	
	public void optOut(String id) {
    	try (Session session = sessionFactory.openSession()) {
            session.getTransaction().begin();
            session.saveOrUpdate(new IgnoredUser(id));
            session.getTransaction().commit();
        }
	}
	
	public void optIn(String id) {
    	try (Session session = sessionFactory.openSession()) {
            session.getTransaction().begin();
            session.createQuery("DELETE FROM IgnoredUser WHERE userID = :userID").setParameter("userID", id).executeUpdate();
            session.getTransaction().commit();
        }
	}

	public boolean userIsIgnored(String id) {
		try (Session session = sessionFactory.openSession()) {
            final IgnoredUser user = (IgnoredUser) session.createQuery("FROM IgnoredUser WHERE userID = :userID")
            		.setParameter("userID", id)
            		.uniqueResult();
            return user != null;
        }
	}
}