package de.frittenburger.movievocabulary.process.interfaces;

import de.frittenburger.movievocabulary.process.model.ProcessorTaskStatus;

abstract public class ProcessorTask {

	private ProcessorTaskStatus status = ProcessorTaskStatus.INIT;
	private int progress = 0;
	
	private final ProcessorServiceType type;
	private final String name;

	public ProcessorTask(ProcessorServiceType type, String name) {
		this.type = type;
		this.name = name;
	}

	public void setProcessorTaskStatus(ProcessorTaskStatus status) {
		this.status = status;
	}
	
	public ProcessorTaskStatus getProcessorTaskStatus() {
		return status;
	}
	
	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public ProcessorServiceType getService() {
		return type;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "ProcessorTask [status=" + status + ", type=" + type + ", name=" + name + "]";
	}

	




}
