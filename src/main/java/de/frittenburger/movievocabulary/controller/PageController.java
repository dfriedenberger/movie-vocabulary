package de.frittenburger.movievocabulary.controller;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.frittenburger.movievocabulary.impl.LanguageDetectorServiceImpl;
import de.frittenburger.movievocabulary.impl.MovieVocabularyServiceImpl;
import de.frittenburger.movievocabulary.impl.NlpServiceFactoryImpl;
import de.frittenburger.movievocabulary.impl.PoFileReader;
import de.frittenburger.movievocabulary.impl.PoFileWriter;
import de.frittenburger.movievocabulary.impl.ReadabilityIndexServiceImpl;
import de.frittenburger.movievocabulary.impl.SrtCompressServiceImpl;
import de.frittenburger.movievocabulary.impl.SrtReaderServiceImpl;
import de.frittenburger.movievocabulary.impl.UnzipServiceImpl;
import de.frittenburger.movievocabulary.interfaces.MovieVocabularyService;
import de.frittenburger.movievocabulary.interfaces.ReadabilityIndexService;
import de.frittenburger.movievocabulary.model.Annotation;
import de.frittenburger.movievocabulary.model.Card;
import de.frittenburger.movievocabulary.model.Paragraph;
import de.frittenburger.movievocabulary.model.PoFile;
import de.frittenburger.movievocabulary.model.PoMap;
import de.frittenburger.movievocabulary.model.Sentence;
import de.frittenburger.movievocabulary.model.SrtRecord;
import de.frittenburger.movievocabulary.model.TranslatedSentence;




@Controller
public class PageController {

 
	private static final Logger logger = LogManager.getLogger(PageController.class);


	private static final MovieVocabularyService movieVocabularyService = 
			new MovieVocabularyServiceImpl(new UnzipServiceImpl(),  new SrtReaderServiceImpl(),
					new SrtCompressServiceImpl(2000),new LanguageDetectorServiceImpl(), new NlpServiceFactoryImpl(),new File("data"));
	
	private static final ReadabilityIndexService service = new ReadabilityIndexServiceImpl();

	
	@RequestMapping("/")
	public String welcome(@RequestHeader Map<String, String> headers, Map<String, Object> model, HttpServletRequest request) {
		
	    model.put("header", "MovieVocabulary");
	    
		return "welcome";
	}
	
	
	@RequestMapping("/admin")
	public String admin(@RequestHeader Map<String, String> headers, Map<String, Object> model, HttpServletRequest request) {
		
	    headers.forEach((key, value) -> {
	    	logger.info(String.format("Header '%s' = %s", key, value));
	    });
	    
	    model.put("header", "MovieVocabulary");
	    model.put("message", "Lade einen Untertitel zu deinem Film in der zu lernenden Sprache hoch");
	    
	    
		return "admin";
	}
	
	@RequestMapping(value = "/movie/{id}", method = RequestMethod.GET)
	public String movie(HttpServletRequest request,Map<String, Object> model,@PathVariable(value="id") String id) throws IOException {
		
	    model.put("header", id);
	    model.put("movie", id);
		
		return "movie";
	}
	
	@RequestMapping(value = "/learn/{id}", method = RequestMethod.GET)
	public String learn(HttpServletRequest request,Map<String, Object> model,@PathVariable(value="id") String id) throws IOException {
		
	    model.put("header", id);
	    model.put("movie", id);
		
		return "learn";
	}
	
