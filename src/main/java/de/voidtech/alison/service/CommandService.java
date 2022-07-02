// 
// Decompiled by Procyon v0.5.36
// 

package main.java.de.voidtech.alison.service;

import java.util.logging.Level;
import main.java.de.voidtech.alison.util.CustomCollectors;
import java.util.function.Predicate;
import java.util.Arrays;
import net.dv8tion.jda.api.entities.Message;
import java.util.logging.Logger;
import main.java.de.voidtech.alison.commands.AbstractCommand;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommandService
{
    @Autowired
    private ConfigService config;
    @Autowired
    private WordService wordService;
    @Autowired
    private List<AbstractCommand> commands;
    private static final Logger LOGGER;
    
    private boolean shouldHandleAsChatCommand(final String prefix, final Message message) {
        final String messageRaw = message.getContentRaw();
        return messageRaw.startsWith(prefix) && messageRaw.length() > prefix.length();
    }
    
    public void handleCommand(final Message message) {
        final String prefix = this.config.getDefaultPrefix();
        if (!this.shouldHandleAsChatCommand(prefix, message)) {
            this.wordService.learn(message.getAuthor().getId(), message.getContentRaw());
            return;
        }
        final String messageContent = message.getContentRaw().substring(prefix.length());
        final List<String> messageArray = Arrays.asList(messageContent.trim().split("\\s+"));
        final AbstractCommand commandOpt = (AbstractCommand)this.commands.stream().filter(CommandService::lambda$handleCommand$0).collect(CustomCollectors.toSingleton());
        if (commandOpt == null) {
            CommandService.LOGGER.log(Level.INFO, "Command not found: " + messageArray.get(0));
            return;
        }
        CommandService.LOGGER.log(Level.INFO, "Running command " + commandOpt.getName());
        commandOpt.execute(message, messageArray.subList(1, messageArray.size()));
    }
    
    static {
        CommandService.LOGGER = Logger.getLogger(CommandService.class.getSimpleName());
    }
}
