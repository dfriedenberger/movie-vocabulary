package de.frittenburger.movievocabulary.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Paragraph {

	private List<Sentence> sentences;
	
	

	public List<Sentence> getSentences() {
		return sentences;
	}

	public void setSentences(List<Sentence> sentences) {
		this.sentences = sentences;
	}
	
	public void addSentencesItem(Sentence sentence) {
		if(sentences == null)
			sentences = new ArrayList<>();
		sentences.add(sentence);
	}

	@Override
	public String toString() {
		return "" + sentences;
	}

	
	public List<Annotation> resolveAnnotations() {
		
		return sentences.stream().flatMap(sentence -> sentence.getAnnotations().stream())
		         .collect(Collectors.toList());
	}
	
}
