package de.frittenburger.movievocabulary.impl;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;









import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.Language;

import de.frittenburger.movievocabulary.interfaces.LanguageDetectorService;



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
	public String detect(String text)   {
		
	    
		try {
			Detector detector = DetectorFactory.create(); 
			detector.append(text); 
			ArrayList<Language> pl = detector.getProbabilities();
			
			return pl.get(0).lang;
			
			 
		} catch (LangDetectException e) {
			e.printStackTrace();
		} 
		return null;
	}




	@Override
	public String detect(List<String> examples) {

		Map<String,Integer> count = new HashMap<>();
		for(String example : examples)
		{
			String lang = detect(example);
			if(lang == null) continue;
			if(!count.containsKey(lang))
				count.put(lang, 0);
			count.put(lang,count.get(lang) + 1 );
		}
		
		String selected = null;
		int max = 0;
		//find max
		for(Entry<String, Integer> e : count.entrySet())
		{
			if(e.getValue() <= max) continue;
			selected = e.getKey();
			max = e.getValue();
			
		}
		
		return selected;
	}

}
