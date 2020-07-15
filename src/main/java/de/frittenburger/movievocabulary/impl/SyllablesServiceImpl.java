package de.frittenburger.movievocabulary.impl;

import de.frittenburger.movievocabulary.interfaces.SyllablesService;

public class SyllablesServiceImpl implements SyllablesService {

	@Override
	public int count(String p) {
		
		int c = 0;
		//How many times do you hear A, E, I, O, or U as a separate sound?

		
		char last = 'b';
		for(char b : p.toLowerCase().toCharArray())
		{
			
			if(isVocal(b) && !isVocal(last))
				c++;
			last = b;
		}
		
		return c == 0?1:c;
	}

	private boolean isVocal(char b) {
		return "aeiouéáíóúüäö".indexOf(b) >= 0;
	}

}
