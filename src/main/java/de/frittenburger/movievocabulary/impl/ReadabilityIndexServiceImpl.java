package de.frittenburger.movievocabulary.impl;

import de.frittenburger.movievocabulary.interfaces.ReadabilityIndexService;
import de.frittenburger.movievocabulary.interfaces.SyllablesService;

public class ReadabilityIndexServiceImpl implements ReadabilityIndexService {

	//see also https://de.wikipedia.org/wiki/Lesbarkeitsindex
	// here implemented https://en.wikipedia.org/wiki/Automated_readability_index
	
	private static SyllablesService syllablesService = new SyllablesServiceImpl();
	
	@Override
	public int calculate(String sentence) {
		
		int words = 0;
		int syllables = 0;
		String[] parts = sentence.split("\\s+");
		for(String p : parts)
		{
			if(p.trim().isEmpty()) continue;
			words++;
			
			syllables += syllablesService.count(p);
		}
		
		
		double result = (words) + ((1.0 * syllables) / words);
		
		return (int)(result + 1);
	}

}
