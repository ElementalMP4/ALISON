package main.java.de.voidtech.alison.entities;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Toxicity {
	
	private List<AfinnWord> positives;
	private List<AfinnWord> negatives;
	private List<String> originalWords;
	private List<AfinnWord> tokens;
	
	private int score;
	private double averageScore;
	
	public Toxicity(List<AfinnWord> positives, List<AfinnWord> negatives, List<String> originalWords) {
		this.positives = positives;
		this.negatives = negatives;
		this.score = addAfinnScores(positives) + addAfinnScores(negatives);
		this.originalWords = originalWords;
		this.tokens = Stream.concat(this.positives.stream(), this.negatives.stream()).collect(Collectors.toList());
		this.averageScore = (double)score / (double)tokens.size();
	}
	
	private int addAfinnScores(List<AfinnWord> list) {
		return list.stream().map(AfinnWord::getScore).reduce(0, Integer::sum);
	}
	
	private int calculateAdjusted(int count, int subtractor, int multiplier) {
		return this.score + ((count - subtractor) * multiplier);
	}
	
	public int getAdjustedScore() {
		if (this.positives.size() == 0 & this.negatives.size() == 0) return 0;
		if (this.positives.size() == 0) return calculateAdjusted(this.negatives.size(), this.positives.size(), -1);
		if (this.negatives.size() == 0) return calculateAdjusted(this.positives.size(), this.negatives.size(), 1);
		if (this.positives.size() == this.negatives.size()) return this.score;
		return this.positives.size() < this.negatives.size() 
				? calculateAdjusted(this.negatives.size(), this.positives.size(), -1) 
				: calculateAdjusted(this.positives.size(), this.negatives.size(), 1);
	}
	
	public double getAverageScore() {
		return this.averageScore;
	}
	
	public int getNegativeCount() {
		return this.negatives.size();
	}
	
	public int getPositiveCount() {
		return this.positives.size();
	}
	
	public int getScore() {
		return this.score;
	}
	
	public int getTokenCount() {
		return this.tokens.size();
	}
	
	public int getTotalWordCount() {
		return this.originalWords.size();
	}
}
