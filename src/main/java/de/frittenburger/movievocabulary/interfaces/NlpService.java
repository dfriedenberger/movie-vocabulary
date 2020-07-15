package de.frittenburger.movievocabulary.interfaces;

import de.frittenburger.movievocabulary.model.Paragraph;



public interface NlpService {

	Paragraph parse(String text);


}
