package de.frittenburger.movievocabulary.model;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SrtRecord {



	private int id;
	private long from;
	private long to;
	private List<String> textes = new ArrayList<String>();

	public void setTime(long from, long to) {
		this.from = from;
		this.to = to;
	}

	public void addText(String text) {
		textes.add(text);
	}

	public int getId() {
		return id;
	}
	
	public long getFrom() {
		return from;
	}
	
	public long getTo() {
		return to;
	}
	
	@Override
	public String toString() {
		return id + " " + timestr(from) + " -> " + timestr(to) + " [" + join(textes," ")+"]";
	}

	private String join(List<String> textList, String space) {
        String line = "";
		for(int i = 0;i < textList.size();i++)
		{
			if(i > 0) line += " ";
			line += textList.get(i);			
		}
		
		return line;
	}

	static String timestr(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
		return sdf.format(new Date(time - 60 * 60 * 1000));
	}

	public List<String> getText() {
		return textes;
	}

	public String joinText() {
		return join(textes," ");
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	public void setText(List<String> textes)
	{
		this.textes = textes;
	}
}
