package de.frittenburger.movievocabulary.inbound.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.frittenburger.movievocabulary.inbound.interfaces.ImportService;
import de.frittenburger.movievocabulary.inbound.interfaces.UnzipService;

public class ImportServiceImpl implements ImportService {

	private UnzipService unzipService;

	public ImportServiceImpl(UnzipService unzipService) {
		this.unzipService = unzipService;
	}

	
	public static String createFileName(String ext) {
	        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
	        return simpleDateFormat.format(new Date()) + "-" + (int) (Math.random() * 900 + 100) + "." + (ext == null ? "" : ext);
	}
	@Override
	public File importFile(File folder, String extension, InputStream is) throws IOException {

		
		switch(extension)
		{
		case "zip":
			is = unzipService.extractSrt(is);
			//FallThrough
		case "srt":
			File input = new File(folder,createFileName("srt"));
			Files.copy(is,input.toPath(), StandardCopyOption.REPLACE_EXISTING);
			return input; 
			
		}
		throw new IOException("Unknown extension "+extension);

	}

}
