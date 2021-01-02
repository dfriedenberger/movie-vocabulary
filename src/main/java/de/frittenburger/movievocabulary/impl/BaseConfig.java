package de.frittenburger.movievocabulary.impl;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.frittenburger.movievocabulary.interfaces.MovieDatabase;

@Configuration
public class BaseConfig {

	@Bean
	public MovieDatabase getInstance()
	{
		return new MovieDatabaseImpl(new File("data"));
	}
}
