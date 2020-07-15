package de.frittenburger.movievocabulary.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import de.frittenburger.movievocabulary.interfaces.AnnotationWrapper;


public class DefaultAnnotationWrapper implements AnnotationWrapper {


	
	private final List<AnnotationMapper> mappers = new ArrayList<>();


	public DefaultAnnotationWrapper(InputStream is) throws IOException {
		
        try (@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,StandardCharsets.UTF_8))) 
        	{
	        	while(true)
	        	{
	        		String line = reader.readLine();  
	        		if(line == null) break;
	        		
	        		//remove comment
	        		int i = line.indexOf("#");
	        		if(i >= 0)
	        			line = line.substring(0, i);
	        		
	        		line = line.trim();
					if(line.isEmpty()) continue;

					String[] parts = line.split("\\s+");
					
					if(parts.length != 2)
					{
						throw new IOException(line);
					}
					
					mappers.add(new AnnotationMapper(parts[0],parts[1]));
					
					
	        	}
		}	
	}
	
	
	@Override
	public String map(String pos, String ne) {

		for(AnnotationMapper mapper : mappers)
		{
			if(mapper.match(ne+":"+pos))
				return mapper.getValue();
		}
		return UNKNOWN;
	}

}
