package de.frittenburger.movievocabulary.omdb.impl;

import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;

import de.frittenburger.movievocabulary.omdb.model.OMDbMovieInfo;

public class JsonToMovieInfoMapper implements Function<JsonNode, OMDbMovieInfo> {

	@Override
	public OMDbMovieInfo apply(JsonNode tree) {
		
		
		OMDbMovieInfo info = new OMDbMovieInfo();
		//{"Title":"Ready to Mingle",
		//"Year":"2019","Rated":"TV-MA","Released":"07 Jun 2019",
		//"Runtime":"95 min",
		//"Genre":"Comedy",
		//"Director":"Luis Javier Henaine",
		//"Writer":"Luis Javier Henaine, Alejandra Olvera Avila",
		//"Actors":"Cassandra Ciangherotti, Gabriela de la Garza, Irán Castillo, Sophie Alexander-Katz",
		//"Plot":"A wannabe bride seeks professional help to find a husband and, in the process, finds herself.",
		//"Language":"Spanish",
		//"Country":"Mexico",
		//"Awards":"1 win & 2 nominations.",
		//"Poster":"https://m.media-amazon.com/images/M/MV5BZjU3YzUzZjItNDAwYy00NTAyLWJmMGUtZjExNDNmYzE0MzhjXkEyXkFqcGdeQXVyMjM0MDgwMA@@._V1_SX300.jpg",
		//"Ratings":[{"Source":"Internet Movie Database","Value":"5.9/10"}],"Metascore":"N/A","imdbRating":"5.9","imdbVotes":"1,909","imdbID":"tt6736198","Type":"movie","DVD":"N/A","BoxOffice":"N/A","Production":"N/A","Website":"N/A","Response":"True"}

		info.setTitle(tree.get("Title").asText());
		info.setYear(tree.get("Year").asText());
		info.setRuntime(tree.get("Runtime").asText());
		info.setDirector(tree.get("Director").asText());
		info.setGenre(tree.get("Genre").asText());
		info.setActors(tree.get("Actors").asText());
		info.setCountry(tree.get("Country").asText());
		info.setLanguage(tree.get("Language").asText());
		info.setPoster(tree.get("Poster").asText());



		return info;
	}

}
