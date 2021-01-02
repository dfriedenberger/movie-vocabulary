package de.frittenburger.movievocabulary.opensubtitle.impl;

import java.io.File;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.frittenburger.movievocabulary.opensubtitle.interfaces.OpenSubtitleService;

@Configuration
public class OpenSubtitleConfig {

	
	@Bean
	public OpenSubtitleService getOpenSubtitleServiceInstance()
	{
		try {
			return new OpenSubtitleServiceImpl(new File("config/opensubtitle.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("cannot create OpenSubtitleService Instance");
		}
	}
}
