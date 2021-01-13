package de.frittenburger.movievocabulary.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.regex.Matcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.frittenburger.movievocabulary.interfaces.MovieDatabase;
import de.frittenburger.movievocabulary.model.VideoId;
import de.frittenburger.movievocabulary.model.Language;
import de.frittenburger.movievocabulary.model.MovieMetadata;
import de.frittenburger.movievocabulary.model.Paragraph;
import de.frittenburger.movievocabulary.model.PoFile;

public class MovieDatabaseImpl implements MovieDatabase {

	private static final Logger logger = LogManager.getLogger(MovieDatabaseImpl.class);

	private static final String METADATA = "metadata.json";
	private static final String INBOUND = "inbound";
	private final File path;

	public MovieDatabaseImpl(File path) {
		if(!path.isDirectory())
			throw new RuntimeException(path.getName()+" has to be an Directory");
		this.path = path;
	}

	
	
	@Override
	public List<VideoId> list() {
		
		List<VideoId> ids = new ArrayList<>();
		
		for(String id : path.list())
		{
			try
			{
				VideoId videoId = VideoId.parse(id);
				ids.add(videoId);
			}
			catch(ParseException e)
			{
				logger.error("Not parseable id "+id);
			}
		}
		return ids;
	}

	
	@Override
	public boolean exists(VideoId id) {
		File folder = new File(path,id.getId());
		return folder.isDirectory();
	}
	
	@Override
	public void create(VideoId id) throws IOException {
		File folder = new File(path,id.getId());
		if(folder.isDirectory())
			throw new IOException("Folder "+id.getId()+" yet exists");		
		folder.mkdir();
	}

	@Override
	public MovieMetadata readMetadata(VideoId id) throws IOException {
					
		File folder = new File(path,id.getId());
		if(!folder.isDirectory())
			throw new IOException("Folder "+id.getId()+" does not exists");
		
		return new ObjectMapper().readValue(new File(folder,METADATA), MovieMetadata.class);
		
	}
	
	@Override
	public void updateMetadata(VideoId id, MovieMetadata metadata) throws IOException {
					
		File folder = new File(path,id.getId());
		if(!folder.isDirectory())
			throw new IOException("Folder "+id.getId()+" does not exists");
		
		new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File(folder,METADATA), metadata);
		
	}

	
	
	
	@Override
	public List<Language> getParagraphLanguages(VideoId id) throws IOException {
		File folder = new File(path,id.getId());
		if(!folder.isDirectory())
			throw new IOException("Folder "+id.getId()+" does not exists");
		
        Pattern r = Pattern.compile("paragraphs[-]([a-z]+)[.]json");

        List<Language> languages = new ArrayList<>();
		
		for(String name : folder.list())
		{
			 Matcher m = r.matcher(name);
		     if (!m.find()) continue;
		     Language language = Language.parse(m.group(1));
		     languages.add(language);
		}
		return languages;
	}
	
	@Override
	public List<Paragraph> readParagraphs(VideoId id, Language language) throws IOException {
		
		File folder = new File(path,id.getId());
		if(!folder.isDirectory())
			throw new IOException("Folder "+id.getId()+" does not exists");
		
		File paragraphsFile = new File(folder,"paragraphs-"+language.getShortName()+".json");
		if(!paragraphsFile.isFile())
			throw new IOException("File "+paragraphsFile.getPath()+" does not exists");
		
		return new ObjectMapper().readValue(paragraphsFile, new TypeReference<List<Paragraph>>(){});	
		
	}

	@Override
	public void updateParagraphs(VideoId id, Language language, List<Paragraph> paragraphs) throws IOException {
		
		File folder = new File(path,id.getId());
		if(!folder.isDirectory())
			throw new IOException("Folder "+id.getId()+" does not exists");
		
		File outfile = new File(folder,"paragraphs-"+language.getShortName()+".json");
		new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(outfile, paragraphs);		
		
	}

	
	@Override
	public PoFile readTranslation(VideoId id, Language sourceLanguage, Language targetLanguage) throws IOException {
		
		File folder = new File(path,id.getId());
		if(!folder.isDirectory())
			throw new IOException("Folder "+id.getId()+" does not exists");
		
		File poFile = new File(folder,"translation-"+sourceLanguage.getShortName()+"."+targetLanguage.getShortName()+".po");
		if(!poFile.isFile())
			throw new IOException("File "+poFile.getPath()+" does not exists");
		
		return new PoFileReader().readPoFile(poFile);
	}
	
	@Override
	public void updateTranslation(VideoId id, Language sourceLanguage, Language targetLanguage, PoFile translation)
			throws IOException {
		
		File folder = new File(path,id.getId());
		if(!folder.isDirectory())
			throw new IOException("Folder "+id.getId()+" does not exists");
		
		File outfile = new File(folder,"translation-"+sourceLanguage.getShortName()+"."+targetLanguage.getShortName()+".po");
		new PoFileWriter().writePoFile(outfile,translation);
		
	}
	
	
	@Override
	public File getBaseFolder(VideoId id) throws IOException {
		File folder = new File(path,id.getId());
		if(!folder.isDirectory())
			throw new IOException("Folder "+id.getId()+" does not exists");
		return folder;
	}
	
	@Override
	public File getInboundFolder(VideoId id) throws IOException {
		File directory = new File(path,id.getId());
		
		
		if(!directory.isDirectory())
			throw new IOException("Folder "+id.getId()+" does not exists");
			
		File inbound = new File(directory,INBOUND);

		if(!inbound.exists())
			inbound.mkdir();
		
		return inbound;
	}



	@Override
	public Set<String> readValidation(VideoId id, Language sourceLanguage, Language targetLanguage) throws IOException {
		File folder = new File(path,id.getId());
		if(!folder.isDirectory())
			throw new IOException("Folder "+id.getId()+" does not exists");
		
		File validationFile = new File(folder,"validation-"+sourceLanguage.getShortName()+"."+targetLanguage.getShortName()+".txt");
		if(!validationFile.exists()) 
			return new HashSet<>();
		return Files.readAllLines(validationFile.toPath()).stream().collect(Collectors.toSet());
	}



	@Override
	public void updateValidation(VideoId id, Language sourceLanguage, Language targetLanguage, Set<String> validation)
			throws IOException {
		
		File folder = new File(path,id.getId());
		if(!folder.isDirectory())
			throw new IOException("Folder "+id.getId()+" does not exists");
		
		File validationFile = new File(folder,"validation-"+sourceLanguage.getShortName()+"."+targetLanguage.getShortName()+".txt");
		Files.write(validationFile.toPath(), validation.stream().collect(Collectors.toList()));
		
	}



	

	
}
