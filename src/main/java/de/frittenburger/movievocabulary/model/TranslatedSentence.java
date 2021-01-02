package de.frittenburger.movievocabulary.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TranslatedSentence {
	
	private List<Token> tokens;
	private String text;

	private Boolean validated;

	private Map<String,String> translation = new LinkedHashMap<>();
	private int readingIndex;
	
	
	public List<Token> getTokens() {
		return tokens;
	}
	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
	public Boolean getValidated() {
		return validated;
	}
	
	public void setValidated(Boolean validated) {
		this.validated = validated;
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
