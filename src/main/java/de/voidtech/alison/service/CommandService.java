package main.java.de.voidtech.alison.service;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import main.java.de.voidtech.alison.commands.AbstractCommand;
import main.java.de.voidtech.alison.util.CustomCollectors;
import main.java.de.voidtech.alison.util.LevenshteinCalculator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

@Service
public class CommandService
{
    @Autowired
    private ConfigService config;
    
    @Autowired
    private WordService wordService;
    
    @Autowired
    private List<AbstractCommand> commands;
    
    @Autowired
    private PrivacyService privacyService;
    
    private static final Logger LOGGER = Logger.getLogger(CommandService.class.getSimpleName());
    private static final int LEVENSHTEIN_THRESHOLD = 2;
    
    private boolean shouldHandleAsChatCommand(final String prefix, final Message message) {
        final String messageRaw = message.getContentRaw();
        return messageRaw.startsWith(prefix) && messageRaw.length() > prefix.length();
    }
    
	private MessageEmbed createLevenshteinEmbed(List<String> possibleOptions) {
		EmbedBuilder levenshteinResultEmbed = new EmbedBuilder()
				.setColor(Color.RED)
				.setTitle("I couldn't find that command! Did you mean `" + String.join("` or `", possibleOptions) + "`?");
		return levenshteinResultEmbed.build();
	}
	
    private void tryLevenshteinOptions(Message message, String commandName) {
        List<String> possibleOptions = new ArrayList<>();
        possibleOptions = commands.stream()
                .map(AbstractCommand::getName)
                .filter(name -> LevenshteinCalculator.calculate(commandName, name) <= LEVENSHTEIN_THRESHOLD)
                .collect(Collectors.toList());
        if (!possibleOptions.isEmpty())
            message.replyEmbeds(createLevenshteinEmbed(possibleOptions)).mentionRepliedUser(false).queue();
    }
    
    public void handleCommand(final Message message) {
        final String prefix = this.config.getDefaultPrefix();
        if (!this.shouldHandleAsChatCommand(prefix, message)) {
        	if (privacyService.userIsIgnored(message.getAuthor().getId())) return;
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
            tryLevenshteinOptions(message, messageArray.get(0));
            return;
        }
        LOGGER.log(Level.INFO, "Running command " + commandOpt.getName());
        commandOpt.run(message, messageArray.subList(1, messageArray.size()));
    }   
}
