package de.frittenburger.movievocabulary.model;

import java.text.ParseException;
import java.util.regex.Pattern;

public class VideoId {
	
	private final String id;

	protected VideoId(String id)
	{
		this.id = id;
	}
	
	
	public String getId() {
		return id;
	}
	
	
	
	public static VideoId parse(String id) throws ParseException {

		if(Pattern.matches( "^tt[0-9]+$", id)) //tt movie
			return new IMDbId(id.substring(0,2),id.substring(2));
		
		//https://webapps.stackexchange.com/questions/54443/format-for-id-of-youtube-video
		if(Pattern.matches( "^[0-9A-Za-z_-]{10}[048AEIMQUYcgkosw]$", id)) //youtube
			return new YoutubeId(id);
		
		

		throw new ParseException(id,0);

	}


	public static <T extends VideoId> T parse(String id, Class<T> clazz) throws ParseException {

		VideoId videoId = parse(id);
		
		T typedVideoId = clazz.cast(videoId);
	
		if(typedVideoId == null)
			throw new ParseException("wrong type "+videoId+" ("+clazz.getSimpleName()+")",0);
		
		return typedVideoId;
	}

	

	
}
