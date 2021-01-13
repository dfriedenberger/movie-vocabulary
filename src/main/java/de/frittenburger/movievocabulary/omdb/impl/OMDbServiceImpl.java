package de.frittenburger.movievocabulary.omdb.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.frittenburger.movievocabulary.model.IMDbId;
import de.frittenburger.movievocabulary.omdb.interfaces.OMDbService;
import de.frittenburger.movievocabulary.omdb.model.OMDbMovie;
import de.frittenburger.movievocabulary.omdb.model.OMDbMovieInfo;

public class OMDbServiceImpl implements OMDbService {

	
	private static final Logger logger = LogManager.getLogger(OMDbServiceImpl.class);

	private final String apiKey;
	private final String url;
	
	private final static Function<JsonNode,List<OMDbMovie>>  convertMovieList = new JsonToMovieListMapper();
	private final static Function<JsonNode,OMDbMovieInfo>  convertMovieInfo = new JsonToMovieInfoMapper();

	
	public OMDbServiceImpl(File configFile) throws IOException
	{
		Properties prop = new Properties();
		prop.load(new FileInputStream(configFile));
		this.apiKey = prop.getProperty("apikey");
		this.url = prop.getProperty("url");
	}
	
	@Override
	public List<OMDbMovie> search(String q) throws IOException {
		
		String request = this.url + "/?s=" + URLEncoder.encode(q, StandardCharsets.UTF_8.toString()) +"&apikey="+ this.apiKey;
    	InputStream is = new URL(request).openStream();

	    try {
	    	ObjectMapper mapper = new ObjectMapper();
	    	JsonNode tree = mapper.readTree(is);
	    	
	    	logger.info(tree.toString());
	    	
			//{"Response":"False","Error":"Movie not found!"}
			JsonNode error = tree.get("Error");
			if(error != null)
				throw new IOException(error.asText());
	    	
	    	return convertMovieList.apply(tree);
	    } finally {
	      is.close();
	    }
	}

	@Override
	public OMDbMovieInfo get(IMDbId iMDbId) throws IOException {
		String request = this.url + "/?i=" + iMDbId.getId() +"&apikey="+ this.apiKey;
    	InputStream is = new URL(request).openStream();

	    try {
	    	ObjectMapper mapper = new ObjectMapper();
	    	JsonNode tree = mapper.readTree(is);
	    	
	    	logger.info(tree.toString());
	    	
			//{"Response":"False","Error":"Movie not found!"}
			JsonNode error = tree.get("Error");
			if(error != null)
				throw new IOException(error.asText());
	    	
	    	return convertMovieInfo.apply(tree);
	    } finally {
	      is.close();
	    }
	}

}
