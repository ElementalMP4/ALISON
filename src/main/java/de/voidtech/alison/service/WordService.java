package main.java.de.voidtech.alison.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import main.java.de.voidtech.alison.entities.AfinnWord;
import main.java.de.voidtech.alison.entities.AlisonWord;
import main.java.de.voidtech.alison.entities.Toxicity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;

@Service
public class WordService
{
	private static final Logger LOGGER = Logger.getLogger(WordService.class.getSimpleName());
	private static List<AfinnWord> AfinnData = new ArrayList<AfinnWord>();
	
    @Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
    private WebhookManager webhookManager;
    
    @EventListener(ApplicationReadyEvent.class)
    private void loadAfinn() {
    	LOGGER.log(Level.INFO, "Loading AFINN dataset...");
    	String data = getAfinnData();
    	if (data == null) LOGGER.log(Level.SEVERE, "Couldn't load AFINN data!");
    	else processAfinn(data);
    }

	private void processAfinn(String dataset) {
		List<String> lines = Arrays.asList(dataset.split("\n"));
		for (String line : lines) {
			int score = Integer.valueOf(line.substring(line.length() - 2).trim());
			String text = line.substring(0, line.length() - 2).trim();
			AfinnData.add(new AfinnWord(text, score));
		}
		LOGGER.log(Level.INFO, "Loaded " + AfinnData.size() + " AFINN words");
	}
	
	private List<String> tokenise(String input) {
		return Arrays.asList(input.toLowerCase().split(" ")).stream().map(i -> i.replaceAll("([^a-zA-Z])", "")).collect(Collectors.toList());
	}
	
	private String alisonWordListToString(List<AlisonWord> words) {
		List<AlisonWord> everySingleGoshDarnWord = new ArrayList<AlisonWord>();
		words.stream().forEach(word -> {
			for (int i = 0; i < word.getFrequency(); i++) everySingleGoshDarnWord.add(word);
		});
		return String.join(" ", everySingleGoshDarnWord.stream().map(AlisonWord::getWord).collect(Collectors.toList()));
	}
	
	public Toxicity scoreUser(String userID) {
		List<AlisonWord> words = getALotOfWordsForuser(userID);
		if (words.isEmpty()) return null;
		return scoreString(alisonWordListToString(words));
	}
	
	public Toxicity scoreServer(Guild guild) {
		List<AlisonWord> words = getAllWordsForServer(guild.getMembers().stream().map(Member::getId).collect(Collectors.toList()));
		if (words.isEmpty()) return null;
		return scoreString(alisonWordListToString(words));
	}
	
    @SuppressWarnings("unchecked")
	private List<AlisonWord> getAllWordsForServer(List<String> members) {
    	try (Session session = sessionFactory.openSession()) {
            final List<AlisonWord> list = (List<AlisonWord>) session.createQuery("FROM AlisonWord WHERE pack IN :memberList")
            		.setParameter("memberList", members)
            		.list();
            return list;
        }
    }
	
	public Toxicity scoreString(String input) {
		List<String> words = tokenise(input);
		List<AfinnWord> wordsWithScores = new ArrayList<AfinnWord>();
		AfinnData.stream().forEach(word -> {
			if (words.contains(word.getWord())) {
				for (int i = 0; i < Collections.frequency(words, word.getWord()); i++) {
					wordsWithScores.add(word);
				}
			}
		});
		List<AfinnWord> positives = new ArrayList<AfinnWord>();
		List<AfinnWord> negatives = new ArrayList<AfinnWord>();
		for (AfinnWord word : wordsWithScores) {
			if (word.getScore() < 0) negatives.add(word);
			else positives.add(word);	
		}
		return new Toxicity(positives, negatives, words);
	}