	@RequestMapping(value = "/info/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String,Object> info(HttpServletRequest request,
				@PathVariable(value="id") String id) throws IOException {
		
		Map<String,Object> info = new HashMap<>();
		
		
		
		
		List<Paragraph> paragraphs = new ObjectMapper().readValue(new File("data/"+id+"/paragraphs.json"), new TypeReference<List<Paragraph>>(){});
		info.put("paragraphs",paragraphs.size());
		
		Set<String> verbs = new HashSet<>();
		Set<String> nouns = new HashSet<>();
		Set<String> adjs = new HashSet<>();
		Set<String> advs = new HashSet<>();
		
		for(Paragraph paragraph : paragraphs)
			for(Sentence sentence : paragraph.getSentences())
			{
				for(Annotation annotation : sentence.getAnnotations())
				{
					String key = annotation.getKey();
					switch(annotation.getType())
					{
						case "T:VERB": verbs.add(key); break;
						case "T:NOUN": nouns.add(key); break;
						case "T:ADJ": adjs.add(key); break;
						case "T:ADV": advs.add(key); break;
					}
					
				}
			}
		info.put("verbs",verbs.size());
		info.put("nouns",nouns.size());
		info.put("adjs",adjs.size());
		info.put("advs",advs.size());

		return info;
		
	}
	
	@RequestMapping(value = "/metadata/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String,Object> getMetadata(HttpServletRequest request,
				@PathVariable(value="id") String id) throws IOException {
		
		
		File metadataFile = new File("data/"+id+"/metadata.json");
		Map<String,Object> metadata = null;
		
		if(metadataFile.exists())
			metadata = new ObjectMapper().readValue(metadataFile, new TypeReference<Map<String,Object>>(){});
		else
		{
			
			metadata = new HashMap<>();
			metadata.put("title",id);
			metadata.put("image","");
			metadata.put("image_top","0px");
			metadata.put("link","");
			metadata.put("link_trailer","");
			metadata.put("link_amazon", "");
		}
		
		//Todo calculate only once
		List<SrtRecord> srtRecords = new ObjectMapper().readValue(new File("data/"+id+"/subtitle.json"), new TypeReference<List<SrtRecord>>(){});
		long duration = srtRecords.get(srtRecords.size()-1).getTo() -  srtRecords.get(0).getFrom();
		long minutes = Math.round(duration / 60000.0);
		metadata.put("minutes", minutes);

		PoFile poFile = new PoFileReader().readPoFile(new File("data/"+id+"/translate.de.po"));
		long words = poFile.count();
		metadata.put("words", words);

		return metadata;
		
	}
	
	@RequestMapping(value = "/metadata/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String,Object> postMetadata(HttpServletRequest request,
				@PathVariable(value="id") String id,@RequestBody Map<String,Object> body) throws IOException {
		
		logger.info(body);
		File metadataFile = new File("data/"+id+"/metadata.json");
		
		new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(metadataFile, body);
		
		return body;
		
	}
	
	
	//Data
	@RequestMapping(value = "/vocabulary/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<TranslatedSentence> vocabulary(HttpServletRequest request,
				@PathVariable(value="id") String id,
				@RequestParam(value="offset", required=false) Integer offset,
				@RequestParam(value="limit", required=false) Integer limit

			) throws IOException {
		
		
		if(offset == null) offset = 0;
		if(limit == null) limit = 10;

		List<TranslatedSentence> translatedSentenceList = new ArrayList<>();
		List<Paragraph> paragraphs = new ObjectMapper().readValue(new File("data/"+id+"/paragraphs.json"), new TypeReference<List<Paragraph>>(){});
		PoFile poFile = new PoFileReader().readPoFile(new File("data/"+id+"/translate.de.po"));

		for(int ix = offset;ix < offset + limit && ix < paragraphs.size();ix++)
		{
			Paragraph paragraph = paragraphs.get(ix);
			for(Sentence sentence : paragraph.getSentences())
			{
			
		    	TranslatedSentence translatedSentence = new TranslatedSentence();
		    	translatedSentence.setSentence(sentence);
		    	
				int ri = service.calculate(sentence.getText());
				translatedSentence.setReadingIndex(ri);
				
				PoMap map = poFile.createMap(sentence.getText());
				for(Annotation annotation : sentence.getAnnotations())
				{
					//String type = annotation.getType();
					String key = annotation.getKey();
			    	translatedSentence.getTranslation().put(key,map.get(key));
				}
				translatedSentenceList.add(translatedSentence);
			}
		}
	    return translatedSentenceList;
	}
	
	

