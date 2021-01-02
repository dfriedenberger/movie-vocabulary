package de.frittenburger.movievocabulary.analyse.interfaces;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import de.frittenburger.movievocabulary.model.Language;

public interface LanguageDetectorService {


	Language detect(List<String> examples);

	Language detect(String text);

	Language detect(File file, Charset charset) throws IOException;

}
