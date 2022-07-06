package main.java.de.voidtech.alison.commands;

import java.awt.Color;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import main.java.de.voidtech.alison.annotations.Command;
import main.java.de.voidtech.alison.entities.AlisonWord;
import main.java.de.voidtech.alison.service.WordService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

@Command
public class TopFiveCommand extends AbstractCommand
{
    @Autowired
    private WordService wordService;
    
    @Override
    public void execute(final Message message, final List<String> args) {
        final List<AlisonWord> topFive = wordService.getTopFiveWords(message.getAuthor().getId());
        final long wordCount = wordService.getWordCountForUser(message.getAuthor().getId());
        String result = "";
        for (final AlisonWord word : topFive) {
            result = result + word.getWord() + " / " + word.getNext() + " - `" + word.getFrequency() + "`\n";
        }
        final MessageEmbed topFiveEmbed = new EmbedBuilder()
        		.setColor(Color.ORANGE)
        		.setTitle(message.getAuthor().getName() + "'s top 5 words")
        		.setDescription(result)
        		.setFooter("I have learnt " + wordCount + " words from you!")
        		.build();
        message.replyEmbeds(topFiveEmbed).mentionRepliedUser(false).queue();
    }
    
    @Override
    public String getName() {
        return "top";
    }

	@Override
	public String getUsage() {
		return "top";
	}

	@Override
	public String getDescription() {
		return "Allows you to see your top 5 most common word combinations!";
	}
}
