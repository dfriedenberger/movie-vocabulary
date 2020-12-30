package de.frittenburger.movievocabulary.model;

import java.text.ParseException;
import java.util.regex.Pattern;

public class IMDbId {
	
	private final String tt;
	private final String nr;

	private IMDbId(String tt,String nr)
	{
		this.tt = tt;
		this.nr = nr;
	}
	
	public String getId() {
		return tt+nr;
	}
	
	public String getNr() {
		return nr;
	}
	
	public static IMDbId parse(String id) throws ParseException {

		if(!Pattern.matches( "^tt[0-9]+$", id))
				throw new ParseException(id,0);
		
		return new IMDbId(id.substring(0,2),id.substring(2));
	}

	

	
}
