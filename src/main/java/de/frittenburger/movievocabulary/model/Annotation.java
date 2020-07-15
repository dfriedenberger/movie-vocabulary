package de.frittenburger.movievocabulary.model;

public class Annotation {
	
	private String key;
	private String type;


	private int level;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "Annotation [key=" + key + ", type=" + type + ", level=" + level
				+ "]";
	}
}
