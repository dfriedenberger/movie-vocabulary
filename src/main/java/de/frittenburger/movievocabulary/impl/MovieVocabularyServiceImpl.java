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

import de.frittenburger.movievocabulary.analyse.interfaces.LanguageDetectorService;
import de.frittenburger.movievocabulary.convert.interfaces.NlpService;
import de.frittenburger.movievocabulary.convert.interfaces.SrtCompressService;
import de.frittenburger.movievocabulary.convert.interfaces.SrtReaderService;
import de.frittenburger.movievocabulary.inbound.interfaces.UnzipService;
import de.frittenburger.movievocabulary.interfaces.TranslateService;
import de.frittenburger.movievocabulary.model.Annotation;
import de.frittenburger.movievocabulary.model.Paragraph;
import de.frittenburger.movievocabulary.model.PoFile;
import de.frittenburger.movievocabulary.model.Sentence;
import de.frittenburger.movievocabulary.model.SrtRecord;

public class MovieVocabularyServiceImpl {

	private static final Logger logger = LogManager.getLogger(MovieVocabularyServiceImpl.class);

	
	
	private void convert(File folder) throws IOException {
		
		
	
		
		
		/*
		
		
		//Create Po-File (Translation)
		TranslateService translateService = new TranslateServiceImpl("language","de");
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
		
			*/
	

	}



	
}
