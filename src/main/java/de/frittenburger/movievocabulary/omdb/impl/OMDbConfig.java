package de.frittenburger.movievocabulary.omdb.impl;

import java.io.File;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.frittenburger.movievocabulary.omdb.interfaces.OMDbService;

@Configuration
public class OMDbConfig {

	
	@Bean
	public OMDbService getOMDbServiceInstance()
	{
		try {
			return new OMDbServiceImpl(new File("config/omdb.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("cannot create OMDbService Instance");
		}

	}
}
