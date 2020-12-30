package de.frittenburger.movievocabulary.interfaces;

import java.io.File;
import java.io.IOException;

import de.frittenburger.movievocabulary.model.IMDbId;
import de.frittenburger.movievocabulary.model.MovieMetadata;

public interface MovieDatabase {

	void updateMetadata(IMDbId id, MovieMetadata metadata) throws IOException;

	File getInboundFolder(IMDbId iMDbId) throws IOException;

}
