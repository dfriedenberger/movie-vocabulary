package de.frittenburger.movievocabulary.analyse.interfaces;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public interface EncodingDetectorService {

	Charset detect(File file) throws IOException;

}
