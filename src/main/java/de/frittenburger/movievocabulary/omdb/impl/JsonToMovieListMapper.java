package de.frittenburger.movievocabulary.omdb.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;

import de.frittenburger.movievocabulary.omdb.model.OMDbMovie;

public class JsonToMovieListMapper implements Function<JsonNode, List<OMDbMovie>> {

	@Override
	public List<OMDbMovie> apply(JsonNode tree) {
		
		List<OMDbMovie> movies = new ArrayList<>();
		
		JsonNode node = tree.get("Search");
		if(node != null && node.isArray())
			for(JsonNode e : node)
			{
				OMDbMovie movie = new OMDbMovie();
				movie.setTitle(e.get("Title").asText());
				movie.setImdbID(e.get("imdbID").asText());
				movie.setYear(e.get("Year").asText());
				movie.setPoster(e.get("Poster").asText());
				movies.add(movie);
			}
		
		
		return movies;
	}

}
