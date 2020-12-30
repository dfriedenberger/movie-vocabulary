package de.frittenburger.movievocabulary.process.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.frittenburger.movievocabulary.analyse.interfaces.EncodingDetectorService;
import de.frittenburger.movievocabulary.analyse.interfaces.LanguageDetectorService;
import de.frittenburger.movievocabulary.convert.interfaces.NlpService;
import de.frittenburger.movievocabulary.convert.interfaces.SrtCompressService;
import de.frittenburger.movievocabulary.convert.interfaces.SrtReaderService;
import de.frittenburger.movievocabulary.impl.DefaultFilter;
import de.frittenburger.movievocabulary.impl.MovieVocabularyServiceImpl;
import de.frittenburger.movievocabulary.model.IMDbId;
import de.frittenburger.movievocabulary.model.Paragraph;
import de.frittenburger.movievocabulary.model.SrtRecord;
import de.frittenburger.movievocabulary.process.interfaces.ProcessorTask;

public class SrtToTextConverterTask implements ProcessorTask {

	private static final Logger logger = LogManager.getLogger(SrtToTextConverterTask.class);

	@Autowired
	private SrtReaderService srtReaderService;
	
	@Autowired
	private SrtCompressService srtCompressService;
		
	@Autowired
	private LanguageDetectorService languageDetector;
	
	@Autowired
	private EncodingDetectorService encodingDetector;
	
	@Autowired
	private NlpService nlpService;
	
	
	private final IMDbId id;
	private final File srtFile;

	public SrtToTextConverterTask(IMDbId id, File srtFile) {
		this.id = id;
		this.srtFile = srtFile;
	}

	@Override
	public void run() throws IOException {

		Charset charset = encodingDetector.detect(srtFile);
		logger.info("detected charset {} for file {}",charset,srtFile);

		String language = languageDetector.detect(srtFile,charset);

		logger.info("detected language {} for file {}",language,srtFile);
		
		List<SrtRecord> records = srtReaderService.read(new FileInputStream(srtFile), charset, new DefaultFilter());
		logger.info("read {} records from file {}",records.size(),srtFile);

		//Compress
		records = srtCompressService.compress(records);
		logger.info("compress to {} records for file {}",records.size(),srtFile);
		
		//Create Paragraphs
		List<Paragraph> paragraphs = new ArrayList<>();
		for(SrtRecord rec : records)
		{
			Paragraph paragraph = nlpService.parse(language,rec.joinText());
			paragraphs.add(paragraph);
		}
		logger.info("created {} paragraphs for file {}",paragraphs.size(),srtFile);

	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

}
