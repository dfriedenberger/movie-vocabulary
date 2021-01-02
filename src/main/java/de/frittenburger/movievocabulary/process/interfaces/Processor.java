package de.frittenburger.movievocabulary.process.interfaces;

import java.util.List;

import de.frittenburger.movievocabulary.process.model.TaskStatus;

public interface Processor {

	void enqueue(ProcessorTask task);

	List<TaskStatus> list();

}
