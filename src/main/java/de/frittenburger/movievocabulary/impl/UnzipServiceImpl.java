package de.frittenburger.movievocabulary.impl;


import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.frittenburger.movievocabulary.interfaces.UnzipService;



public class UnzipServiceImpl implements UnzipService {

	private static final Logger logger = LogManager.getLogger(UnzipServiceImpl.class);
	private static final String SRT_EXTENSION = ".srt";

	@Override
	public
	InputStream extractSrt(InputStream in) throws IOException
	{

    	//get the zip file content
    	ZipInputStream zis = new ZipInputStream(in);
    	//get the zipped file list entry
    	ZipEntry ze = zis.getNextEntry();
    		
    	while(ze!=null){
    			
    	   String fileName = ze.getName();
           logger.info("unzip {}" , fileName);

    	   if(fileName.toLowerCase().endsWith(SRT_EXTENSION))
    	   {
    		   return zis;
    	   }
           ze = zis.getNextEntry();
    	}
	    	
        zis.closeEntry();
    	zis.close();
	    		
	   
		throw new IOException("No subtitle file found");
	}

}
