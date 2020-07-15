package de.frittenburger.movievocabulary.interfaces;

public interface AnnotationWrapper {

	public static final String IGNORE = "I:IGNORE";
	public static final String UNKNOWN = "UNKNOWN";

	//Typen
	public static final String T_VERB = "T:VERB";

	//Entitaeten
	public static final String E_LOCATION = "E:LOCATION";
	public static final String E_PERSON = "E:PERSON";

	
	String map(String pos, String ne);

}
