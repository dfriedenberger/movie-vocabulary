package de.frittenburger.movievocabulary.convert.interfaces;

import java.io.IOException;

import de.frittenburger.movievocabulary.model.Paragraph;



public interface NlpService {


	Paragraph parse(String language, String text) throws UnsupportedOperationException, IOException;


}
