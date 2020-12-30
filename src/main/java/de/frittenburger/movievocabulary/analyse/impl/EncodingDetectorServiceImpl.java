package de.frittenburger.movievocabulary.analyse.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.any23.encoding.TikaEncodingDetector;
import org.springframework.stereotype.Service;

import de.frittenburger.movievocabulary.analyse.interfaces.EncodingDetectorService;

@Service
public class EncodingDetectorServiceImpl implements EncodingDetectorService {

	@Override
	public Charset detect(File file) throws IOException {
		try(InputStream is = new FileInputStream(file))
		{
			return Charset.forName(new TikaEncodingDetector().guessEncoding(is));  	
		}
	}

}
