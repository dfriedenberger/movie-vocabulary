package de.frittenburger.movievocabulary.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.frittenburger.movievocabulary.analyse.interfaces.EncodingDetectorService;
import de.frittenburger.movievocabulary.analyse.interfaces.LanguageDetectorService;
import de.frittenburger.movievocabulary.impl.MovieDatabaseImpl;
import de.frittenburger.movievocabulary.impl.PoFileReader;
import de.frittenburger.movievocabulary.impl.PoFileWriter;
import de.frittenburger.movievocabulary.impl.ReadabilityIndexServiceImpl;
import de.frittenburger.movievocabulary.inbound.impl.DownloadServiceImpl;
import de.frittenburger.movievocabulary.inbound.impl.ImportServiceImpl;
import de.frittenburger.movievocabulary.inbound.impl.UnzipServiceImpl;
import de.frittenburger.movievocabulary.inbound.interfaces.DownloadService;
import de.frittenburger.movievocabulary.inbound.interfaces.ImportService;
import de.frittenburger.movievocabulary.interfaces.MovieDatabase;
import de.frittenburger.movievocabulary.interfaces.ReadabilityIndexService;
import de.frittenburger.movievocabulary.model.Annotation;
import de.frittenburger.movievocabulary.model.Card;
import de.frittenburger.movievocabulary.model.IMDbId;
import de.frittenburger.movievocabulary.model.Paragraph;
import de.frittenburger.movievocabulary.model.PoFile;
import de.frittenburger.movievocabulary.model.PoMap;
import de.frittenburger.movievocabulary.model.Sentence;
import de.frittenburger.movievocabulary.model.SrtRecord;
import de.frittenburger.movievocabulary.model.TranslatedSentence;
import de.frittenburger.movievocabulary.omdb.impl.OMDbServiceImpl;
import de.frittenburger.movievocabulary.omdb.interfaces.OMDbService;
import de.frittenburger.movievocabulary.omdb.model.OMDbMovie;
import de.frittenburger.movievocabulary.omdb.model.OMDbMovieInfo;
import de.frittenburger.movievocabulary.opensubtitle.impl.OpenSubtitleServiceImpl;
import de.frittenburger.movievocabulary.opensubtitle.interfaces.OpenSubtitleService;
import de.frittenburger.movievocabulary.opensubtitle.model.OpenSubtitle;
import de.frittenburger.movievocabulary.process.impl.ProcessorImpl;
import de.frittenburger.movievocabulary.process.impl.SrtToTextConverterTask;
import de.frittenburger.movievocabulary.process.interfaces.Processor;




@RestController
@RequestMapping("/api/v1/")
public class ApiController {

 
	private static final Logger logger = LogManager.getLogger(ApiController.class);

	
	private static final Processor processor = new ProcessorImpl();
	
	private static final ReadabilityIndexService service = new ReadabilityIndexServiceImpl();

	private final OMDbService omdbService;
	private final OpenSubtitleService openSubtitleService;
	private final MovieDatabase movieDatabase = new MovieDatabaseImpl(new File("data"));
	private final DownloadService downloadService = new DownloadServiceImpl();
	private final ImportService importService = new ImportServiceImpl(new UnzipServiceImpl());
	
	@Autowired
	private LanguageDetectorService languageDetector;
	
	@Autowired
	private EncodingDetectorService encodingDetector;
	
	public ApiController() throws IOException
	{
		omdbService = new OMDbServiceImpl(new File("config/omdb.properties"));
		openSubtitleService = new OpenSubtitleServiceImpl(new File("config/opensubtitle.properties"));
	}
	
	
	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String,Object> searchMovie(HttpServletRequest request,
			@RequestParam(value="q") String q)  {
	
		
		Map<String,Object> result = new HashMap<>();
		result.put("q", q);

		List<OMDbMovie> movies;
		try {
			movies = omdbService.search(q);
			result.put("status", "Ok");
			result.put("movies", movies);
		} catch (IOException e) {
			logger.error(e);			
			result.put("status", "Error");
			result.put("error", e.getMessage());
		}
	
	

		return result;
		
	}
	
	@RequestMapping(value = "/omdb/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public OMDbMovieInfo imdbInfo(HttpServletRequest request, @PathVariable(value="id") String id)  {
	

		try {
			IMDbId iMDbId = IMDbId.parse(id);
			return omdbService.get(iMDbId);
		} catch (ParseException | IOException e) {
			logger.error(e);			
		}
	
		throw new VideoNotFoundException();		
	}
	
