package org.twittercity.twitterdataminer.database.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.twittercity.twitterdataminer.database.DatabaseManager;
import org.twittercity.twitterdataminer.twitter.models.Query;


public class QueryDAO {
	
	private QueryDAO() {}
	
	public static boolean isQueryTableEmpty() {
		try ( Session session = DatabaseManager.getSessionFactory().openSession();) {
			return session.createQuery("SELECT 1 FROM Query").setMaxResults(1).list().isEmpty();
		}
	}


	public static void saveQueries(List<Query> queries) {
		try ( Session session = DatabaseManager.getSessionFactory().openSession();) {
			for(Query query : queries) {
				session.save(query);
			}
		}
	}
	
	public static void updateQuery(Query query) {
		try ( Session session = DatabaseManager.getSessionFactory().openSession();) {
			Transaction tx = session.beginTransaction();
			session.saveOrUpdate(query);
			tx.commit();
		}
	}
	
	public static List<Query> getQueries() {
		ArrayList<Query> query;
		try ( Session session = DatabaseManager.getSessionFactory().openSession();) {
			Transaction tx = session.beginTransaction();
			query = (ArrayList<Query>) session.createQuery("SELECT q FROM Query q", Query.class).getResultList();
			tx.commit();
		}
		return query;
	}
	
	
	
	public static Query getQueryById(int queryId) {
		Query query = null;
		try ( Session session = DatabaseManager.getSessionFactory().openSession();) {
			query = session.get(Query.class, queryId);
		}
		return query;
	}
	
	public static Query getNextQuery(int queryId) {
		Query query = null;
		try ( Session session = DatabaseManager.getSessionFactory().openSession();) {
			query = session.createQuery("FROM Query WHERE id > :queryId", Query.class)
					.setParameter("queryId", queryId).setMaxResults(1).uniqueResult();
		}
		return query;
	}
	
	
	
	/**
	 * Checks the queries' database table and counts the row
	 * @return How many queries are stored in the database
	 */
	public static int countQueries() {
		try ( Session session = DatabaseManager.getSessionFactory().openSession();) {
			EntityManagerFactory entityManagerFactory = session.getEntityManagerFactory();
			CriteriaBuilder criteriaBuilder = entityManagerFactory.getCriteriaBuilder();
			CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
			
			criteria.select(criteriaBuilder.count(criteria.from(Query.class)));
			return Math.toIntExact(entityManagerFactory.createEntityManager().createQuery(criteria).getSingleResult());
		}
	}
}
