package main.java.de.voidtech.alison.service;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import main.java.de.voidtech.alison.commands.AbstractCommand;
import main.java.de.voidtech.alison.util.CustomCollectors;
import net.dv8tion.jda.api.entities.Message;

@Service
public class CommandService
{
    @Autowired
    private ConfigService config;
    @Autowired
    private WordService wordService;
    @Autowired
    private List<AbstractCommand> commands;
    private static final Logger LOGGER = Logger.getLogger(CommandService.class.getSimpleName());;
    
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
        final AbstractCommand commandOpt = commands.stream()
        		.filter(command -> command.getName().equals(messageArray.get(0)))
        		.collect(CustomCollectors.toSingleton());
        if (commandOpt == null) {
            LOGGER.log(Level.INFO, "Command not found: " + messageArray.get(0));
            return;
        }
        LOGGER.log(Level.INFO, "Running command " + commandOpt.getName());
        commandOpt.run(message, messageArray.subList(1, messageArray.size()));
    }   
}
