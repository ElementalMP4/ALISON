package main.java.de.voidtech.alison.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(1)
public class ConfigService
{
    private static final Logger LOGGER = Logger.getLogger(ConfigService.class.getName());;
    private final Properties config;
    
    public ConfigService() {
        this.config = new Properties();
        final File configFile = new File("AlisonConfig.properties");
        if (configFile.exists()) {
            try {
                final FileInputStream fis = new FileInputStream(configFile);
                try {
                    this.config.load(fis);
                    fis.close();
                }
                catch (Throwable t) {
                    try {
                        fis.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
            }
            catch (IOException e) {
                LOGGER.log(Level.SEVERE, "an error has occurred while reading the config\n" + e.getMessage());
            }
        }
        else {
            LOGGER.log(Level.SEVERE, "There is no config file. You need a file called AlisonConfig.properties at the root of the project!");
        }
    }
    
    public String getToken() {
        return this.config.getProperty("token");
    }
    
    public String getDefaultPrefix() {
        final String prefix = this.config.getProperty("defaultPrefix");
        return (prefix != null) ? prefix : "a!";
    }
    
    public String getHibernateDialect() {
        final String dialect = this.config.getProperty("hibernate.Dialect");
        return (dialect != null) ? dialect : "org.hibernate.dialect.PostgreSQLDialect";
    }
    
    public String getDriver() {
        final String driver = this.config.getProperty("hibernate.Driver");
        return (driver != null) ? driver : "org.postgresql.Driver";
    }
    
    public String getDBUser() {
        final String user = this.config.getProperty("hibernate.User");
        return (user != null) ? user : "postgres";
    }
    
    public String getDBPassword() {
        final String pass = this.config.getProperty("hibernate.Password");
        return (pass != null) ? pass : "root";
    }
    
    public String getConnectionURL() {
        final String dbURL = this.config.getProperty("hibernate.ConnectionURL");
        return (dbURL != null) ? dbURL : "jdbc:postgresql://localhost:5432/Alison";
    }
}
