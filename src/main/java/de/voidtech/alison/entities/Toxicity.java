package main.java.de.voidtech.alison.entities;

public class Toxicity {
	
	private int positive;
	private int negative;
	private int score;
	private int totalWords;
	private int tokens;
	private float averageScore;
	
	public Toxicity(int positive, int negative, int score, int totalWords, int tokens) {
		this.positive = positive;
		this.negative = negative;
		this.score = score;
		this.totalWords = totalWords;
		this.tokens = tokens;
		this.averageScore = score / tokens;
	}
	
	public float getAverageScore() {
		return this.averageScore;
	}
	
	public int getNegatives() {
		return this.negative;
	}
	
	public int getPositives() {
		return this.positive;
	}
	
	public int getScore() {
		return this.score;
	}
	
	public int getTokenCount() {
		return this.tokens;
	}
	
	public int getTotalWordCount() {
		return this.totalWords;
	}
}
