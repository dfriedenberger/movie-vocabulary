package de.frittenburger.movievocabulary.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class TranslatedSentence {
	
	private Sentence sentence;
	private Map<String,String> translation = new LinkedHashMap<>();
	private int readingIndex;
	
	
	public Sentence getSentence() {
		return sentence;
	}
	public void setSentence(Sentence sentence) {
		this.sentence = sentence;
	}
	public Map<String, String> getTranslation() {
		return translation;
	}
	public void setTranslation(Map<String, String> translation) {
		this.translation = translation;
	}
	public int getReadingIndex() {
		return readingIndex;
	}
	public void setReadingIndex(int readingIndex) {
		this.readingIndex = readingIndex;
	}
	
	
}
