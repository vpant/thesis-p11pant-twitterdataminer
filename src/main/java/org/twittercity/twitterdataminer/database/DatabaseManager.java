package org.twittercity.twitterdataminer.database;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twittercity.twitterdataminer.init.ApplicationStateData;
import org.twittercity.twitterdataminer.twitter.models.State;
import org.twittercity.twitterdataminer.twitter.models.Query;
import org.twittercity.twitterdataminer.twitter.models.Status;

public class DatabaseManager {
	
	private static Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
	
	private DatabaseManager(){}

    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;
    
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Create registry
                registry = new StandardServiceRegistryBuilder().configure().build();
                // Create MetadataSources
                MetadataSources sources = new MetadataSources(registry)
                		.addAnnotatedClass(Status.class)
                		.addAnnotatedClass(ApplicationStateData.class)
                		.addAnnotatedClass(Query.class)
                		.addAnnotatedClass(State.class);
                // Create Metadata
                Metadata metadata = sources.getMetadataBuilder().build();
                // Create SessionFactory
                sessionFactory = metadata.getSessionFactoryBuilder().build();
            } catch (Exception e) {
                logger.error(e.getMessage());
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
            }
        }
        return sessionFactory;
    }
    
    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
