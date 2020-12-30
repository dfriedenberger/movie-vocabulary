package de.frittenburger.movievocabulary.impl;

import java.io.IOException;
import java.io.InputStream;

import de.frittenburger.movievocabulary.interfaces.AnnotationWrapper;

public class Factory {



	
	

	public static AnnotationWrapper getAnnotationWrapperInstance(String language) throws IOException
    {
	
		ClassLoader cl = Factory.class.getClassLoader();
		String path = "nlp/"+language+"/nlp.txt";
		try(InputStream is = cl.getResourceAsStream(path))
		{
			if(is == null) throw new IOException(path + " not found");
			return new DefaultAnnotationWrapper(is);
		}
		
    }
}
