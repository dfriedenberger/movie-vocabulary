package de.frittenburger.movievocabulary.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "internal error")
public class InternalErrorException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5660938825925521244L;

}
