package de.frittenburger.movievocabulary.process.interfaces;

import java.io.IOException;

public interface ProcessorTask {

	void run() throws Exception;

	String getName();

}
