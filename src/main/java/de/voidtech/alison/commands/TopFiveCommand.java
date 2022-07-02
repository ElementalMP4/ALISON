// 
// Decompiled by Procyon v0.5.36
// 

package main.java.de.voidtech.alison.commands;

import java.util.Iterator;
import net.dv8tion.jda.api.entities.MessageEmbed;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import main.java.de.voidtech.alison.entities.AlisonWord;
import java.util.List;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import main.java.de.voidtech.alison.service.WordService;
import main.java.de.voidtech.alison.annotations.Command;

@Command
public class TopFiveCommand extends AbstractCommand
{
    @Autowired
    private WordService wordService;
    
    @Override
    public void execute(final Message message, final List<String> args) {
        final List<AlisonWord> topFive = (List<AlisonWord>)this.wordService.getTopFiveWords(message.getAuthor().getId());
        String result = "";
        for (final AlisonWord word : topFive) {
            result = result + word.getNext() + " - `" + word.getFrequency() + "`\n";
        }
        final MessageEmbed topFiveEmbed = new EmbedBuilder().setColor(Color.ORANGE).setTitle(message.getAuthor().getName() + "'s top 5 words").setDescription((CharSequence)result).build();
        message.replyEmbeds(topFiveEmbed, new MessageEmbed[0]).mentionRepliedUser(false).queue();
    }
    
    @Override
    public String getName() {
        return "top";
    }
}
