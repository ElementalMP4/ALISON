package main.java.de.voidtech.alison.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import main.java.de.voidtech.alison.annotations.Command;
import main.java.de.voidtech.alison.service.ConfigService;
import main.java.de.voidtech.alison.service.WordService;
import main.java.de.voidtech.alison.util.CustomCollectors;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;

@Command
public class LoadFileCommand extends AbstractCommand {

	@Autowired
	private ConfigService config;
	
	@Autowired
	private WordService wordService;
	
	private static final Logger LOGGER = Logger.getLogger(LoadFileCommand.class.getSimpleName());
	
	@Override
	public void execute(Message message, List<String> args) {
		if (!config.getMaster().equals(message.getAuthor().getId())) {
			message.reply("Only my creator is allowed to use this command!").mentionRepliedUser(false).queue();
			return;
		}
		if (message.getAttachments().isEmpty()) {
			message.reply("You need to upload a text file, moron").mentionRepliedUser(false).queue();
			return;
		}
		Attachment textFileAttachment = message.getAttachments()
				.stream()
				.filter(a -> a.getFileExtension().equals("txt"))
				.collect(CustomCollectors.toSingleton());
		try {
			File textFile =	textFileAttachment.downloadToFile().get();
			List<String> content = fileToText(textFile);
			if (content.isEmpty()) {
				message.reply("Something went wrong, I couldn't process that!").mentionRepliedUser(false).queue();
				return;
			}
			LOGGER.log(Level.INFO, "About to load " + content.size() + " lines");
			int loaded = 0;
			for (String line : content) {
				loaded++;
				wordService.learn(textFileAttachment.getFileName(), line);
				LOGGER.log(Level.INFO, "Loaded " + loaded + "/" + content.size() + " (" + ((loaded/content.size()) * 100) + ")");
				
			};
			textFile.delete();
			message.reply("I've added this file. Use the `craft` command with the pack name `" + textFileAttachment.getFileName() + "`")
				.mentionRepliedUser(false).queue();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	private List<String> fileToText(File textFile) {
		List<String> lines = new ArrayList<String>();
		try {
        	BufferedReader br = new BufferedReader(new FileReader(textFile));
        	String line;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

	@Override
	public String getName() {
		return "loadfile";
	}

	@Override
	public String getShortName() {
		return "lf";
	}

	@Override
	public String getUsage() {
		return "loadfile [uploaded text file]";
	}

	@Override
	public String getDescription() {
		return "Allows a file to be uploaded to ALISON's database";
	}
	
	@Override
	public boolean isHidden() {
		return true;
	}

}
