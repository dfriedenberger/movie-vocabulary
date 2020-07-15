package de.frittenburger.movievocabulary.interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import de.frittenburger.movievocabulary.model.SrtRecord;

public interface SrtReaderService {

	List<SrtRecord> read(InputStream is, String encoding, Filter filter)
			throws IOException;

}
