package de.frittenburger.movievocabulary.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import de.frittenburger.movievocabulary.model.PoFile;

public class PoFileReader {

	
	
	public PoFile readPoFile(File file) throws IOException {
		PoFile poFile = new PoFile();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), "UTF8"))) {

		
			String line;
			while ((line = in.readLine()) != null) {
				poFile.getLines().add(line);
			}
		}
		return poFile;
	}

	
	
	
	
}
