package de.frittenburger.movievocabulary.inbound.interfaces;

import java.io.IOException;
import java.io.InputStream;

public interface UnzipService {

	InputStream extractSrt(InputStream in) throws IOException;

}
