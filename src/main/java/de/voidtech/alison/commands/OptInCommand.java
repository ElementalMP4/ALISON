package main.java.de.voidtech.alison.commands;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import main.java.de.voidtech.alison.annotations.Command;
import main.java.de.voidtech.alison.service.PrivacyService;
import net.dv8tion.jda.api.entities.Message;

@Command
public class OptInCommand extends AbstractCommand {

	@Autowired
	private PrivacyService privacyService;
	
	@Override
	public void execute(Message message, List<String> args) {
		String ID = message.getAuthor().getId();
		if (privacyService.userIsIgnored(ID)) {
			message.reply("You have been re-opted in to the learning program! I will learn from your messages again!")
				.mentionRepliedUser(false).queue();
			privacyService.optIn(ID);
		} else {
			message.reply("You have already opted in to the learning program! (Users are opted in by default!)")
				.mentionRepliedUser(false).queue();
		}
	}

	@Override
	public String getName() {
		return "optin";
	}

	@Override
	public String getUsage() {
		return "optin";
	}

	@Override
	public String getDescription() {
		return "Allows ALISON to learn from your messages. By default, you will be opted in."
				+ " You can use the optout command to stop ALISON from learning from you, and the clear command to delete all your learnt words.";
	}

	@Override
	public String getShortName() {
		return "in";
	}
	
	@Override
	public boolean isHidden() {
		return false;
	}

}
