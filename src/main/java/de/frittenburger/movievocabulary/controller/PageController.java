package de.frittenburger.movievocabulary.controller;


import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class PageController {

 
	private static final Logger logger = LogManager.getLogger(PageController.class);



	
	@RequestMapping("/")
	public String welcome(@RequestHeader Map<String, String> headers, Map<String, Object> model, HttpServletRequest request) {
		
		 headers.forEach((key, value) -> {
		    	logger.info(String.format("Header '%s' = %s", key, value));
		    });
		    
		 
	    model.put("header", "MovieVocabulary");
	    
		return "welcome";
	}
	

	@RequestMapping(value = "/learn/{id}", method = RequestMethod.GET)
	public String learn(HttpServletRequest request,Map<String, Object> model,@PathVariable(value="id") String id) throws IOException {
		
	    model.put("header", id);
	    model.put("movie", id);
		
		return "learn";
	}
	
}