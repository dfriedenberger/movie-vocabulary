package de.frittenburger.movievocabulary.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import de.frittenburger.movievocabulary.model.PoFile;

public class PoFileWriter {

	public void writePoFile(File file, PoFile poFile) throws IOException {
		try (OutputStreamWriter writer =
	             new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))
	    {
			for(String line : poFile.getLines())
	             writer.write(line +"\r\n");
	    }
	}

}
