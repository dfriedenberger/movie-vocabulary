package de.frittenburger.movievocabulary.inbound.interfaces;

import java.io.IOException;
import java.io.InputStream;

public interface DownloadService {

	InputStream getInputStream(String url) throws IOException;

}
