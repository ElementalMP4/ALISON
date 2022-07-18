package main.java.de.voidtech.alison.commands;

import java.awt.Color;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import main.java.de.voidtech.alison.annotations.Command;
import main.java.de.voidtech.alison.entities.Toxicity;
import main.java.de.voidtech.alison.service.PrivacyService;
import main.java.de.voidtech.alison.service.WordService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.Result;

@Command
public class HowToxicAmICommand extends AbstractCommand {

	@Autowired
	private WordService wordService;
    
    @Autowired
    private PrivacyService privacyService;
	
	@Override
	public void execute(Message message, List<String> args) {
		
    	String userID;
    	if (args.isEmpty()) userID = message.getAuthor().getId();
    	else userID = args.get(0).replaceAll("([^0-9])", "");
    	
        if (userID.equals("")) {
            message.reply("I couldn't find that user :(").mentionRepliedUser(false).queue();
            return;
        }
        
        Result<User> userResult = message.getJDA().retrieveUserById(userID).mapToResult().complete();
        if (userResult.isSuccess()) {
        	if (privacyService.userIsIgnored(userID)) {
    			message.reply("This user has chosen not to be analysed!").mentionRepliedUser(false).queue();
    			return;
    		}
    		Toxicity howToxic = wordService.scoreUser(userID);
    		if (howToxic == null) {
    			message.reply("I couldn't find any data to analyse!").mentionRepliedUser(false).queue();
    			return;
    		}
    		MessageEmbed toxicityEmbed = new EmbedBuilder()
    				.setColor(getColour(howToxic))
    				.setTitle("How toxic is " + userResult.get().getName() + "?")
    				.setDescription("I searched `" + howToxic.getTotalWordCount() +
    						"` words. From this, I found `" + howToxic.getTokenCount() +
    						"` words with meaning.\n" +
    						"**Positive words found: `" + howToxic.getPositives() + "`**\n" +
    						"**Negative words found: `" + howToxic.getNegatives() + "`**\n" +
    						"**Total Score (higher is better!): `" + howToxic.getScore() + "`**")
    				.setFooter(getMessage(howToxic))
    				.build();
    		message.replyEmbeds(toxicityEmbed).mentionRepliedUser(false).queue();
        } else {
        	message.reply("I couldn't find that user :(").mentionRepliedUser(false).queue();
        }
	}

	private String getMessage(Toxicity howToxic) {
		return howToxic.getScore() < -2 ? "You are a right asshole, you should be nicer >:("
				: howToxic.getScore() < 2 ? "You're an alright person, but could be better." 
				: "Everyone loves you! You say all the nicest things!";
	}

	private Color getColour(Toxicity howToxic) {
		return howToxic.getScore() < -2 ? Color.RED 
				: howToxic.getScore() < 2 ? Color.ORANGE
				: Color.GREEN;
	}

	@Override
	public String getName() {
		return "howtoxicami";
	}

	@Override
	public String getUsage() {
		return "howtoxic";
	}

	@Override
	public String getDescription() {
		return "Using everything I know about you, I will determine how toxic you are!";
	}
}