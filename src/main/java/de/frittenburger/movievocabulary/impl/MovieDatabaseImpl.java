package de.frittenburger.movievocabulary.impl;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.frittenburger.movievocabulary.interfaces.MovieDatabase;
import de.frittenburger.movievocabulary.model.IMDbId;
import de.frittenburger.movievocabulary.model.MovieMetadata;

public class MovieDatabaseImpl implements MovieDatabase {

	private static final String METADATA = "metadata.json";
	private static final String INBOUND = "inbound";
	private final File path;

	public MovieDatabaseImpl(File path) {
		if(!path.isDirectory())
			throw new RuntimeException(path.getName()+" has to be an Directory");
		this.path = path;
	}

	@Override
	public void updateMetadata(IMDbId id, MovieMetadata metadata) throws IOException {
					
		File directory = new File(path,id.getId());
		if(!directory.exists())
			directory.mkdir();
		
		new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File(directory,METADATA), metadata);
		
		
	}

	@Override
	public File getInboundFolder(IMDbId id) throws IOException {
		File directory = new File(path,id.getId());
		
		
		if(!directory.isDirectory())
			throw new IOException("Folder "+id.getId()+" does not exists");
			
		File inbound = new File(directory,INBOUND);

		if(!inbound.exists())
			inbound.mkdir();
		
		return inbound;
	}

}
