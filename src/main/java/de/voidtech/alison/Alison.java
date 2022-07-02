// 
// Decompiled by Procyon v0.5.36
// 

package main.java.de.voidtech.alison;

import java.util.Properties;
import org.springframework.boot.SpringApplication;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import java.util.ArrayList;
import java.util.Arrays;
import net.dv8tion.jda.api.requests.GatewayIntent;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Bean;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import java.util.Collection;
import net.dv8tion.jda.api.JDABuilder;
import main.java.de.voidtech.alison.service.ConfigService;
import net.dv8tion.jda.api.JDA;
import main.java.de.voidtech.alison.listeners.ReadyListener;
import main.java.de.voidtech.alison.listeners.MessageListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Alison
{
    @Bean
    @DependsOn({ "sessionFactory" })
    @Order(3)
    @Autowired
    public JDA getJDA(final MessageListener msgListener, final ReadyListener readyListener) throws LoginException, InterruptedException {
        final ConfigService config = new ConfigService();
        return JDABuilder.createDefault(config.getToken()).enableIntents((Collection)this.getNonPrivilegedIntents()).setMemberCachePolicy(MemberCachePolicy.ALL).setBulkDeleteSplittingEnabled(false).setStatus(OnlineStatus.ONLINE).setCompression(Compression.NONE).addEventListeners(new Object[] { msgListener, readyListener }).build().awaitReady();
    }
    
    private List<GatewayIntent> getNonPrivilegedIntents() {
        final List<GatewayIntent> gatewayIntents = new ArrayList<GatewayIntent>(Arrays.asList(GatewayIntent.values()));
        gatewayIntents.remove(GatewayIntent.GUILD_PRESENCES);
        return gatewayIntents;
    }
    
    @Bean
    public EventWaiter getEventWaiter() {
        return new EventWaiter();
    }
    
    public static void main(final String[] args) {
        final SpringApplication springApp = new SpringApplication(new Class[] { Alison.class });
        final ConfigService configService = new ConfigService();
        final Properties properties = new Properties();
        properties.put("spring.datasource.url", configService.getConnectionURL());
        properties.put("spring.datasource.username", configService.getDBUser());
        properties.put("spring.datasource.password", configService.getDBPassword());
        properties.put("spring.datasource.url", configService.getConnectionURL());
        properties.put("spring.jpa.properties.hibernate.dialect", configService.getHibernateDialect());
        properties.put("jdbc.driver", configService.getDriver());
        properties.put("spring.jpa.hibernate.ddl-auto", "update");
        springApp.setDefaultProperties(properties);
        springApp.run(args);
    }
}
