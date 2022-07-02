// 
// Decompiled by Procyon v0.5.36
// 

package main.java.de.voidtech.alison.service;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import main.java.de.voidtech.alison.entities.AlisonWord;
import java.util.List;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

@Service
public class WordService
{
    @Autowired
    private SessionFactory sessionFactory;
    
    public void clearUser(final String userID) {
        final Session session = this.sessionFactory.openSession();
        try {
            session.getTransaction().begin();
            session.createQuery("DELETE FROM AlisonWord WHERE pack = :userID").setParameter("userID", (Object)userID).executeUpdate();
            session.getTransaction().commit();
            if (session != null) {
                session.close();
            }
        }
        catch (Throwable t) {
            if (session != null) {
                try {
                    session.close();
                }
                catch (Throwable exception) {
                    t.addSuppressed(exception);
                }
            }
            throw t;
        }
    }
    
    public List<AlisonWord> getTopFiveWords(final String userID) {
        final Session session = this.sessionFactory.openSession();
        try {
            final List list = session.createQuery("FROM AlisonWord WHERE pack = :pack ORDER BY frequency").setParameter("pack", (Object)userID).setMaxResults(5).list();
            if (session != null) {
                session.close();
            }
            return (List<AlisonWord>)list;
        }
        catch (Throwable t) {
            if (session != null) {
                try {
                    session.close();
                }
                catch (Throwable exception) {
                    t.addSuppressed(exception);
                }
            }
            throw t;
        }
    }
    
    private AlisonWord getRandomWord(final List<AlisonWord> words) {
        return words.get(new Random().nextInt(words.size()));
    }
    
    public AlisonWord getWordAllDetails(final String pack, final String word, final String next) {
        final Session session = this.sessionFactory.openSession();
        try {
            final AlisonWord alisonWord = (AlisonWord)session.createQuery("FROM AlisonWord WHERE pack = :pack AND word = :word AND next = :next").setParameter("pack", (Object)pack).setParameter("word", (Object)word).setParameter("next", (Object)next).uniqueResult();
            if (session != null) {
                session.close();
            }
            return alisonWord;
        }
        catch (Throwable t) {
            if (session != null) {
                try {
                    session.close();
                }
                catch (Throwable exception) {
                    t.addSuppressed(exception);
                }
            }
            throw t;
        }
    }
    
    public List<AlisonWord> getWordList(final String pack, final String word) {
        final Session session = this.sessionFactory.openSession();
        try {
            final List list = session.createQuery("FROM AlisonWord WHERE pack = :pack AND word = :word").setParameter("pack", (Object)pack).setParameter("word", (Object)word).list();
            if (session != null) {
                session.close();
            }
            return (List<AlisonWord>)list;
        }
        catch (Throwable t) {
            if (session != null) {
                try {
                    session.close();
                }
                catch (Throwable exception) {
                    t.addSuppressed(exception);
                }
            }
            throw t;
        }
    }
    
    public AlisonWord getRandomWord(final String pack) {
        final Session session = this.sessionFactory.openSession();
        try {
            final AlisonWord alisonWord = (AlisonWord)session.createQuery("FROM AlisonWord WHERE pack = :pack ORDER BY RANDOM()").setParameter("pack", (Object)pack).setMaxResults(1).uniqueResult();
            if (session != null) {
                session.close();
            }
            return alisonWord;
        }
        catch (Throwable t) {
            if (session != null) {
                try {
                    session.close();
                }
                catch (Throwable exception) {
                    t.addSuppressed(exception);
                }
            }
            throw t;
        }
    }
    
    public void updateWord(final AlisonWord word) {
        final Session session = this.sessionFactory.openSession();
        try {
            session.getTransaction().begin();
            session.saveOrUpdate((Object)word);
            session.getTransaction().commit();
            if (session != null) {
                session.close();
            }
        }
        catch (Throwable t) {
            if (session != null) {
                try {
                    session.close();
                }
                catch (Throwable exception) {
                    t.addSuppressed(exception);
                }
            }
            throw t;
        }
    }
    
    private void saveOrUpdate(final String ID, final String token, final String follow) {
        AlisonWord word = this.getWordAllDetails(ID, token, follow);
        if (word == null) {
            word = new AlisonWord(ID, token, follow);
        }
        else {
            word.incrementFrequency();
        }
        this.updateWord(word);
    }
    
    public void learn(final String ID, final String content) {
        final List<String> tokens = Arrays.asList(content.split(" "));
        for (int i = 0; i < tokens.size(); ++i) {
            if (i == tokens.size() - 1) {
                this.saveOrUpdate(ID, tokens.get(i), "StopWord");
            }
            else {
                this.saveOrUpdate(ID, tokens.get(i), tokens.get(i + 1));
            }
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
