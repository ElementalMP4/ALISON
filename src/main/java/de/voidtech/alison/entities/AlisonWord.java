package main.java.de.voidtech.alison.entities;

import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "alisonword")
public class AlisonWord
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column
    private String pack;
    
    @Column
    private String word;
    
    @Column
    private String next;
    
    @Column
    private int frequency;
    
    @Deprecated
    AlisonWord() {
    }
    
    public AlisonWord(String pack, String word, String next) {
        this.pack = pack;
        this.word = word;
        this.next = next;
        this.frequency = 1;
    }
    
    public void incrementFrequency() {
        ++this.frequency;
    }
    
    public String getWord() {
        return this.word;
    }
    
    public String getNext() {
        return this.next;
    }
    
    public int getFrequency() {
        return this.frequency;
    }
    
    public boolean isStopWord() {
        return this.next.equals("StopWord");
    }
}
