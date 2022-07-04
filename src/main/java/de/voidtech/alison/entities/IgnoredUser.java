package main.java.de.voidtech.alison.entities;

import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "ignoreduser")
public class IgnoredUser
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column
    private String user;

    @Deprecated
    IgnoredUser() {
    }
    
    public IgnoredUser(String user) {
        this.user = user;
    }
}
