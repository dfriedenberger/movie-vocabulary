package de.frittenburger.movievocabulary.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
import de.frittenburger.movievocabulary.analyse.interfaces.MD5Service;
import de.frittenburger.movievocabulary.impl.PoFileReader;
import de.frittenburger.movievocabulary.impl.ReadabilityIndexServiceImpl;
import de.frittenburger.movievocabulary.inbound.impl.DownloadServiceImpl;
import de.frittenburger.movievocabulary.inbound.impl.ImportServiceImpl;
import de.frittenburger.movievocabulary.inbound.impl.UnzipServiceImpl;
import de.frittenburger.movievocabulary.inbound.interfaces.DownloadService;
import de.frittenburger.movievocabulary.inbound.interfaces.ImportService;
import de.frittenburger.movievocabulary.interfaces.MovieDatabase;
import de.frittenburger.movievocabulary.interfaces.ReadabilityIndexService;
import de.frittenburger.movievocabulary.interfaces.StatisticCounterService;
import de.frittenburger.movievocabulary.model.Card;
import de.frittenburger.movievocabulary.model.IMDbId;
import de.frittenburger.movievocabulary.model.VideoId;
import de.frittenburger.movievocabulary.model.YoutubeId;
import de.frittenburger.movievocabulary.model.Language;
import de.frittenburger.movievocabulary.model.MovieMetadata;
import de.frittenburger.movievocabulary.model.Paragraph;
import de.frittenburger.movievocabulary.model.PoFile;
import de.frittenburger.movievocabulary.model.PoMap;
import de.frittenburger.movievocabulary.model.Sentence;
import de.frittenburger.movievocabulary.model.Token;
import de.frittenburger.movievocabulary.model.TranslatedSentence;
import de.frittenburger.movievocabulary.omdb.interfaces.OMDbService;
import de.frittenburger.movievocabulary.omdb.model.OMDbMovie;
import de.frittenburger.movievocabulary.omdb.model.OMDbMovieInfo;
import de.frittenburger.movievocabulary.opensubtitle.interfaces.OpenSubtitleService;
import de.frittenburger.movievocabulary.opensubtitle.model.OpenSubtitle;
import de.frittenburger.movievocabulary.process.impl.ConverterTask;
import de.frittenburger.movievocabulary.process.impl.TranslateTask;
import de.frittenburger.movievocabulary.process.interfaces.Processor;
import de.frittenburger.movievocabulary.process.interfaces.ProcessorServiceType;
import de.frittenburger.movievocabulary.process.model.TaskStatus;
import de.frittenburger.movievocabulary.youtube.interfaces.YoutubeService;
import de.frittenburger.movievocabulary.youtube.model.YoutubeVideoInfo;




@RestController
@RequestMapping("/api/v1/")
public class ApiController {

 
	private static final Logger logger = LogManager.getLogger(ApiController.class);

	
	private final ReadabilityIndexService service = new ReadabilityIndexServiceImpl();

	
	private final DownloadService downloadService = new DownloadServiceImpl();
	private final ImportService importService = new ImportServiceImpl(new UnzipServiceImpl());
	
	@Autowired
	private MovieDatabase movieDatabase;
	
	@Autowired
	private OMDbService omdbService;
	
	@Autowired
	private YoutubeService youtubeService;
	
	@Autowired
	private OpenSubtitleService openSubtitleService;
	
	@Autowired
	private Processor processor;
	
	@Autowired
	private LanguageDetectorService languageDetector;
	
	@Autowired
	private EncodingDetectorService encodingDetector;

	@Autowired
	private MD5Service md5Service;
	
	@Autowired
	private StatisticCounterService statisticCounterService;
	
	
	
