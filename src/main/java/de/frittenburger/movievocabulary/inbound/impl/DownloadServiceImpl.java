package de.frittenburger.movievocabulary.inbound.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


import de.frittenburger.movievocabulary.inbound.interfaces.DownloadService;

public class DownloadServiceImpl implements DownloadService {

	@Override
	public InputStream getInputStream(String url) throws IOException {
		return new BufferedInputStream(new URL(url).openStream());
	}

}
