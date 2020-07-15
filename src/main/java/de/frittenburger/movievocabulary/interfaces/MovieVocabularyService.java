package de.frittenburger.movievocabulary.interfaces;

import java.io.IOException;
import java.io.InputStream;

public interface MovieVocabularyService {

	String importFile(String originalFilename, InputStream inputStream) throws IOException;

}
