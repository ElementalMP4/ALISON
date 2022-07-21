package main.java.de.voidtech.alison.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import main.java.de.voidtech.alison.annotations.Command;
import main.java.de.voidtech.alison.service.PrivacyService;
import main.java.de.voidtech.alison.service.WordService;
import main.java.de.voidtech.alison.util.ButtonConsumer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;

@Command
public class OptOutCommand extends AbstractCommand {
	
	@Autowired
	private EventWaiter waiter;
	
	@Autowired
	private PrivacyService privacyService;
	
	@Autowired
	private WordService wordService;
	
	private static final String TRUE_EMOTE = "\u2705";
	private static final String FALSE_EMOTE = "\u274C";

	private void getAwaitedButton(Message message, String question, List<Component> actions, Consumer<ButtonConsumer> result) {
        Message msg = message.reply(question).setActionRow(actions).mentionRepliedUser(false).complete();
        waiter.waitForEvent(ButtonClickEvent.class,
                e -> e.getUser().getId().equals(message.getAuthor().getId()),
				e -> result.accept(new ButtonConsumer(e, msg)), 30, TimeUnit.SECONDS,
                () -> message.getChannel().sendMessage("Timed out waiting for reply").queue());
    }
	
	@Override
	public void execute(Message message, List<String> args) {
		if (!privacyService.userIsIgnored(message.getAuthor().getId())) {
			privacyService.optOut(message.getAuthor().getId());	
			getAwaitedButton(message, "Would you also like to delete any collected data?", createTrueFalseButtons(), result -> {
				result.getButton().deferEdit().queue();
				switch (result.getButton().getComponentId()) {
				case "YES":
					wordService.clearUser(message.getAuthor().getId());
					result.getMessage().editMessage("Data cleared!").queue();
					break;
				case "NO":
					result.getMessage().editMessage("Data has been left alone for now. Use the `clear` command if you change your mind!").queue();
					break;
				}
			});			
			
		} else message.reply("You have already chosen to opt out!").mentionRepliedUser(false).queue();
	}
	
	private List<Component> createTrueFalseButtons() {
		List<Component> components = new ArrayList<>();
		components.add(Button.secondary("YES", TRUE_EMOTE));
		components.add(Button.secondary("NO", FALSE_EMOTE));
		return components;
	}

	@Override
	public String getName() {
		return "optout";
	}

	@Override
	public String getUsage() {
		return "optout";
	}

	@Override
	public String getDescription() {
		return "Stops ALISON from learning from your messages. By default, you will be opted in."
				+ " You can use the optin command to let ALISON learn from you, and the clear command to delete all your learnt words.";
	}

	@Override
	public String getShortName() {
		return "out";
	}
	
	@Override
	public boolean isHidden() {
		return false;
	}

}
