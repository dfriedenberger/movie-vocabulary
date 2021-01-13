package de.frittenburger.movievocabulary.process.impl;

import java.io.File;

import de.frittenburger.movievocabulary.model.VideoId;
import de.frittenburger.movievocabulary.model.Language;
import de.frittenburger.movievocabulary.process.interfaces.ProcessorServiceType;
import de.frittenburger.movievocabulary.process.interfaces.ProcessorTask;

public class TranslateTask extends ProcessorTask {

	private final VideoId id;
	private final File file;
	
	private final Language sourceLanguage;
	private final Language targetLanguage;

	public TranslateTask(ProcessorServiceType type,VideoId id,File file,Language sourceLanguage,Language targetLanguage)
	{
		super(type,type.toString());
		this.id = id;
		this.file = file;
		this.sourceLanguage = sourceLanguage;
		this.targetLanguage = targetLanguage;
	}
	
	public VideoId getId() {
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
