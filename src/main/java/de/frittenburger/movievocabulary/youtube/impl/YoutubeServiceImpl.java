package de.frittenburger.movievocabulary.youtube.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.frittenburger.movievocabulary.model.VideoId;
import de.frittenburger.movievocabulary.model.YoutubeId;
import de.frittenburger.movievocabulary.omdb.impl.OMDbServiceImpl;
import de.frittenburger.movievocabulary.youtube.interfaces.YoutubeService;
import de.frittenburger.movievocabulary.youtube.model.YoutubeVideo;
import de.frittenburger.movievocabulary.youtube.model.YoutubeVideoInfo;

public class YoutubeServiceImpl implements YoutubeService {

	private static final Logger logger = LogManager.getLogger(OMDbServiceImpl.class);

	private final String apiKey;
	private final String url;
	
	private final static Function<JsonNode,YoutubeVideoInfo>  convertYoutubeInfo = new JsonToYoutubeInfoMapper();


	public YoutubeServiceImpl(File configFile) throws IOException {
		
		Properties prop = new Properties();
		prop.load(new FileInputStream(configFile));
		this.apiKey = prop.getProperty("apikey");
		this.url = prop.getProperty("url");	
		
	}

	@Override
	public List<YoutubeVideo> search(String q) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public YoutubeVideoInfo get(YoutubeId youtubeId) throws IOException {
		
		
		String request = url+"/videos?part=snippet"
				+"&id="+youtubeId.getId()
				+"&key="+apiKey;;
		InputStream is = new URL(request).openStream();

	    try {
	    	ObjectMapper mapper = new ObjectMapper();
	    	JsonNode tree = mapper.readTree(is);
	    	
	    	logger.info(tree.toString());

	    	//{"Response":"False","Error":"Movie not found!"}
			JsonNode error = tree.get("Error");
			if(error != null)
				throw new IOException(error.asText());
	    	
	    	return convertYoutubeInfo.apply(tree);
	    } finally {
	      is.close();
	    }
	}

	
	public static void main(String args[]) throws IOException, ParseException
	{
		new YoutubeServiceImpl(new File("config/youtube.properties")).get(VideoId.parse("g6zBmBUOMhY",YoutubeId.class));
	
	}
}
