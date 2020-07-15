package de.frittenburger.movievocabulary.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;


import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.frittenburger.movievocabulary.interfaces.LanguageDetectorService;
import de.frittenburger.movievocabulary.interfaces.MovieVocabularyService;
import de.frittenburger.movievocabulary.interfaces.NlpService;
import de.frittenburger.movievocabulary.interfaces.NlpServiceFactory;
import de.frittenburger.movievocabulary.interfaces.SrtCompressService;
import de.frittenburger.movievocabulary.interfaces.SrtReaderService;
import de.frittenburger.movievocabulary.interfaces.TranslateService;
import de.frittenburger.movievocabulary.interfaces.UnzipService;
import de.frittenburger.movievocabulary.model.Annotation;
import de.frittenburger.movievocabulary.model.Paragraph;
import de.frittenburger.movievocabulary.model.PoFile;
import de.frittenburger.movievocabulary.model.Sentence;
import de.frittenburger.movievocabulary.model.SrtRecord;

public class MovieVocabularyServiceImpl implements MovieVocabularyService {

	private static final Logger logger = LogManager.getLogger(MovieVocabularyServiceImpl.class);

	private final UnzipService unzipService;
	private final SrtReaderService srtReaderService;
	private final SrtCompressService srtCompressService;
	private final LanguageDetectorService languageDetectorService;
	private final NlpServiceFactory nlpServiceFactory;
	private final File dataFolder;
	private final List<File> queue = new ArrayList<>();

	private final Thread converter;
	private boolean runnable = true;

	public MovieVocabularyServiceImpl(UnzipService unzipService,SrtReaderService srtReaderService,
			SrtCompressService srtCompressService,LanguageDetectorService languageDetectorService,
			NlpServiceFactory nlpServiceFactory,File dataFolder) {
		
		this.unzipService = unzipService;
		this.srtReaderService = srtReaderService;
		this.srtCompressService = srtCompressService;
		this.languageDetectorService = languageDetectorService;
		this.nlpServiceFactory = nlpServiceFactory;
		this.dataFolder = dataFolder;
		
		
		converter = new Thread(){

			@Override
			public
			void run()
			{
				int ix = 0;
				while(runnable)
				{
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						logger.error(e);
					}
					File folder = null;
					synchronized(queue)
					{
						if(ix < queue.size()) 
						{
							folder = queue.get(ix++);
						}
					}
					
					if(folder == null) continue;
					
					logger.info("Start {} Converting Id={}",ix,folder.getName());
					try {
						convert(folder);
					} catch (IOException e) {
						logger.error(e);
					}
					logger.info("Stop {} Converting Id={}",ix,folder.getName());

				}
			}
		};
		converter.start();
	}

	
	
	private static String[] splitFileName(String filename) {
	   
	    int i = filename.lastIndexOf('.');
	    String ext = i > 0 ? filename.substring(i + 1) : "";
	    String name =  i > 0 ? filename.substring(0,i) : filename;
	    	    
	    return new String[]{ name.replaceAll("[^a-zA-Z0-9\\.\\-]", "_") , ext.toLowerCase()};
	    
	}
	
	
	

	@Override
	public String importFile(String originalFilename, InputStream is) throws IOException {
		
		String[] filename = splitFileName(originalFilename);
		
		switch(filename[1])
		{
		case "zip":
			is = unzipService.extractSrt(is);
			//FallThrough
		case "srt":
		
			File folder = new File(dataFolder,filename[0]);
			folder.mkdir();
			File input = new File(folder,"input.srt");
			Files.copy(is,input.toPath(), StandardCopyOption.REPLACE_EXISTING);
			
			synchronized(queue )
			{
				queue.add(folder);
			}
			return filename[0];

		}
		throw new IOException("Unknown filetype "+originalFilename);

	}
	
	
	private void convert(File folder) throws IOException {
		
		
		List<SrtRecord> records = srtReaderService.read(new FileInputStream(new File(folder,"input.srt")), "UTF-8", new DefaultFilter());
		
		//Compress
		records = srtCompressService.compress(records);
		
		//Detect language	
		List<String> examples = getExamples(records,10);
		String language = languageDetectorService.detect(examples);
		logger.info("Detect language {}",language);
		
		
		NlpService nlpService = nlpServiceFactory.getNlpService(language);
		
		//Create Paragraphs
		List<Paragraph> paragraphs = new ArrayList<>();
		for(SrtRecord rec : records)
		{
			Paragraph paragraph = nlpService.parse(rec.joinText());
			paragraphs.add(paragraph);
		}
		
		
		//Create Po-File (Translation)
		TranslateService translateService = new TranslateServiceImpl(language,"de");
		PoFile poFile = new PoFile();
		for(int i = 0;i< paragraphs.size();i++)
		{
			Paragraph paragraph = paragraphs.get(i);
			
			for(Sentence sentence : paragraph.getSentences())
			{
				logger.info("Translate "+i+"/"+paragraphs.size()+" : "+sentence.getText());

				poFile.addExtractedComment(sentence.getText());
				for(Annotation annotation : sentence.getAnnotations())
				{
					if(annotation.getLevel() < 0) continue;
					String key = annotation.getKey();
					poFile.addMessageId(key);
					String translation = translateService.translate(key);
					if(translation != null)
						poFile.addMessageStr(translation);
				}
			}
		}
					
		new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File(folder,"subtitle.json"), records);
		new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File(folder,"paragraphs.json"), paragraphs);

		new PoFileWriter().writePoFile(new File(folder,"translate.de.po"),poFile);
		
			
	

	}



	private List<String> getExamples(List<SrtRecord> records, int cnt) {
		List<String> examples = new ArrayList<>();
		for(int i = 0;i <= cnt;i++)
		{
			SrtRecord langrec = records.get((i * 27) % records.size());
			examples.add(langrec.joinText());
		}
		return examples;
	}

}
