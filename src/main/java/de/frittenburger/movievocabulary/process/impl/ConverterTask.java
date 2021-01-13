package de.frittenburger.movievocabulary.process.impl;

import java.io.File;

import de.frittenburger.movievocabulary.model.VideoId;
import de.frittenburger.movievocabulary.process.interfaces.ProcessorServiceType;
import de.frittenburger.movievocabulary.process.interfaces.ProcessorTask;

public class ConverterTask extends ProcessorTask {

	private final VideoId id;
	private final File file;
	
	public ConverterTask(ProcessorServiceType type,VideoId id,File file)
	{
		super(type,type.toString());
		this.id = id;
		this.file = file;
	}
	
	public VideoId getId() {
		return id;
	}

	public File getFile() {
		return file;
	}

	

}
