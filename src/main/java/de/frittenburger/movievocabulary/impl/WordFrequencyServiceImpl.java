package de.frittenburger.movievocabulary.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import de.frittenburger.movievocabulary.interfaces.WordFrequencyService;



public class WordFrequencyServiceImpl implements WordFrequencyService {

	

	private Map<String,Long> wordmap = new HashMap<>();
	private Map<String,Integer> wordLevel = new HashMap<>();
	private int cnt = 0;
	public WordFrequencyServiceImpl(InputStream is) throws IOException {
		
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is,StandardCharsets.UTF_8))) 
        	{
        		int level = 0;
        		int levelcnt = 500;

	        	while(true)
	        	{
	        		String line = reader.readLine();  
	        		if(line == null) break;
					
					int i = line.indexOf(' ');
					if(i < 0) continue;
					
					
					
					String word = line.substring(0,i).trim().toLowerCase();
					long count = Long.parseLong(line.substring(i+1).trim());
					wordmap.put(word,count);
					cnt++;
					if(cnt % levelcnt == 0) //TODO what ist the right condition for A1,A2,B1,B2,...
					{
						level++;
						levelcnt *= 2;
					}
					wordLevel.put(word, level);
	        	}
		}	
	}

	@Override
	public int level(String word) {
		
		Long c = wordmap.get(word.toLowerCase());
		Integer l = wordLevel.get(word.toLowerCase());
		if(c == null) return 99;
		//frequency is  ((c * 100.0) / words)
		return l;
		
	}
	
}
