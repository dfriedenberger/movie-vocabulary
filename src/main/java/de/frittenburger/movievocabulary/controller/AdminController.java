package de.frittenburger.movievocabulary.controller;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.frittenburger.movievocabulary.impl.MovieDatabaseImpl;
import de.frittenburger.movievocabulary.interfaces.MovieDatabase;
import de.frittenburger.movievocabulary.model.IMDbId;
import de.frittenburger.movievocabulary.model.MovieMetadata;
import de.frittenburger.movievocabulary.opensubtitle.impl.OpenSubtitleServiceImpl;
import de.frittenburger.movievocabulary.opensubtitle.interfaces.OpenSubtitleService;



@Controller
public class AdminController {

 
	private static final Logger logger = LogManager.getLogger(AdminController.class);

	private final MovieDatabase movieDatabase = new MovieDatabaseImpl(new File("data"));

	@RequestMapping("/search")
	public String searchMovie(@RequestHeader Map<String, String> headers, Map<String, Object> model, HttpServletRequest request) {
		return "search";
	}
	
	
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public String editMovie(@RequestHeader Map<String, String> headers,HttpServletRequest request,Map<String, Object> model,@PathVariable(value="id") String id)  {
	  
		try {
			 IMDbId iMDbId = IMDbId.parse(id);
			 model.put("movieId", iMDbId.getId());
		} catch (ParseException e) {
			logger.error(e);
		}
	    
		
		return "edit";
	}
	
	
	@RequestMapping(value = "/files/{id}", method = RequestMethod.GET)
	public String listFilesMovie(@RequestHeader Map<String, String> headers,HttpServletRequest request,Map<String, Object> model,@PathVariable(value="id") String id)  {
	  
		try {
			 IMDbId iMDbId = IMDbId.parse(id);
			 model.put("movieId", iMDbId.getId());
		} catch (ParseException e) {
			logger.error(e);
		}
		
		return "files";
	}
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String saveMovie(HttpServletRequest request,Map<String, Object> model)  {
		
		
		try
		{
	        Enumeration<String> names = request.getParameterNames();
	        
	        MovieMetadata metadata = new MovieMetadata();
	        while (names.hasMoreElements()) {
	            String name = names.nextElement();
	            String value = request.getParameter(name);
	            metadata.put(name, value);
	        }
			           
	        IMDbId iMDbId = IMDbId.parse(metadata.get("imdbId"));
			movieDatabase.updateMetadata(iMDbId,metadata);
			return "redirect:/files/"+iMDbId.getId();

		} catch(ParseException | IOException e) {
			
			logger.error(e);
			throw new InternalErrorException();
		}
	}
	
	
	
	@RequestMapping(value = "/movie/{id}", method = RequestMethod.GET)
	public String movie(HttpServletRequest request,Map<String, Object> model,@PathVariable(value="id") String id) throws IOException {
		
	    model.put("header", id);
	    model.put("movie", id);
		
		return "movie";
	}
	
	
	
	
}