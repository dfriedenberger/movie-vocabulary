package de.frittenburger.movievocabulary.controller;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "video not found")
public class VideoNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7046253053534082550L;

	
}

