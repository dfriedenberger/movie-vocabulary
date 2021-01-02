package de.frittenburger.movievocabulary.translate.interfaces;

import java.io.IOException;
import java.util.List;

import de.frittenburger.movievocabulary.model.Language;

public interface TranslateService {

	List<String> translate(List<String> text, Language source, Language target) throws IOException;

}