	@RequestMapping(value = "/change/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TranslatedSentence change(HttpServletRequest request,@RequestBody TranslatedSentence body,
				@PathVariable(value="id") String id) throws IOException  {
	
		logger.info(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(body));
		
		
		List<Paragraph> paragraphs = new ObjectMapper().readValue(new File("data/"+id+"/paragraphs.json"), new TypeReference<List<Paragraph>>(){});

		PoFile poFile = new PoFileReader().readPoFile(new File("data/"+id+"/translate.de.po"));

				
		for(int ix = 0;ix < paragraphs.size();ix++)
		{
			Paragraph paragraph = paragraphs.get(ix);
			for(Sentence sentence : paragraph.getSentences())
			{
				if(sentence.getText().equals(body.getSentence().getText()))
				{
					//change annotation
					sentence.setAnnotations(body.getSentence().getAnnotations());
					
					//change Translation
					poFile.replace(sentence.getText(),body.getTranslation());
					
					//save
					new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File("data/"+id+"/paragraphs.json"), paragraphs);
					new PoFileWriter().writePoFile(new File("data/"+id+"/translate.de.po"),poFile);
					
					return body;
				}
			}
		}
		
		
		throw new IOException("not found");
	}
	
	@CrossOrigin
	@RequestMapping(value = "/cards/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Card> cards(HttpServletRequest request,
			@PathVariable(value="id") String id,
			@RequestParam(value="minlevel", required=false) Integer minlevel,
			@RequestParam(value="maxlevel", required=false) Integer maxlevel) throws IOException {
		
		//default
		if(minlevel == null) minlevel = 0;
		
		List<Paragraph> paragraphs = new ObjectMapper().readValue(new File("data/"+id+"/paragraphs.json"), new TypeReference<List<Paragraph>>(){});
		PoFile poFile = new PoFileReader().readPoFile(new File("data/"+id+"/translate.de.po"));

		List<Card> cards = new ArrayList<>();
		Set<String> cache = new HashSet<>();
		for(Paragraph paragraph : paragraphs)
		{
			for(Sentence sentence : paragraph.getSentences())
			{
				PoMap map = poFile.createMap(sentence.getText());

				for(Annotation a : sentence.getAnnotations())
				{
					if(minlevel != null && a.getLevel() < minlevel) continue;
					if(maxlevel != null && a.getLevel() > maxlevel) continue;
					if(cache.contains(a.getKey())) continue;
					cache.add(a.getKey());
					
					Card card = new Card();
					card.setQuestion(a.getKey());
					card.setAnswer(map.get(a.getKey()));
					cards.add(card);
				}
			}
		}
		
		
		return cards;
		
	}
	
	

	@RequestMapping(value = "/file-upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String,String> uploadFile(MultipartHttpServletRequest request) throws IOException {
	   
		Map<String,String> response = new HashMap<>();
	        Iterator<String> itr = request.getFileNames();
	        while (itr.hasNext()) {
	            String uploadedFile = itr.next();
	            MultipartFile file = request.getFile(uploadedFile);
	            response.put("name",file.getOriginalFilename());
	            response.put("contentType",file.getContentType());
	            response.put("size",""+file.getSize());
				
	            String id = movieVocabularyService.importFile(file.getOriginalFilename(),file.getInputStream());
	            response.put("id",id);

	            logger.info(response);
	        }
	   
		return response;
	}
	
	@RequestMapping(value = "/list-movies", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<String> listMovies(HttpServletRequest request) throws IOException {
	
		return Arrays.asList(new File("data").list(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return new File(dir,name).isDirectory();
				}

		}));
		
		
	}
}