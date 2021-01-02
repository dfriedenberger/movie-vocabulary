package de.frittenburger.movievocabulary.process.impl;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.frittenburger.movievocabulary.interfaces.MovieDatabase;
import de.frittenburger.movievocabulary.model.IMDbId;
import de.frittenburger.movievocabulary.model.Language;
import de.frittenburger.movievocabulary.model.Paragraph;
import de.frittenburger.movievocabulary.model.PoFile;
import de.frittenburger.movievocabulary.model.Sentence;
import de.frittenburger.movievocabulary.model.Token;
import de.frittenburger.movievocabulary.process.interfaces.ProcessorService;
import de.frittenburger.movievocabulary.process.interfaces.ProcessorTask;
import de.frittenburger.movievocabulary.translate.impl.TranslateServiceImpl;
import de.frittenburger.movievocabulary.translate.interfaces.TranslateService;

@Service
public class TranslateProcessorServiceImpl implements ProcessorService {
	
	private static final Logger logger = LogManager.getLogger(TranslateProcessorServiceImpl.class);
	
	@Autowired
	private MovieDatabase movieDatabase;

	@Autowired
	private TranslateService translateService;
	
	@Override
	public void run(ProcessorTask task) throws Exception {
		if(!(task instanceof TranslateTask))
			throw new RuntimeException("not supported");
		TranslateTask translateTask = TranslateTask.class.cast(task);
		
		File paragraphFile = translateTask.getFile();
		
		IMDbId iMDbId = translateTask.getId();		
		Language sourceLanguage = translateTask.getSourceLanguage();
		Language targetLanguage = translateTask.getTargetLanguage();

		List<Paragraph> paragraphs = new ObjectMapper().readValue(paragraphFile, new TypeReference<List<Paragraph>>(){});

		//Create Po-File (Translation)
		PoFile poFile = new PoFile();
		for(int i = 0;i< paragraphs.size();i++)
		{
			Paragraph paragraph = paragraphs.get(i);
			
			for(Sentence sentence : paragraph.getSentences())
			{
				logger.trace("Translate "+i+"/"+paragraphs.size()+" : "+sentence.getText());

				poFile.addExtractedComment(sentence.getText());
				for(Token token : sentence.getTokens())
				{
					if(token.getLevel() < 0) continue;
					String key = token.getText();
					poFile.addMessageId(key);
					List<String> translation = translateService.translate(Arrays.asList(new String[]{key}),sourceLanguage,targetLanguage);
					if(translation != null)
						poFile.addMessageStr(translation.get(0));
				}
			}
			task.setProgress(((i + 1)* 100) /paragraphs.size());
		}
							
		//save
		movieDatabase.updateTranslation(iMDbId,sourceLanguage,targetLanguage,poFile);
		
		logger.info("created {} translation",poFile.count());
		

	}

}