	@RequestMapping(value = "/opensubtitle/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<OpenSubtitle> opensubtitleSearch(HttpServletRequest request, @PathVariable(value="id") String id,@RequestParam("language") String language)  {
	

		try {
			IMDbId iMDbId = IMDbId.parse(id);
			//return openSubtitleService.search(iMDbId,language);
			
			List<OpenSubtitle> opensubtitles = new ArrayList<>();

			OpenSubtitle s = new OpenSubtitle();
			opensubtitles.add(s);
			
			s.setId(id);
			s.setFileName("Ready.to.Mingle.2019.1080p.NF.WEB-DL.DDP5.1.H264-CMRG_spa.srt");
			s.setEncoding("UTF-8");
			s.setLanguage("Spanish");
			s.setFormat("srt");
			s.setDownloadlink("https://dl.opensubtitles.org/en/download/src-api/vrf-f54f0bb6/sid-8gwEzMZzxt6NP7cSkbRX,59XZV8/subad/7912325");
			
			
			return opensubtitles;
			
			
		} catch (ParseException  /*| IOException | XmlRpcException */ e) {
			logger.error(e);			
		}
	
		throw new VideoNotFoundException();		
	}
	
	@RequestMapping(value = "/inbound-list/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Map<String,String>> readInboundFolder(HttpServletRequest request, @PathVariable(value="id") String id)  {
	
	
		List<Map<String,String>> list = new ArrayList<>();
		try {
			IMDbId iMDbId = IMDbId.parse(id);
			
			File folder = movieDatabase.getInboundFolder(iMDbId);
			
			for(String name : folder.list())
			{
				File file = new File(folder,name);
				
				Charset charset = encodingDetector.detect(file);
								
				String language = languageDetector.detect(file,charset);

				Map<String,String> info = new HashMap<>();
				info.put("name", name);
				info.put("charset", charset.name());
				info.put("language", language);

				list.add(info);
				
				
			}
			return list;
		} catch (ParseException | IOException e) {
			logger.error(e);			
		}
		
		throw new InternalErrorException();
	
	}
	
	@RequestMapping(value = "/inbound-delete/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String,Object> deleteFileFromInboundFolder(HttpServletRequest request, 
			@PathVariable(value="id") String id,@RequestParam(value="name") String name)  {
	
	
		Map<String,Object> result = new HashMap<>();
		try {
			IMDbId iMDbId = IMDbId.parse(id);
			
			File folder = movieDatabase.getInboundFolder(iMDbId);
			File file = new File(folder,name);
			if(file.exists())
				result.put("delete", file.delete());
			else
				result.put("not exists", file.getPath());

			return result;
		} catch (ParseException | IOException e) {
			logger.error(e);			
		}
		
		throw new InternalErrorException();
	
	}
	
	@RequestMapping(value = "/inbound-convert/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String,Object> convertFileFromInboundFolder(HttpServletRequest request, 
			@PathVariable(value="id") String id,@RequestParam(value="name") String name)  {
	
	
		Map<String,Object> result = new HashMap<>();
		try {
			IMDbId iMDbId = IMDbId.parse(id);
			
			File inboundFolder = movieDatabase.getInboundFolder(iMDbId);
			File srtFile = new File(inboundFolder,name);
			
			processor.enqueue(new SrtToTextConverterTask(iMDbId,srtFile));

			return result;
		} catch (ParseException | IOException e) {
			logger.error(e);			
		}
		
		throw new InternalErrorException();
	
	}
	
	@RequestMapping(value = "/download", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String,Object> downloadZipFileFromUrl(HttpServletRequest request, @RequestParam("id") String id,
			@RequestParam("url") String url,@RequestParam("language") String language)  {
	
		Map<String,Object> result = new HashMap<>();
		try {
			IMDbId iMDbId = IMDbId.parse(id);
			
			File folder = movieDatabase.getInboundFolder(iMDbId);
			File file = importService.importFile(folder, "zip", downloadService.getInputStream(url));
			
			result.put("id", iMDbId.getId());
			result.put("file", file.getPath());

		} catch (ParseException | IOException e) {
			logger.error(e);			
		}
	
		return result;
	}
	
	

	
	
	
	@RequestMapping(value = "/file-upload/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String,String> uploadFile(MultipartHttpServletRequest request,@PathVariable(value="id") String id) throws IOException {
	   
		Map<String,String> result = new HashMap<>();
		
		try
		{
			IMDbId iMDbId = IMDbId.parse(id);
			File folder = movieDatabase.getInboundFolder(iMDbId);
		
		
	        Iterator<String> itr = request.getFileNames();
	        while (itr.hasNext()) {
	            String uploadedFile = itr.next();
	            MultipartFile mpFile = request.getFile(uploadedFile);
	            String extension = FilenameUtils.getExtension(mpFile.getOriginalFilename()).toLowerCase();
	            File file = importService.importFile(folder,extension,mpFile.getInputStream());
	        	result.put("id", iMDbId.getId());
				result.put("file", file.getPath());

	        }
	   
		} catch (ParseException | IOException e) {
			logger.error(e);			
		}
	
		return result;
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