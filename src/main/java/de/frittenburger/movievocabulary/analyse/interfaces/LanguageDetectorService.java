package de.frittenburger.movievocabulary.analyse.interfaces;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public interface LanguageDetectorService {


	String detect(List<String> examples);

	String detect(String text);

	String detect(File file, Charset charset) throws IOException;

}
