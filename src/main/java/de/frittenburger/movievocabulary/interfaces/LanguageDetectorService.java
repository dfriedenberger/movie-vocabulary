package de.frittenburger.movievocabulary.interfaces;

import java.util.List;

public interface LanguageDetectorService {

	String detect(String string);

	String detect(List<String> examples);

}
