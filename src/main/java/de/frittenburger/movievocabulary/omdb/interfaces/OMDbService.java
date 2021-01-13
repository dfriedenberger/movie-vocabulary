package de.frittenburger.movievocabulary.omdb.interfaces;

import java.io.IOException;
import java.util.List;

import de.frittenburger.movievocabulary.model.IMDbId;
import de.frittenburger.movievocabulary.omdb.model.OMDbMovie;
import de.frittenburger.movievocabulary.omdb.model.OMDbMovieInfo;

public interface OMDbService {

	List<OMDbMovie> search(String q) throws IOException;

	OMDbMovieInfo get(IMDbId iMDbId) throws IOException;
	
}
