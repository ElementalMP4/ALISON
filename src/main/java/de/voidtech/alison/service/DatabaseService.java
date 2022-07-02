// 
// Decompiled by Procyon v0.5.36
// 

package main.java.de.voidtech.alison.service;

import javax.persistence.Entity;
import org.reflections.Reflections;
import org.reflections.scanners.Scanner;
import java.util.EnumSet;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.MetadataSources;
import java.util.Map;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.context.annotation.Bean;
import java.util.Set;
import java.util.Properties;
import java.util.logging.Level;
import java.util.function.Consumer;
import java.util.Objects;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.logging.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.stereotype.Service;

@Service
@EnableTransactionManagement
@Configuration
public class DatabaseService
{
    private static final Logger LOGGER;
    @Autowired
    private ConfigService config;
    
    @Bean({ "sessionFactory" })
    @Order(2)
    public SessionFactory getSessionFactory() {
        SessionFactory sessionFactory = null;
        try {
            this.exportSchema();
            final Properties hibernateProperties = this.getHibernateProperties();
            final org.hibernate.cfg.Configuration hibernateConfig = new org.hibernate.cfg.Configuration();
            final Set<Class<?>> allEntities = this.getAllEntities();
            final org.hibernate.cfg.Configuration obj = hibernateConfig;
            Objects.requireNonNull(obj);
            allEntities.forEach(obj::addAnnotatedClass);
            hibernateConfig.setProperties(hibernateProperties);
            sessionFactory = hibernateConfig.buildSessionFactory();
        }
        catch (Exception e) {
            DatabaseService.LOGGER.log(Level.SEVERE, "An error has occurred while setting up Hibernate SessionFactory:\n" + e.getMessage());
        }
        return sessionFactory;
    }
    
    private void exportSchema() {
        final Properties hbnProperties = this.getHibernateProperties();
        final MetadataSources metadataSources = new MetadataSources((ServiceRegistry)new StandardServiceRegistryBuilder().applySettings((Map)hbnProperties).build());
        final Set<Class<?>> allEntities;
        final Set<Class<?>> annotated = allEntities = this.getAllEntities();
        final MetadataSources obj = metadataSources;
        Objects.requireNonNull(obj);
        allEntities.forEach(obj::addAnnotatedClass);
        new SchemaUpdate().setFormat(true).execute((EnumSet)EnumSet.of(TargetType.DATABASE), metadataSources.buildMetadata());
    }
    
    private Properties getHibernateProperties() {
        final Properties properties = new Properties();
        properties.put("hibernate.connection.driver_class", this.config.getDriver());
        properties.put("hibernate.connection.url", this.config.getConnectionURL());
        properties.put("hibernate.connection.username", this.config.getDBUser());
        properties.put("hibernate.connection.password", this.config.getDBPassword());
        properties.put("hibernate.dialect", this.config.getHibernateDialect());
        return properties;
    }
    
    private Set<Class<?>> getAllEntities() {
        return (Set<Class<?>>)new Reflections("main.java.de.voidtech.alison", new Scanner[0]).getTypesAnnotatedWith((Class)Entity.class);
    }
    
    static {
        LOGGER = Logger.getLogger(DatabaseService.class.getName());
    }
}
