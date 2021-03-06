package de.frittenburger.movievocabulary.convert.impl;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

import de.frittenburger.movievocabulary.convert.interfaces.NlpService;
import de.frittenburger.movievocabulary.model.Language;
import de.frittenburger.movievocabulary.model.Paragraph;


public class NlpServiceImpl implements NlpService {

	private static final Logger logger = LogManager.getLogger(NlpServiceImpl.class);
	private final String bearer;
	private final String url;

	
	public NlpServiceImpl(File configFile) throws IOException {
		Properties prop = new Properties();
		prop.load(new FileInputStream(configFile));
		this.bearer = prop.getProperty("bearer");
		this.url = prop.getProperty("url");
	}

	// keytool -trustcacerts -keystore /c/Program\ Files/Java/jdk-11.0.8/lib/security/cacerts -storepass changeit -noprompt -importcert -file letsencrypt.cer

	
	@Override
	public Paragraph parse(Language language,String text) throws UnsupportedOperationException, IOException {
		
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(this.url);

		String authHeader = "Bearer " + this.bearer;
		httppost.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("language", language.getLongName()));
		params.add(new BasicNameValuePair("text", text));
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

		//Execute and get the response.
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();

		if (entity == null) 
			throw new IOException("no response");

		try (InputStream in = entity.getContent()) {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode tree = mapper.readTree(in);
			logger.trace("Status {}",tree.get("status").asText());
			return mapper.treeToValue(tree.get("text"), Paragraph.class);

	    }
		
	}
	
	
	
	public static void main(String args[]) throws UnsupportedOperationException, IOException
	{
		Paragraph paragraph = new ConvertConfig().getNlpServiceInstance().parse(Language.spanish, "Yo vivo en Barcelona");
		new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(System.out, paragraph);
	}

	
}
