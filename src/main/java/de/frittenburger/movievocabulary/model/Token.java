package de.frittenburger.movievocabulary.model;

public class Token {
	
	  private String text;
	  private String namedEntity;
	  private String partOfSpeech;
	  private long level;
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getNamedEntity() {
		return namedEntity;
	}
	public void setNamedEntity(String namedEntity) {
		this.namedEntity = namedEntity;
	}
	public String getPartOfSpeech() {
		return partOfSpeech;
	}
	public void setPartOfSpeech(String partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}
	public long getLevel() {
		return level;
	}
	public void setLevel(long level) {
		this.level = level;
	}

}
