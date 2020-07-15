package de.frittenburger.movievocabulary.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.frittenburger.movievocabulary.interfaces.NlpService;
import de.frittenburger.movievocabulary.interfaces.NlpServiceFactory;

public class NlpServiceFactoryImpl implements NlpServiceFactory {

		
	private final Map<String, NlpService> services = new HashMap<>();
	
	
	
	@Override
	public NlpService getNlpService(String language) throws IOException {
		
		if(!services.containsKey(language))
			services.put(language,new NlpServiceImpl(language,new HashMap<>()));
		
		return services.get(language);
	}

}
