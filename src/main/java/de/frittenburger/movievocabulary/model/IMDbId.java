package de.frittenburger.movievocabulary.model;

public class IMDbId extends VideoId {

	private final String nr;

	protected IMDbId(String tt,String nr)
	{
		super(tt+nr);
		this.nr = nr;
	}
	
	public String getNr() {
		return nr;
	}
}
