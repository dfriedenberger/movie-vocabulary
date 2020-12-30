package de.frittenburger.movievocabulary.opensubtitle.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;

import com.github.wtekiela.opensub4j.api.OpenSubtitlesClient;
import com.github.wtekiela.opensub4j.impl.OpenSubtitlesClientImpl;
import com.github.wtekiela.opensub4j.response.ListResponse;
import com.github.wtekiela.opensub4j.response.Response;
import com.github.wtekiela.opensub4j.response.ResponseStatus;
import com.github.wtekiela.opensub4j.response.SubtitleInfo;

import de.frittenburger.movievocabulary.model.IMDbId;
import de.frittenburger.movievocabulary.opensubtitle.interfaces.OpenSubtitleService;
import de.frittenburger.movievocabulary.opensubtitle.model.OpenSubtitle;

public class OpenSubtitleServiceImpl implements OpenSubtitleService {

	private static final Logger logger = LogManager.getLogger(OpenSubtitleServiceImpl.class);

	private String user;
	private String pass;
	private String agent;


	public OpenSubtitleServiceImpl(File configFile) throws IOException {
		Properties prop = new Properties();
		prop.load(new FileInputStream(configFile));
		this.user = prop.getProperty("user");
		this.pass = prop.getProperty("pass");
		this.agent = prop.getProperty("agent");
	}

	@Override
	public List<OpenSubtitle> search(IMDbId iMDbId,String language) throws XmlRpcException, IOException {

		
		URL serverUrl = new URL("https", "api.opensubtitles.org", 443, "/xml-rpc");
		OpenSubtitlesClient osClient = new OpenSubtitlesClientImpl(serverUrl);
		
		Response response = osClient.login(this.user, this.pass, "en", this.agent);

		// checking login status
		assert response.getStatus() == ResponseStatus.OK;
		assert osClient.isLoggedIn() == true;

		
		ListResponse<SubtitleInfo> lresponse = osClient.searchSubtitles(language, iMDbId.getNr());
		List<SubtitleInfo> subtitles = lresponse.getData();
		
		
		List<OpenSubtitle> opensubtitles = new ArrayList<>();
		for(SubtitleInfo subtitle : subtitles)
		{
			logger.info(subtitle);
			OpenSubtitle opensubtitle = new OpenSubtitle();
			opensubtitle.setId(iMDbId.getId());
			opensubtitle.setFileName(subtitle.getFileName());
			opensubtitle.setLanguage(subtitle.getLanguage());
			opensubtitle.setFormat(subtitle.getFormat());
			opensubtitle.setEncoding(subtitle.getEncoding());
			opensubtitle.setDownloadlink(subtitle.getZipDownloadLink());
			opensubtitles.add(opensubtitle);
		}

		return opensubtitles;
		
	}


	
}
