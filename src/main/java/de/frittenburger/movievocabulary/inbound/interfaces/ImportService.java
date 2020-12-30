package de.frittenburger.movievocabulary.inbound.interfaces;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface ImportService {

	File importFile(File folder, String extension, InputStream inputStream) throws IOException;

}
