package de.frittenburger.movievocabulary.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class Sentence {
	
	private String text;
	
	@JsonInclude(Include.NON_DEFAULT)
	private boolean validated;
	
	private List<Token> tokens;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	
	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	public List<Token> getTokens() {
		return tokens;
	}

	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}

	@Override
	public String toString() {
		return "Sentence [text=" + text + ", tokens=" + tokens.size() + "]";
	}


	
	
}
