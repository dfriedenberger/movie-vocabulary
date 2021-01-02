package de.frittenburger.movievocabulary.convert.impl;

import java.io.File;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.frittenburger.movievocabulary.convert.interfaces.NlpService;
import de.frittenburger.movievocabulary.convert.interfaces.SrtCompressService;

@Configuration
public class ConvertConfig {

	@Bean
	public SrtCompressService getSrtCompressServiceInstance()
	{
		return new SrtCompressServiceImpl(2000);
	}
	
	@Bean
	public NlpService getNlpServiceInstance()
	{
		try {
			return new NlpServiceImpl(new File("config/nlp.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("cannot create NlpService Instance");
		}
	}
}
