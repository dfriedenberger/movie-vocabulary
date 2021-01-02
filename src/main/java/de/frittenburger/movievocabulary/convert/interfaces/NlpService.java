package de.frittenburger.movievocabulary.convert.interfaces;

import java.io.IOException;

import de.frittenburger.movievocabulary.model.Language;
import de.frittenburger.movievocabulary.model.Paragraph;



public interface NlpService {

	Paragraph parse(Language language, String text) throws UnsupportedOperationException, IOException;

}
