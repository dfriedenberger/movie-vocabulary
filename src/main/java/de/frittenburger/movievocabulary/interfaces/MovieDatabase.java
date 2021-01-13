package de.frittenburger.movievocabulary.interfaces;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import de.frittenburger.movievocabulary.model.VideoId;
import de.frittenburger.movievocabulary.model.Language;
import de.frittenburger.movievocabulary.model.MovieMetadata;
import de.frittenburger.movievocabulary.model.Paragraph;
import de.frittenburger.movievocabulary.model.PoFile;

public interface MovieDatabase {

	List<VideoId> list();

	void create(VideoId iMDbId) throws IOException;
	boolean exists(VideoId iMDbId);

	
	
	File getBaseFolder(VideoId iMDbId) throws IOException;
	File getInboundFolder(VideoId iMDbId) throws IOException;
	
	
	MovieMetadata readMetadata(VideoId id) throws IOException;
	void updateMetadata(VideoId id, MovieMetadata metadata) throws IOException;

	
	List<Language> getParagraphLanguages(VideoId videoId) throws IOException;
	List<Paragraph> readParagraphs(VideoId id, Language language) throws IOException;
	void updateParagraphs(VideoId id, Language language,List<Paragraph> paragraphs) throws IOException;

	
	PoFile readTranslation(VideoId id, Language sourceLanguage, Language targetLanguage) throws IOException;
	void updateTranslation(VideoId id, Language sourceLanguage, Language targetLanguage,PoFile translation) throws IOException;

	Set<String> readValidation(VideoId id, Language sourceLanguage, Language targetLanguage) throws IOException;
	void updateValidation(VideoId id, Language sourceLanguage, Language targetLanguage, Set<String> validation) throws IOException;

}
