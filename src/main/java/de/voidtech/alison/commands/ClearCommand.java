// 
// Decompiled by Procyon v0.5.36
// 

package main.java.de.voidtech.alison.commands;

import java.util.List;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import main.java.de.voidtech.alison.service.WordService;
import main.java.de.voidtech.alison.annotations.Command;

@Command
public class ClearCommand extends AbstractCommand
{
    @Autowired
    private WordService wordService;
    
    @Override
    public void execute(final Message message, final List<String> args) {
        this.wordService.clearUser(message.getAuthor().getId());
        message.reply((CharSequence)"Your data has been erased").mentionRepliedUser(false).queue();
    }
    
    @Override
    public String getName() {
        return "clear";
    }
}
