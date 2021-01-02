package de.frittenburger.movievocabulary.analyse.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import de.frittenburger.movievocabulary.analyse.interfaces.MD5Service;

@Service
public class MD5ServiceImpl implements MD5Service {

	@Override
	public String calculate(File file) throws IOException {
		try (InputStream is = Files.newInputStream(file.toPath())) {
		    return DigestUtils.md5Hex(is);
		}
	}

}
