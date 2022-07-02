// 
// Decompiled by Procyon v0.5.36
// 

package main.java.de.voidtech.alison.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import main.java.de.voidtech.alison.service.CommandService;
import org.springframework.stereotype.Component;
import net.dv8tion.jda.api.hooks.EventListener;

@Component
public class MessageListener implements EventListener
{
    @Autowired
    private CommandService commandService;
    
    public void onEvent(final GenericEvent event) {
        if (event instanceof MessageReceivedEvent) {
            final MessageReceivedEvent msgEvent = (MessageReceivedEvent)event;
            if (msgEvent.getAuthor().getId().equals(msgEvent.getJDA().getSelfUser().getId())) {
                return;
            }
            this.commandService.handleCommand(msgEvent.getMessage());
        }
    }
}
