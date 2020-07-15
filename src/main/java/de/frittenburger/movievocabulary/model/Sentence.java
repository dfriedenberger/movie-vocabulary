package de.frittenburger.movievocabulary.model;

import java.util.List;

public class Sentence {
	
	private List<Annotation> annotations;
	 
	private String text;
	private String token;

	public List<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<Annotation> annotations) {
		this.annotations = annotations;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return text;
	}

	

	


	
	
}
