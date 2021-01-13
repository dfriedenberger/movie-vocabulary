package de.frittenburger.movievocabulary.process.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import de.frittenburger.movievocabulary.analyse.interfaces.EncodingDetectorService;
import de.frittenburger.movievocabulary.analyse.interfaces.LanguageDetectorService;
import de.frittenburger.movievocabulary.convert.impl.DefaultFilter;
import de.frittenburger.movievocabulary.convert.interfaces.NlpService;
import de.frittenburger.movievocabulary.convert.interfaces.SrtCompressService;
import de.frittenburger.movievocabulary.convert.interfaces.SrtReaderService;
import de.frittenburger.movievocabulary.interfaces.MovieDatabase;
import de.frittenburger.movievocabulary.model.VideoId;
import de.frittenburger.movievocabulary.model.Language;
import de.frittenburger.movievocabulary.model.Paragraph;
import de.frittenburger.movievocabulary.model.SrtRecord;
import de.frittenburger.movievocabulary.process.interfaces.ProcessorService;
import de.frittenburger.movievocabulary.process.interfaces.ProcessorTask;

@Service
public class SrtToTextProcessorServiceImpl implements ProcessorService {

	
	private static final Logger logger = LogManager.getLogger(SrtToTextProcessorServiceImpl.class);

	@Autowired
	private SrtReaderService srtReaderService;
	
	@Autowired
	private SrtCompressService srtCompressService;
		
	@Autowired
	private LanguageDetectorService languageDetector;
	
	@Autowired
	private EncodingDetectorService encodingDetector;
	
	@Autowired
	private MovieDatabase movieDatabase;
	
	@Autowired
	private NlpService nlpService;
	
	
	@Override
	public void run(ProcessorTask task) throws IOException {
		
		if(!(task instanceof ConverterTask))
			throw new RuntimeException("not supported");
		ConverterTask srtToTextTask = ConverterTask.class.cast(task);
		
		File srtFile = srtToTextTask.getFile();
		VideoId iMDbId = srtToTextTask.getId();
		
		Charset charset = encodingDetector.detect(srtFile);
		logger.info("detected charset {} for file {}",charset,srtFile);

		Language language = languageDetector.detect(srtFile,charset);

		logger.info("detected language {} for file {}",language,srtFile);
		
		List<SrtRecord> records = srtReaderService.read(new FileInputStream(srtFile), charset, new DefaultFilter());
		logger.info("read {} records from file {}",records.size(),srtFile);

		//Compress
		records = srtCompressService.compress(records);
		logger.info("compress to {} records for file {}",records.size(),srtFile);
		
		//Create Paragraphs
		List<Paragraph> paragraphs = new ArrayList<>();
		for(int i = 0;i < records.size();i++)
		{
			SrtRecord rec = records.get(i);
			Paragraph paragraph = nlpService.parse(language,rec.joinText());
			paragraphs.add(paragraph);
			task.setProgress(((i + 1)* 100) /records.size());
		}
		
		//save
		movieDatabase.updateParagraphs(iMDbId,language,paragraphs);
	
		
		logger.info("created {} paragraphs",paragraphs.size());

	}

}
