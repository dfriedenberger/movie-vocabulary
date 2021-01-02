package de.frittenburger.movievocabulary.analyse.impl;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;









import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

import de.frittenburger.movievocabulary.analyse.interfaces.LanguageDetectorService;
import de.frittenburger.movievocabulary.model.Language;


@Service
public class LanguageDetectorServiceImpl implements LanguageDetectorService {

	
	static
	{
		try
		{
		    List<String> profiles = new ArrayList<>();
	
		    if(DetectorFactory.getLangList().size() == 0)
		    {
			    ClassLoader cl = Detector.class.getClassLoader();
			    try (InputStream in = 
	                    cl.getResourceAsStream("profiles/es");) {
	                profiles.add(IOUtils.toString(in, "utf8"));
	            }
			    try (InputStream in = 
	                    cl.getResourceAsStream("profiles/de");) {
	                profiles.add(IOUtils.toString(in, "utf8"));
	            }
			    try (InputStream in = 
	                    cl.getResourceAsStream("profiles/en");) {
	                profiles.add(IOUtils.toString(in, "utf8"));
	            }
			    DetectorFactory.loadProfile(profiles);
		    }

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	@Override
	public Language detect(String text)   {
		
	    
		try {
			Detector detector = DetectorFactory.create(); 
			detector.append(text); 
			ArrayList<com.cybozu.labs.langdetect.Language> pl = detector.getProbabilities();
			
			return Language.parse(pl.get(0).lang);
			
			 
		} catch (LangDetectException e) {
			System.err.println(text);
			e.printStackTrace();
		} 
		return null;
	}

	@Override
	public Language detect(File file, Charset charset) throws IOException {
		
		List<String> samples = new ArrayList<>();
		
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	if(isTextSample(line))
		    		samples.add(line);
		    	if(samples.size() > 20) break;
		    }
		}
		
		return detect(samples);
	}


	private boolean isTextSample(String line) {

		int characters = 0;
		int digits = 0;
		char c[] = line.toCharArray();
		for(int i = 0;i < c.length;i++)
		{
			if(Character.isLetter(c[i])) characters++;
			if(Character.isDigit(c[i])) digits++;
		}
		return (c.length > 5) && (characters * 2) > (c.length) && ((2 * digits) < characters);
	}

	@Override
	public Language detect(List<String> examples) {

		Map<Language,Integer> count = new HashMap<>();
		for(String example : examples)
		{
			Language lang = detect(example);
			if(lang == null) continue;
			if(!count.containsKey(lang))
				count.put(lang, 0);
			count.put(lang,count.get(lang) + 1 );
		}
		
		Language selected = null;
		int max = 0;
		//find max
		for(Entry<Language, Integer> e : count.entrySet())
		{
			if(e.getValue() <= max) continue;
			selected = e.getKey();
			max = e.getValue();
			
		}
		
		return selected;
	}

	
	
	
}
