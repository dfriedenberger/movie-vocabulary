package de.frittenburger.movievocabulary.process.impl;

import java.io.File;

import de.frittenburger.movievocabulary.model.IMDbId;
import de.frittenburger.movievocabulary.model.Language;
import de.frittenburger.movievocabulary.process.interfaces.ProcessorServiceType;
import de.frittenburger.movievocabulary.process.interfaces.ProcessorTask;

public class TranslateTask extends ProcessorTask {

	private final IMDbId id;
	private final File file;
	
	private final Language sourceLanguage;
	private final Language targetLanguage;

	public TranslateTask(ProcessorServiceType type,IMDbId id,File file,Language sourceLanguage,Language targetLanguage)
	{
		super(type,type.toString());
		this.id = id;
		this.file = file;
		this.sourceLanguage = sourceLanguage;
		this.targetLanguage = targetLanguage;
	}
	
	public IMDbId getId() {
		return id;
	}

	public File getFile() {
		return file;
	}

	public Language getSourceLanguage() {
		return sourceLanguage;
	}

	public Language getTargetLanguage() {
		return targetLanguage;
	}

}
