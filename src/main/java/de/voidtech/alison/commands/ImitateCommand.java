// 
// Decompiled by Procyon v0.5.36
// 

package main.java.de.voidtech.alison.commands;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.Result;
import java.util.List;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import main.java.de.voidtech.alison.service.WordService;
import main.java.de.voidtech.alison.annotations.Command;

@Command
public class ImitateCommand extends AbstractCommand
{
    @Autowired
    private WordService wordService;
    
    @Override
    public void execute(final Message message, final List<String> args) {
    	String ID;
    	if (args.isEmpty()) ID = message.getAuthor().getId();
    	else ID = args.get(0).replaceAll("([^0-9])", "");
    	
        if (ID.equals("")) {
            message.reply((CharSequence)"I couldn't find that user :(").mentionRepliedUser(false).queue();
            return;
        }
        
        final Result<User> userResult = (Result<User>)message.getJDA().retrieveUserById(ID).mapToResult().complete();
        if (userResult.isSuccess()) {
            if (args.size() < 2) {
                message.reply(wordService.generateRandomSentence(ID)).mentionRepliedUser(false).queue();
            }
            else {
                message.reply(wordService.generatePromptedSentence(ID, args.get(1))).mentionRepliedUser(false).queue();
            }
        }
        else {
            message.reply("User " + ID + " could not be found").mentionRepliedUser(false).queue();
        }
    }
    
    @Override
    public String getName() {
        return "imitate";
    }
}
