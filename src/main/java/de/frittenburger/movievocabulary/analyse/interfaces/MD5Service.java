package de.frittenburger.movievocabulary.analyse.interfaces;

import java.io.File;
import java.io.IOException;

public interface MD5Service {

	String calculate(File file) throws IOException;

}
