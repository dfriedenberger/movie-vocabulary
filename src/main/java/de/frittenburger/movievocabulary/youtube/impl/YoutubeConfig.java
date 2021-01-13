package de.frittenburger.movievocabulary.youtube.impl;

import java.io.File;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.frittenburger.movievocabulary.youtube.interfaces.YoutubeService;



@Configuration
public class YoutubeConfig {

	
	@Bean
	public YoutubeService getYoutubeServiceInstance()
	{
		try {
			return new YoutubeServiceImpl(new File("config/youtube.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("cannot create YoutubeService Instance");
		}

	}
}