package de.frittenburger.movievocabulary.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PoFile {

	private final List<String> lines = new ArrayList<>();
	
	public void addExtractedComment(String text) {
		lines.add("#. "+text);
	}
	
	public void addMessageId(String msgid)
	{
		lines.add("msgid \""+msgid+"\"");

	}
	public void addMessageStr(String msgstr)
	{
		lines.add("msgstr \""+msgstr+"\"");
	}

	public List<String> getLines() {
		return lines;
	}

	private final Pattern pattern = Pattern
			.compile("^[msgidstr]+\\s+\"(.*)\"$");
	
	private String match(String str) throws IOException {
		Matcher matcher = pattern.matcher(str);
		if (!matcher.find())
			throw new IOException(str);
		return matcher.group(1);
	}
	
	public PoMap createMap(String text) throws IOException {
		PoMap map = new PoMap();
		
		String msgid = null;
		boolean read = false;

		for(String line : lines) {

			
			if(line.startsWith("#. ") && read) break;
			if(line.startsWith("#. ") && line.endsWith(text)) read = true;
			
			if(!read) continue;
			
			if (line.startsWith("msgid")) {
				msgid = match(line);

			}
			if (line.startsWith("msgstr")) {
				String msgstr = match(line);
				map.put(msgid, msgstr);
			}
			
		}
		return map;
		
	}

	public int deleteSection(String text) {

		boolean remove = false;
		for(int i = 0;i < lines.size();i++)
		{
			String line = lines.get(i);
			if(line.startsWith("#. ") && remove) return i;
			if(line.startsWith("#. ") && line.endsWith(text)) remove = true;
			if(remove)
				lines.remove(i--);
		}
		return -1;
	}

	public void replace(String text, Map<String, String> translation) {
		
		boolean remove = false;
		for(int i = 0;i < lines.size();i++)
		{
			String line = lines.get(i);
			if(line.startsWith("#. ") && remove) return;
			if(line.startsWith("#. ") && line.endsWith(text)) 
			{
				for(Entry<String, String> e : translation.entrySet())
				{
					lines.add(++i,"msgid \""+e.getKey()+"\"");
					lines.add(++i,"msgstr \""+e.getValue()+"\"");
				}
				//remove the rest
				remove = true;
				continue;
			}
			if(remove)
				lines.remove(i--);
		}
		
	}

	public long count() throws IOException {

		Set<String> c = new HashSet<>();
		for(String line : lines) {
			if (line.startsWith("msgid")) {
				String msgid = match(line);
				c.add(msgid);
			}
		}
		return c.size();
		
	}

	

}
