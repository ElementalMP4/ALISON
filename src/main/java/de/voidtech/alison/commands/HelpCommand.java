package main.java.de.voidtech.alison.commands;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import main.java.de.voidtech.alison.GlobalConstants;
import main.java.de.voidtech.alison.annotations.Command;
import main.java.de.voidtech.alison.util.CustomCollectors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

@Command
public class HelpCommand extends AbstractCommand {

	@Autowired
	private List<AbstractCommand> commands;
	
	@Override
	public void execute(Message message, List<String> args) {
		if (!commands.contains(this)) commands.add(this);
		if (args.size() == 0) showAllCommands(message);
		else {
			AbstractCommand commandOpt = commands.stream()
					.filter(c -> c.getName().equals(args.get(0)) | c.getShortName().equals(args.get(0)))
					.collect(CustomCollectors.toSingleton());
			if (commandOpt == null) message.reply("I couldn't find that command :(").mentionRepliedUser(false).queue();
			else showCommandHelp(commandOpt, message);
		}
	}

	private void showAllCommands(Message message) {
		String commandsList = String.join("\n", commands.stream().map(c -> addFormatting(c.getName())).collect(Collectors.toList()));
		MessageEmbed helpEmbed = new EmbedBuilder()
				.setTitle("ALISON Commands")
				.setColor(Color.ORANGE)
				.setDescription(commandsList)
				.setThumbnail(message.getJDA().getSelfUser().getAvatarUrl())
				.setFooter(GlobalConstants.VERSION, message.getJDA().getSelfUser().getAvatarUrl())
				.build();
		message.replyEmbeds(helpEmbed).mentionRepliedUser(false).queue();
	}

	private String addFormatting(String input) {
		return "```\n" + input + "\n```";
	}
	
	private void showCommandHelp(AbstractCommand command, Message message) {
		MessageEmbed helpEmbed = new EmbedBuilder()
				.setTitle("How to use " + command.getName())
				.setColor(Color.ORANGE)
				.setDescription(addFormatting(command.getDescription()))
				.addField("Usage", addFormatting(command.getUsage()), true)
				.addField("Name", addFormatting(command.getName()), true)
				.addField("Short name", addFormatting(command.getShortName()), true)
				.setThumbnail(message.getJDA().getSelfUser().getAvatarUrl())
				.setFooter(GlobalConstants.VERSION, message.getJDA().getSelfUser().getAvatarUrl())
				.build();
		message.replyEmbeds(helpEmbed).mentionRepliedUser(false).queue();
	}

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public String getUsage() {
		return "help [command]";
	}

	@Override
	public String getDescription() {
		return "Shows you how to use all the commands!";
	}

	@Override
	public String getShortName() {
		return "h";
	}
	
	@Override
	public boolean isHidden() {
		return false;
	}

}
