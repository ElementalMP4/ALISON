package main.java.de.voidtech.alison.commands;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import main.java.de.voidtech.alison.annotations.Command;
import main.java.de.voidtech.alison.service.PrivacyService;
import main.java.de.voidtech.alison.service.WordService;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.Result;

@Command
public class ImitateCommand extends AbstractCommand
{
    @Autowired
    private WordService wordService;
    
    @Autowired
    private PrivacyService privacyService;
    
    @Override
    public void execute(final Message message, final List<String> args) {
    	String ID;
    	if (args.isEmpty()) ID = message.getAuthor().getId();
    	else ID = args.get(0).replaceAll("([^0-9])", "");
    	
        if (ID.equals("")) {
            message.reply((CharSequence)"I couldn't find that user :(").mentionRepliedUser(false).queue();
            return;
        }
        
        Result<User> userResult = message.getJDA().retrieveUserById(ID).mapToResult().complete();
        if (userResult.isSuccess()) {
        	if (privacyService.userIsIgnored(ID)) {
        		message.reply("This user has chosen not to be imitated.").mentionRepliedUser(false).queue();
        		return;
        	}
            wordService.generateRandomSentenceForUser(userResult.get(), message);
        } else message.reply("User " + ID + " could not be found").mentionRepliedUser(false).queue();
    }
    
    @Override
    public String getName() {
        return "imitate";
    }

	@Override
	public String getUsage() {
		return "imitate [user mention or ID]";
	}

	@Override
	public String getDescription() {
		return "Allows you to use the power of ALISON to imitate someone! ALISON constantly learns from your messages,"
				+ " and when you use this command, she uses her knowledge to try and speak like you do!\n\n"
				+ "To stop ALISON from learning from you, use the optout command!";
	}

	@Override
	public String getShortName() {
		return "i";
	}
	
	@Override
	public boolean isHidden() {
		return false;
	}
}
