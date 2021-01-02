package de.frittenburger.movievocabulary.process.impl;

import java.io.File;

import de.frittenburger.movievocabulary.model.IMDbId;
import de.frittenburger.movievocabulary.process.interfaces.ProcessorServiceType;
import de.frittenburger.movievocabulary.process.interfaces.ProcessorTask;

public class ConverterTask extends ProcessorTask {

	private final IMDbId id;
	private final File file;
	
	public ConverterTask(ProcessorServiceType type,IMDbId id,File file)
	{
		super(type,type.toString());
		this.id = id;
		this.file = file;
	}
	
	public IMDbId getId() {
		return id;
	}

	public File getFile() {
		return file;
	}

	

}
