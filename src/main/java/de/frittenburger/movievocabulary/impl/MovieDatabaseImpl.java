package de.frittenburger.movievocabulary.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.frittenburger.movievocabulary.interfaces.MovieDatabase;
import de.frittenburger.movievocabulary.model.IMDbId;
import de.frittenburger.movievocabulary.model.Language;
import de.frittenburger.movievocabulary.model.MovieMetadata;
import de.frittenburger.movievocabulary.model.Paragraph;
import de.frittenburger.movievocabulary.model.PoFile;

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
	public boolean exists(IMDbId id) {
		File folder = new File(path,id.getId());
		return folder.isDirectory();
	}
	
	@Override
	public void create(IMDbId id) throws IOException {
		File folder = new File(path,id.getId());
		if(folder.isDirectory())
			throw new IOException("Folder "+id.getId()+" yet exists");		
		folder.mkdir();
	}

	@Override
	public MovieMetadata readMetadata(IMDbId id) throws IOException {
					
		File folder = new File(path,id.getId());
		if(!folder.isDirectory())
			throw new IOException("Folder "+id.getId()+" does not exists");
		
		return new ObjectMapper().readValue(new File(folder,METADATA), MovieMetadata.class);
		
	}
	
	@Override
	public void updateMetadata(IMDbId id, MovieMetadata metadata) throws IOException {
					
		File folder = new File(path,id.getId());
		if(!folder.isDirectory())
			throw new IOException("Folder "+id.getId()+" does not exists");
		
		new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File(folder,METADATA), metadata);
		
	}

	@Override
	public List<Paragraph> readParagraphs(IMDbId id, Language language) throws IOException {
		
		File folder = new File(path,id.getId());
		if(!folder.isDirectory())
			throw new IOException("Folder "+id.getId()+" does not exists");
		
		File paragraphsFile = new File(folder,"paragraphs-"+language.getShortName()+".json");
		if(!paragraphsFile.isFile())
			throw new IOException("File "+paragraphsFile.getPath()+" does not exists");
		
		return new ObjectMapper().readValue(paragraphsFile, new TypeReference<List<Paragraph>>(){});	
		
	}

	@Override
	public void updateParagraphs(IMDbId id, Language language, List<Paragraph> paragraphs) throws IOException {
		
		File folder = new File(path,id.getId());
		if(!folder.isDirectory())
			throw new IOException("Folder "+id.getId()+" does not exists");
		
		File outfile = new File(folder,"paragraphs-"+language.getShortName()+".json");
		new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(outfile, paragraphs);		
		
	}

	
	@Override
	public PoFile readTranslation(IMDbId id, Language sourceLanguage, Language targetLanguage) throws IOException {
		
		File folder = new File(path,id.getId());
		if(!folder.isDirectory())
			throw new IOException("Folder "+id.getId()+" does not exists");
		
		File poFile = new File(folder,"paragraphs-"+sourceLanguage.getShortName()+"."+targetLanguage.getShortName()+".po");
		if(!poFile.isFile())
			throw new IOException("File "+poFile.getPath()+" does not exists");
		
		return new PoFileReader().readPoFile(poFile);
	}
	
	@Override
	public void updateTranslation(IMDbId id, Language sourceLanguage, Language targetLanguage, PoFile translation)
			throws IOException {
		
		File folder = new File(path,id.getId());
		if(!folder.isDirectory())
			throw new IOException("Folder "+id.getId()+" does not exists");
		
		File outfile = new File(folder,"paragraphs-"+sourceLanguage.getShortName()+"."+targetLanguage.getShortName()+".po");
		new PoFileWriter().writePoFile(outfile,translation);
		
	}
	
	
	@Override
	public File getBaseFolder(IMDbId id) throws IOException {
		File folder = new File(path,id.getId());
		if(!folder.isDirectory())
			throw new IOException("Folder "+id.getId()+" does not exists");
		return folder;
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
