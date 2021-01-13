package de.frittenburger.movievocabulary.controller;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Map;

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
import de.frittenburger.movievocabulary.model.VideoId;
import de.frittenburger.movievocabulary.model.YoutubeId;
import de.frittenburger.movievocabulary.model.IMDbId;
import de.frittenburger.movievocabulary.model.MovieMetadata;



@Controller
public class AdminController {

 
	private static final Logger logger = LogManager.getLogger(AdminController.class);

	private final MovieDatabase movieDatabase = new MovieDatabaseImpl(new File("data"));

	
	@RequestMapping("/list")
	public String listMovies() {
		return "admin/index";
	}
		
	
	@RequestMapping(value = "/create/{id}", method = RequestMethod.GET)
	public String createMovie(@RequestHeader Map<String, String> headers,HttpServletRequest request,Map<String, Object> model,@PathVariable(value="id") String id)  {
	  
		
		try {
			 VideoId videoId = VideoId.parse(id);
			 if(movieDatabase.exists(videoId))
			 {
				 return "redirect:/edit/"+videoId.getId();
			 }
			 
			 
			 model.put("movieId", videoId.getId());
			 if(videoId instanceof IMDbId)
				 model.put("url", "/api/v1/omdb");
			 if(videoId instanceof YoutubeId)
				 model.put("url", "/api/v1/youtube");

			 return "edit";
		} catch (ParseException e) {
			logger.error(e);
			throw new InternalErrorException();
		}
		
	}
	
	
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public String editMovie(@RequestHeader Map<String, String> headers,HttpServletRequest request,Map<String, Object> model,@PathVariable(value="id") String id)  {
	  
		
		try {
			 VideoId iMDbId = VideoId.parse(id);
			 model.put("movieId", iMDbId.getId());
			 model.put("url", "/api/v1/read");
			 return "edit";
		} catch (ParseException e) {
			logger.error(e);
			throw new InternalErrorException();
		}
		
	}
	
	
	@RequestMapping(value = "/files/{id}", method = RequestMethod.GET)
	public String listFilesMovie(@RequestHeader Map<String, String> headers,HttpServletRequest request,Map<String, Object> model,@PathVariable(value="id") String id)  {
	  
		try {
			 VideoId iMDbId = VideoId.parse(id);
			 model.put("movieId", iMDbId.getId());
			 return "files";
		} catch (ParseException e) {
			logger.error(e);
			throw new InternalErrorException();
		}
		
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
			           
	        VideoId videoId = VideoId.parse(metadata.get("imdbId"));
	        
	        if(!movieDatabase.exists(videoId))
	        	movieDatabase.create(videoId);

			movieDatabase.updateMetadata(videoId,metadata);
			return "redirect:/files/"+videoId.getId();

		} catch(ParseException | IOException e) {
			
			logger.error(e);
			throw new InternalErrorException();
		}
	}
	
	
	
	@RequestMapping(value = "/review/{id}/{source}/{target}", method = RequestMethod.GET)
	public String movie(HttpServletRequest request,Map<String, Object> model,
			@PathVariable(value="id") String id,
			@PathVariable(value="source") String sourceLanguage,
			@PathVariable(value="target") String targetLanguage) throws IOException {
		
	    model.put("movieId", id);
	    model.put("sourceLanguage", sourceLanguage);
	    model.put("targetLanguage", targetLanguage);

		return "review";
	}
	
	
	
	
}