	@RequestMapping(value = "list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Map<String,Object>> listMovies(HttpServletRequest request)
	{
		try {
			List<Map<String,Object>> movies = new ArrayList<>();
			List<VideoId> videoIds = movieDatabase.list();
			for(VideoId videoId : videoIds)
			{
				Map<String,Object> movie = new HashMap<>();
				movie.put("id", videoId.getId());
				movies.add(movie);

				MovieMetadata md = movieDatabase.readMetadata(videoId);
				movie.put("metadata", md);
				
				List<Map<String,Object>> langDataList = new ArrayList<>();
				List<Language> languages = movieDatabase.getParagraphLanguages(videoId);
				for(Language language : languages)
				{
					Map<String,Object> langData = new HashMap<>();

					langData.put("language", language);
					List<Paragraph> paragraphs = movieDatabase.readParagraphs(videoId,language);
					Map<String,Integer> statistics = statisticCounterService.count(paragraphs);
					langData.put("statistics", statistics);

					langDataList.add(langData);
				}
				movie.put("languages", langDataList);
			}
			return movies;
			
		} catch (IOException e) {
			logger.error(e);			
		}
	
		throw new InternalErrorException();		
		
		
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
	
	
	@RequestMapping(value = "/validate/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String,Object> validateId(HttpServletRequest request, @PathVariable(value="id") String id)  {
	

		Map<String,Object> result = new HashMap<>();
		try {
			VideoId videoId = VideoId.parse(id);
			
			result.put("id",videoId.getId());
			result.put("class",videoId.getClass().getSimpleName());
			result.put("exists",movieDatabase.exists(videoId));
			
			return result;
		} catch (ParseException e) {
			logger.error(e);			
		}
		
		throw new InternalErrorException();
	}
	
	@RequestMapping(value = "/read/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public MovieMetadata readMetadata(HttpServletRequest request, @PathVariable(value="id") String id)  {
	

		try {
			VideoId iMDbId = VideoId.parse(id);
			return movieDatabase.readMetadata(iMDbId);
		} catch (ParseException | IOException e) {
			logger.error(e);			
		}
	
		throw new VideoNotFoundException();		
	}
	
	
	@RequestMapping(value = "/omdb/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public OMDbMovieInfo imdbInfo(HttpServletRequest request, @PathVariable(value="id") String id)  {
	

		try {
			IMDbId iMDbId = VideoId.parse(id,IMDbId.class);
			return omdbService.get(iMDbId);
		} catch (ParseException | IOException e) {
			logger.error(e);			
		}
	
		throw new VideoNotFoundException();		
	}
	
	
	@RequestMapping(value = "/youtube/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public YoutubeVideoInfo youtubeInfo(HttpServletRequest request, @PathVariable(value="id") String id)  {
	

		try {
			YoutubeId youtubeId = VideoId.parse(id,YoutubeId.class);
			return youtubeService.get(youtubeId);
		} catch (ParseException | IOException e) {
			logger.error(e);			
		}
	
		throw new VideoNotFoundException();		
	}
	
	//
	
	
	@RequestMapping(value = "/opensubtitle/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<OpenSubtitle> opensubtitleSearch(HttpServletRequest request, @PathVariable(value="id") String id,@RequestParam("language") String language)  {
	
		try {
			IMDbId iMDbId = VideoId.parse(id,IMDbId.class);
			return openSubtitleService.search(iMDbId,language);
		} catch (ParseException  | IOException | XmlRpcException e) {
			logger.error(e);			
		}
	
		throw new VideoNotFoundException();		
	}
	
	@RequestMapping(value = "/inbound-list/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Map<String,String>> readInboundFolder(HttpServletRequest request, @PathVariable(value="id") String id)  {
	
	
		List<Map<String,String>> list = new ArrayList<>();
		try {
			VideoId videoId = VideoId.parse(id);
			
			File folder = movieDatabase.getInboundFolder(videoId);
			
			for(String name : folder.list())
			{
				File file = new File(folder,name);
				
				String md5 = md5Service.calculate(file);

				Charset charset = encodingDetector.detect(file);
								
				Language language = languageDetector.detect(file,charset);

				Map<String,String> info = new HashMap<>();
				info.put("name", name);
				info.put("charset", charset.name());
				info.put("language", language.getShortName());
				info.put("md5", md5);

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
			VideoId videoId = VideoId.parse(id);
			
			File folder = movieDatabase.getInboundFolder(videoId);
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
			VideoId iMDbId = VideoId.parse(id);
			
			File inboundFolder = movieDatabase.getInboundFolder(iMDbId);
			File srtFile = new File(inboundFolder,name);
			
			processor.enqueue(new ConverterTask(ProcessorServiceType.SrtToText, iMDbId, srtFile));

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
			VideoId iMDbId = VideoId.parse(id);
			
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
			VideoId iMDbId = VideoId.parse(id);
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
	
	@RequestMapping(value = "/queue-list/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<TaskStatus> listQueue(HttpServletRequest request, 
			@PathVariable(value="id") String id)  {
	
		try {
			VideoId.parse(id);
			List<TaskStatus> tasks = processor.list();

			//TODO filter for iMDbId
			
			return tasks;
		} catch (ParseException e) {
			logger.error(e);			
		}
		
		throw new InternalErrorException();
	
	}
	
	
	@RequestMapping(value = "/base-list/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Map<String,String>> readBaseFolder(HttpServletRequest request, @PathVariable(value="id") String id)  {
	
	
		List<Map<String,String>> list = new ArrayList<>();
		try {
			VideoId videoId = VideoId.parse(id);
			
			File folder = movieDatabase.getBaseFolder(videoId);
			
			for(File file : folder.listFiles())
			{
				if(!file.isFile()) continue;
				Map<String,String> info = new HashMap<>();
				info.put("name", file.getName());
				list.add(info);
			}
			return list;
		} catch (ParseException | IOException e) {
			logger.error(e);			
		}
		
		throw new InternalErrorException();
	
	}
	
	@RequestMapping(value = "/base-translate/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String,Object> translateFileFromBaseFolder(HttpServletRequest request, 
			@PathVariable(value="id") String id,
			@RequestParam(value="name") String name,
			@RequestParam(value="source") String sourceLanguage,
			@RequestParam(value="target") String targetLanguage)  {
	
	
		Map<String,Object> result = new HashMap<>();
		try {
			VideoId iMDbId = VideoId.parse(id);
			
			File baseFolder = movieDatabase.getBaseFolder(iMDbId);
			File paragraphFile = new File(baseFolder,name);
			
			processor.enqueue(new TranslateTask(ProcessorServiceType.Translate, iMDbId, paragraphFile,
					Language.parse(sourceLanguage),Language.parse(targetLanguage)));

			return result;
		} catch (ParseException | IOException e) {
			logger.error(e);			
		}
		
		throw new InternalErrorException();
	
	}
	
	
	
	@RequestMapping(value = "/statistics/{id}/{language}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String,Integer> info(HttpServletRequest request,
				@PathVariable(value="id") String id,@PathVariable(value="language") String language) throws IOException {
		
		try {
			VideoId iMDbId = VideoId.parse(id);
			Language sourceLanguage = Language.parse(language);
			List<Paragraph> paragraphs = movieDatabase.readParagraphs(iMDbId,sourceLanguage);
		

			Map<String,Integer> info = statisticCounterService.count(paragraphs);

			
		
			return info;
		} catch (ParseException | IOException e) {
			logger.error(e);			
		}
		
		throw new InternalErrorException();
		
	
		/*
		 * Todo calculate only once
		List<SrtRecord> srtRecords = new ObjectMapper().readValue(new File("data/"+id+"/subtitle.json"), new TypeReference<List<SrtRecord>>(){});
		long duration = srtRecords.get(srtRecords.size()-1).getTo() -  srtRecords.get(0).getFrom();
		long minutes = Math.round(duration / 60000.0);
		metadata.put("minutes", minutes);

		PoFile poFile = new PoFileReader().readPoFile(new File("data/"+id+"/translate.de.po"));
		long words = poFile.count();
		metadata.put("words", words);

		 */		
		
	}
		


	//Data
	@RequestMapping(value = "/vocabulary/{id}/{source}/{target}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<TranslatedSentence> vocabulary(HttpServletRequest request,
				@PathVariable(value="id") String id,
				@PathVariable(value="source") String source,
				@PathVariable(value="target") String target,
				@RequestParam(value="offset", required=false) Integer offset,
				@RequestParam(value="limit", required=false) Integer limit

			) throws IOException {
		
		
		if(offset == null) offset = 0;
		if(limit == null) limit = 10;

		try {
			
		
			VideoId iMDbId = VideoId.parse(id);
			Language sourceLanguage = Language.parse(source);
			Language targetLanguage = Language.parse(target);
			
			List<Paragraph> paragraphs = movieDatabase.readParagraphs(iMDbId,sourceLanguage);
			PoFile poFile = movieDatabase.readTranslation(iMDbId,sourceLanguage,targetLanguage);
			Set<String> validation = movieDatabase.readValidation(iMDbId,sourceLanguage,targetLanguage);

			
			List<TranslatedSentence> translatedSentenceList = new ArrayList<>();
	
			for(int ix = offset;ix < offset + limit && ix < paragraphs.size();ix++)
			{
				Paragraph paragraph = paragraphs.get(ix);
				for(Sentence sentence : paragraph.getSentences())
				{
				
			    	TranslatedSentence translatedSentence = new TranslatedSentence();
			    	translatedSentence.setTokens(sentence.getTokens());
			    	translatedSentence.setText(sentence.getText());
			    	translatedSentence.setValidated(validation.contains(sentence.getText()));

					int ri = service.calculate(sentence.getText());
					translatedSentence.setReadingIndex(ri);
					
					PoMap map = poFile.createMap(sentence.getText());
					for(Token annotation : sentence.getTokens())
					{
						//String type = annotation.getType();
						String key = annotation.getText();
				    	translatedSentence.getTranslation().put(key,map.get(key));
					}
					translatedSentenceList.add(translatedSentence);
				}
			}
		    return translatedSentenceList;
		} catch (ParseException | IOException e) {
			logger.error(e);			
		}
		
		throw new InternalErrorException();
		
	}
	
	

	@RequestMapping(value = "/change/{id}/{source}/{target}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TranslatedSentence change(HttpServletRequest request,@RequestBody TranslatedSentence body,
			@PathVariable(value="id") String id,
			@PathVariable(value="source") String source,
			@PathVariable(value="target") String target) throws IOException  {
	
		logger.info(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(body));
		
		try
		{
		
			VideoId iMDbId = VideoId.parse(id);
			Language sourceLanguage = Language.parse(source);
			Language targetLanguage = Language.parse(target);
			
			List<Paragraph> paragraphs = movieDatabase.readParagraphs(iMDbId,sourceLanguage);
			PoFile poFile = movieDatabase.readTranslation(iMDbId,sourceLanguage,targetLanguage);
	
			
			Set<String> validation = movieDatabase.readValidation(iMDbId,sourceLanguage,targetLanguage);

					
			for(int ix = 0;ix < paragraphs.size();ix++)
			{
				Paragraph paragraph = paragraphs.get(ix);
				for(Sentence sentence : paragraph.getSentences())
				{
					if(sentence.getText().equals(body.getText()))
					{
						//change annotation
						sentence.setTokens(body.getTokens());
						
						//change Validated
						if(body.getValidated())
							validation.add(sentence.getText());
						else
							validation.remove(sentence.getText());
						
						//change Translation
						poFile.replace(sentence.getText(),body.getTranslation());
						
						//save
						movieDatabase.updateParagraphs(iMDbId,sourceLanguage, paragraphs);
						movieDatabase.updateTranslation(iMDbId, sourceLanguage, targetLanguage, poFile);
						movieDatabase.updateValidation(iMDbId, sourceLanguage, targetLanguage, validation);

						return body;
					}
				}
			}
			
		} catch (ParseException | IOException e) {
			logger.error(e);			
		}
		
		throw new VideoNotFoundException();
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

				for(Token a : sentence.getTokens())
				{
					if(minlevel != null && a.getLevel() < minlevel) continue;
					if(maxlevel != null && a.getLevel() > maxlevel) continue;
					if(cache.contains(a.getText())) continue;
					cache.add(a.getText());
					
					Card card = new Card();
					card.setQuestion(a.getText());
					card.setAnswer(map.get(a.getText()));
					cards.add(card);
				}
			}
		}
		
		
		return cards;
		
	}
	
}