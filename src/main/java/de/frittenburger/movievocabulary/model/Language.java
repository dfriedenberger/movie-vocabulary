package de.frittenburger.movievocabulary.model;

public class Language {

	
	public final static Language spanish = new Language("es","spanish");
	public final static Language english = new Language("en","english");
	public final static Language german = new Language("de","german");

	private final String shortName;
	private final String longName;

	private Language(String shortName,String longName)
	{
		this.shortName = shortName;
		this.longName = longName;
	}
	
	public String getLongName() {
		return longName;
	}
	public String getShortName() {
		return shortName;
	}

	public static Language parse(String lang) {

		
		switch(lang)
		{
		case "es":
			return spanish;
		case "de":
			return german;
		case "en":
			return english;
		}
		
		throw new RuntimeException(lang +" not supported");
	}
}
