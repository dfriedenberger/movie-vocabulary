package de.frittenburger.movievocabulary.translate.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.frittenburger.movievocabulary.convert.impl.ConvertConfig;
import de.frittenburger.movievocabulary.model.Language;
import de.frittenburger.movievocabulary.model.Paragraph;
import de.frittenburger.movievocabulary.translate.interfaces.TranslateService;

public class TranslateServiceImpl implements TranslateService {

	private static final Logger logger = LogManager.getLogger(TranslateServiceImpl.class);
	private final String bearer;
	private final String url;

	
	public TranslateServiceImpl(File configFile) throws IOException {
		Properties prop = new Properties();
		prop.load(new FileInputStream(configFile));
		this.bearer = prop.getProperty("bearer");
		this.url = prop.getProperty("url");
	}

	@Override
	public List<String> translate(List<String> text,Language source,Language target) throws IOException {

		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(this.url);

		String authHeader = "Bearer " + this.bearer;
		httppost.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("sourcelanguage", source.getShortName()));
		params.add(new BasicNameValuePair("targetlanguage", target.getShortName()));
		params.add(new BasicNameValuePair("text", String.join("\r\n",text)));
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

		//Execute and get the response.
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();

		if (entity == null) 
			throw new IOException("no response");

		try (InputStream in = entity.getContent()) {
			
			List<String> result = new ArrayList<>();
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode tree = mapper.readTree(in);
			logger.trace("Status {}",tree.get("status").asText());
			for(JsonNode n : tree.get("text"))
			{
				result.add(n.asText());
			}			
			if(result.size() != text.size())
				throw new IOException(result.size() + " != " + text.size());
			return result;
	    }
		
	}
	
	
	
	public static void main(String args[]) throws UnsupportedOperationException, IOException
	{
		List<String> text = new ArrayList<>();
		text.add("Yo vivo en Barcelona.");
		text.add("Yo");
		text.add("vivo");
		text.add("en");

		
		
		List<String> res = new TranslateConfig().getTranslateServiceInstance().translate(text, Language.spanish, Language.german);
		System.out.println(text+" => "+res);
	}

	

}