	private String getAfinnData() {
        try {
        	StringBuilder resultBuilder = new StringBuilder();
    		InputStream dataInStream = getClass().getClassLoader().getResourceAsStream("AFINN.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(dataInStream));
            String line;
			while ((line = br.readLine()) != null) {
				resultBuilder.append(line + "\n");
			}
			return resultBuilder.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return null;
    }
    
    public void clearUser(final String userID) {
    	try (Session session = sessionFactory.openSession()) {
            session.getTransaction().begin();
            session.createQuery("DELETE FROM AlisonWord WHERE pack = :userID").setParameter("userID", userID).executeUpdate();
            session.getTransaction().commit();
        }
    }
    
    @SuppressWarnings("unchecked")
	public List<AlisonWord> getTopFiveWords(final String userID) {
        try (Session session = sessionFactory.openSession()) {
            final List<AlisonWord> list = (List<AlisonWord>) session.createQuery("FROM AlisonWord WHERE pack = :pack ORDER BY frequency DESC")
            		.setParameter("pack", userID)
            		.setMaxResults(5)
            		.list();
            return list;
        }
    }
    
    @SuppressWarnings("unchecked")
	private List<AlisonWord> getALotOfWordsForuser(String userID) {
        try (Session session = sessionFactory.openSession()) {
            final List<AlisonWord> list = (List<AlisonWord>) session.createQuery("FROM AlisonWord WHERE pack = :pack")
            		.setParameter("pack", userID)
            		.list();
            return list;
        }
    }
    
    private AlisonWord getRandomWord(final List<AlisonWord> words) {
        return words.get(new Random().nextInt(words.size()));
    }
    
    public AlisonWord getWordAllDetails(final String pack, final String word, final String next) {
    	try (Session session = sessionFactory.openSession()) {
            final AlisonWord alisonWord = (AlisonWord) session.createQuery("FROM AlisonWord WHERE pack = :pack AND word = :word AND next = :next")
            		.setParameter("pack", pack)
            		.setParameter("word", word)
            		.setParameter("next", next)
            		.uniqueResult();
            return alisonWord;
        }
    }
    
    @SuppressWarnings("unchecked")
	public List<AlisonWord> getWordList(final String pack, final String word) {
    	try (Session session = sessionFactory.openSession()) {
            final List<AlisonWord> list = (List<AlisonWord>) session.createQuery("FROM AlisonWord WHERE pack = :pack AND word = :word")
            		.setParameter("pack", pack)
            		.setParameter("word", word)
            		.list();
            return list;
        }
    }
    
    public AlisonWord getRandomWord(final String pack) {
    	try (Session session = sessionFactory.openSession()) {
            final AlisonWord alisonWord = (AlisonWord) session.createQuery("FROM AlisonWord WHERE pack = :pack ORDER BY RANDOM()")
            		.setParameter("pack", pack)
            		.setMaxResults(1)
            		.uniqueResult();
            return alisonWord;
        }
    }
    
    public void updateWord(final AlisonWord word) {
    	try (Session session = sessionFactory.openSession()) {
            session.getTransaction().begin();
            session.saveOrUpdate(word);
            session.getTransaction().commit();
        }
    }
    
    private void saveOrUpdate(final String ID, final String token, final String follow) {
        AlisonWord word = getWordAllDetails(ID, token, follow);
        if (word == null) word = new AlisonWord(ID, token, follow);
        else word.incrementFrequency();
        updateWord(word);
    }
    
    public void learn(final String ID, final String content) {
        final List<String> tokens = Arrays.asList(content.split(" "));
        for (int i = 0; i < tokens.size(); ++i) {
            if (i == tokens.size() - 1) saveOrUpdate(ID, tokens.get(i), "StopWord");
            else saveOrUpdate(ID, tokens.get(i), tokens.get(i + 1));
        }
    }
    
    public void generatePromptedSentence(User user, Message message, String prompt) {
        List<AlisonWord> startWords = getWordList(user.getId(), prompt);
        if (startWords == null) {
        	message.reply("I couldn't use that prompt :(").mentionRepliedUser(false).queue();
        }
        List<String> results = new ArrayList<String>();
        AlisonWord next;
        List<AlisonWord> choices;
        for (next = this.getRandomWord(startWords); !next.isStopWord(); next = this.getRandomWord(choices)) {
            results.add(next.getWord());
            List<AlisonWord> potentials = getWordList(user.getId(), next.getNext());
            choices = new ArrayList<AlisonWord>();
            for (AlisonWord word : potentials) {
                for (int i = 0; i < word.getFrequency(); ++i) {
                    choices.add(word);
                }
            }
        }
        results.add(next.getWord());
        String result = String.join(" ", results);
        Webhook webhook = webhookManager.getOrCreateWebhook(message.getTextChannel(), "Alison", message.getJDA().getSelfUser().getId());
        webhookManager.postMessage(result, user.getAvatarUrl(), user.getName(), webhook);
    }
    
    public void generateRandomSentence(User user, Message message) {
        AlisonWord start = getRandomWord(user.getId());
        if (start == null) {
            message.reply("I couldn't imitate that person :(").mentionRepliedUser(false).queue();
        } else {
        	generatePromptedSentence(user, message, start.getWord());
        }
    }

	public long getWordCountForUser(String id) {
		try(Session session = sessionFactory.openSession())
		{
			@SuppressWarnings("rawtypes")
			Query query = session.createQuery("SELECT COUNT(*) FROM AlisonWord WHERE pack = :pack").setParameter("pack", id);
			long count = (long) query.uniqueResult();
			session.close();
			return count;
		}
	}

	public long getTotalWords() {
		try(Session session = sessionFactory.openSession())
		{
			@SuppressWarnings("rawtypes")
			Query query = session.createQuery("SELECT COUNT(*) FROM AlisonWord");
			long count = (long) query.uniqueResult();
			session.close();
			return count;
		}
	}
}
