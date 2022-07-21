package main.java.de.voidtech.alison.commands;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import main.java.de.voidtech.alison.annotations.Command;
import main.java.de.voidtech.alison.service.ConfigService;
import main.java.de.voidtech.alison.service.WordService;
import net.dv8tion.jda.api.entities.Message;

@Command
public class CraftCommand extends AbstractCommand {

	@Autowired
	private ConfigService config;
	
	@Autowired
	private WordService wordService;
	
	@Override
	public void execute(Message message, List<String> args) {
		if (!config.getMaster().equals(message.getAuthor().getId())) {
			message.reply("Only my creator is allowed to use this command!").mentionRepliedUser(false).queue();
			return;
		}
		if (args.isEmpty()) {
			message.reply("You need to specify a pack to craft a message from you silly goose").mentionRepliedUser(false).queue();
			return;
		}
		String pack = args.get(0);
		String result = wordService.generateRandomSentenceForPack(pack);
		message.reply(result).mentionRepliedUser(false).queue();
	}

	@Override
	public String getName() {
		return "craft";
	}

	@Override
	public String getShortName() {
		return "cr";
	}

	@Override
	public String getUsage() {
		return "craft [pack]";
	}

	@Override
	public String getDescription() {
		return "Allows a sentence to be generated for any loaded pack";
	}

}
