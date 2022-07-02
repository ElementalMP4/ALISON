// 
// Decompiled by Procyon v0.5.36
// 

package main.java.de.voidtech.alison.listeners;

import java.util.logging.Level;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.GenericEvent;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;
import net.dv8tion.jda.api.hooks.EventListener;

@Component
public class ReadyListener implements EventListener
{
    private static final Logger LOGGER;
    
    public void onEvent(final GenericEvent event) {
        if (event instanceof ReadyEvent) {
            final String clientName = ((ReadyEvent)event).getJDA().getSelfUser().getAsTag();
            ReadyListener.LOGGER.log(Level.INFO, "Alison logged in as " + clientName);
        }
    }
    
    static {
        ReadyListener.LOGGER = Logger.getLogger(ReadyListener.class.getName());
    }
}
