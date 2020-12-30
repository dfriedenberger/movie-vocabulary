package de.frittenburger.movievocabulary.convert.impl;



import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.frittenburger.movievocabulary.convert.interfaces.NlpService;
import de.frittenburger.movievocabulary.model.Paragraph;
import de.frittenburger.movievocabulary.omdb.impl.JsonToMovieInfoMapper;
import de.frittenburger.movievocabulary.omdb.model.OMDbMovieInfo;


public class NlpServiceImpl implements NlpService {


	private final static Function<JsonNode,Paragraph>  converter = new JsonToParagraphMapper();

	// keytool -trustcacerts -keystore /c/Program\ Files/Java/jdk-11.0.8/lib/security/cacerts -storepass changeit -noprompt -importcert -file letsencrypt.cer
	
	@Override
	public Paragraph parse(String language,String text) throws UnsupportedOperationException, IOException {
		
		HttpClient httpclient = HttpClients.createDefault();
		
		HttpPost httppost = new HttpPost("https://text.frittenburger.de/parse");

		String authHeader = "Bearer " + "DummyBearer";
		httppost.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("language", language));
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
			return converter.apply(tree);
	    }
		
	}
	
	
	public static void main(String args[]) throws UnsupportedOperationException, IOException
	{
		new NlpServiceImpl().parse("spanish", "Yo vivo en Barcelona");
	}
}
