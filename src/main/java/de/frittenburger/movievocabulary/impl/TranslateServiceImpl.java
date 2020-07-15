package de.frittenburger.movievocabulary.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.AmazonTranslateClient;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.frittenburger.movievocabulary.interfaces.TranslateService;

public class TranslateServiceImpl implements TranslateService {

	private static final Logger logger = LogManager.getLogger(TranslateServiceImpl.class);

	private final String toLang;
	private final String fromLang;
	private final AmazonTranslate translate;

	private final String filename;
	private final Map<String, String> map = new HashMap<>();


	public TranslateServiceImpl(String fromLang, String toLang) {
	
		this.fromLang = fromLang;
		this.toLang = toLang;
		this.filename = "dict/dictionary_"+fromLang+"_"+toLang+".txt";

		Map<String, String> config = null;
		try {
			config = new ObjectMapper().readValue(new File("config/aws.json"),new TypeReference<HashMap<String,String>>() {});
		} catch (IOException e) {
			logger.error(e);
		}
		
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(config.get("accessKeyId"), config.get("secretAccessKey"));

		 translate = AmazonTranslateClient.builder()
	                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
	                .withRegion(config.get("region"))
	                .build();
        
			
		
		 
		try(
				InputStream is = new FileInputStream(filename);
				BufferedReader in = new BufferedReader(new InputStreamReader(is,StandardCharsets.UTF_8))
		   )
		{
			
			while(true)
			{
				String line = in.readLine();
				if(line == null) break;
				if(line.trim().equals("")) continue;
				if(line.trim().startsWith("#")) continue;
				int i = line.indexOf("::");
				if(i < 0) continue;
				String key = line.substring(0, i).trim();
				String[] values = line.substring(i+2).trim().split(";");
				map.put(key, values[0]);
			}
		
		} catch (IOException e) {
			logger.error(e);
		}	
				
		
	}

	@Override
	public String translate(String text) {

		if(map.containsKey(text))
			return map.get(text);
		
		
		
		  TranslateTextRequest request = new TranslateTextRequest()
	        .withText(text)
	        .withSourceLanguageCode(fromLang)
	        .withTargetLanguageCode(toLang);

		TranslateTextResult result  = translate.translateText(request);
		String translation = result.getTranslatedText();
		if(translation == null) 
			return null;
		
		map.put(text, translation);
		
		
		try (OutputStreamWriter writer =
	             new OutputStreamWriter(new FileOutputStream(new File(filename),true), StandardCharsets.UTF_8))
	    {
			writer.write(text +"::"+translation+"\r\n");
	    } 
		catch(IOException e)
		{
	    	logger.error(e);
		}
		
		return translation;
	}
	

}
