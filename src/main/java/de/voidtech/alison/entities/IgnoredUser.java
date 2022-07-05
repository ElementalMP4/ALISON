package main.java.de.voidtech.alison.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ignoreduser")
public class IgnoredUser 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column
	private String userID;
	
	@Deprecated
	IgnoredUser() {
	}
	
	public IgnoredUser(String user)
	{
		this.userID = user;
	}
	
	public String getUserID() {
		return this.userID;
	}
}
