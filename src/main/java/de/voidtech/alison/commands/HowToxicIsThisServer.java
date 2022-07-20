package main.java.de.voidtech.alison.commands;

import java.awt.Color;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import main.java.de.voidtech.alison.annotations.Command;
import main.java.de.voidtech.alison.entities.Toxicity;
import main.java.de.voidtech.alison.service.WordService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

@Command
public class HowToxicIsThisServer extends AbstractCommand {

	@Autowired
	private WordService wordService;
	
	@Override
	public void execute(Message message, List<String> args) {
		Toxicity howToxic = wordService.scoreServer(message.getGuild());
		if (howToxic == null) {
			message.reply("I couldn't find any data to analyse!").mentionRepliedUser(false).queue();
			return;
		}
		MessageEmbed toxicityEmbed = new EmbedBuilder()
			.setColor(getColour(howToxic))
			.setTitle("How toxic is " + message.getGuild().getName() + "?")
			.setDescription("I searched `" + howToxic.getTotalWordCount() + "` words. From this, I found `" + howToxic.getTokenCount() + "` words with meaning.")
			.addField("Positive words found", "```\n" + howToxic.getPositiveCount() + "\n```", true)
			.addField("Negative words found",  "```\n" + howToxic.getNegativeCount() + "\n```", true)
			.addField("Total Score (higher is better!)",  "```\n" + howToxic.getScore() + "\n```", true)
			.addField("Average Score (higher is better!)",  "```\n" + howToxic.getAverageScore() + "\n```", true)
			.addField("Adjusted Score (higher is better!)",  "```\n" + howToxic.getAdjustedScore() + "\n```", true)
			.setFooter(getMessage(howToxic))
			.build();
		message.replyEmbeds(toxicityEmbed).mentionRepliedUser(false).queue();
	}
	
	private String getMessage(Toxicity howToxic) {
		return howToxic.getAdjustedScore() < -2 ? "You are all terrible people go and sit in the corner and think about your actions"
				: howToxic.getAdjustedScore() < 2 ? "There's nice people in here somewhere..." 
				: "You are all delightful people!";
	}

	private Color getColour(Toxicity howToxic) {
		return howToxic.getAdjustedScore() < -2 ? Color.RED 
				: howToxic.getAdjustedScore() < 2 ? Color.ORANGE
				: Color.GREEN;
	}

	@Override
	public String getName() {
		return "howtoxicisthisserver";
	}

	@Override
	public String getUsage() {
		return "howtoxicisthisserver";
	}

	@Override
	public String getDescription() {
		return "Want to see how toxic your server is? You may not like the results...";
	}

}