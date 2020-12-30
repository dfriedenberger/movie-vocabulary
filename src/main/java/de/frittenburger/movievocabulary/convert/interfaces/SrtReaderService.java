package de.frittenburger.movievocabulary.convert.interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import de.frittenburger.movievocabulary.model.SrtRecord;

public interface SrtReaderService {

	List<SrtRecord> read(InputStream is, Charset charset, Filter filter) throws IOException;

}
