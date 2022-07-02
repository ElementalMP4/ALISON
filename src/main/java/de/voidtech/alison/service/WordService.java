package main.java.de.voidtech.alison.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import main.java.de.voidtech.alison.entities.AlisonWord;

@Service
public class WordService
{
    @Autowired
    private SessionFactory sessionFactory;
    
    public void clearUser(final String userID) {
    	try (Session session = sessionFactory.openSession()) {
            session.getTransaction().begin();
            session.createQuery("DELETE FROM AlisonWord WHERE pack = :userID").setParameter("userID", (Object)userID).executeUpdate();
            session.getTransaction().commit();
        }
    }
    
    @SuppressWarnings("unchecked")
	public List<AlisonWord> getTopFiveWords(final String userID) {
        try (Session session = sessionFactory.openSession()) {
            final List<AlisonWord> list = (List<AlisonWord>) session.createQuery("FROM AlisonWord WHERE pack = :pack ORDER BY frequency")
            		.setParameter("pack", userID)
            		.setMaxResults(5)
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
    
    public String generatePromptedSentence(final String iD, final String prompt) {
        final List<AlisonWord> startWords = (List<AlisonWord>)this.getWordList(iD, prompt);
        if (startWords == null) {
            return "I couldn't use that prompt :(";
        }
        final List<String> results = new ArrayList<String>();
        AlisonWord next;
        List<AlisonWord> choices;
        for (next = this.getRandomWord(startWords); !next.isStopWord(); next = this.getRandomWord(choices)) {
            results.add(next.getWord());
            final List<AlisonWord> potentials = (List<AlisonWord>)this.getWordList(iD, next.getNext());
            choices = new ArrayList<AlisonWord>();
            for (final AlisonWord word : potentials) {
                for (int i = 0; i < word.getFrequency(); ++i) {
                    choices.add(word);
                }
            }
        }
        results.add(next.getWord());
        final String result = String.join(" ", results);
        return result;
    }
    
    public String generateRandomSentence(final String iD) {
        final AlisonWord start = this.getRandomWord(iD);
        if (start == null) {
            return "I couldn't imitate that person :(";
        }
        final String sentence = this.generatePromptedSentence(iD, start.getWord());
        return sentence;
    }
}
