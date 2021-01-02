package de.frittenburger.movievocabulary.interfaces;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.frittenburger.movievocabulary.model.IMDbId;
import de.frittenburger.movievocabulary.model.Language;
import de.frittenburger.movievocabulary.model.MovieMetadata;
import de.frittenburger.movievocabulary.model.Paragraph;
import de.frittenburger.movievocabulary.model.PoFile;

public interface MovieDatabase {

	void create(IMDbId iMDbId) throws IOException;
	boolean exists(IMDbId iMDbId);

	MovieMetadata readMetadata(IMDbId id) throws IOException;
	void updateMetadata(IMDbId id, MovieMetadata metadata) throws IOException;

	
	List<Paragraph> readParagraphs(IMDbId id, Language language) throws IOException;
	void updateParagraphs(IMDbId id, Language language,List<Paragraph> paragraphs) throws IOException;

	
	PoFile readTranslation(IMDbId iMDbId, Language sourceLanguage, Language targetLanguage) throws IOException;
	void updateTranslation(IMDbId iMDbId, Language sourceLanguage, Language targetLanguage,PoFile translation) throws IOException;

	
	
	File getBaseFolder(IMDbId iMDbId) throws IOException;
	File getInboundFolder(IMDbId iMDbId) throws IOException;


}
