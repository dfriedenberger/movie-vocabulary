package de.frittenburger.movievocabulary.translate.impl;

import java.io.File;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.frittenburger.movievocabulary.translate.interfaces.TranslateService;


@Configuration
public class TranslateConfig {

	@Bean
	public TranslateService getTranslateServiceInstance()
	{
		try {
			return new TranslateServiceImpl(new File("config/translate.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("cannot create TranslateService Instance");
		}
	}
}
