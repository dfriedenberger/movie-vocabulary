package de.frittenburger.movievocabulary.opensubtitle.interfaces;

import java.io.IOException;
import java.util.List;

import org.apache.xmlrpc.XmlRpcException;

import de.frittenburger.movievocabulary.model.IMDbId;
import de.frittenburger.movievocabulary.opensubtitle.model.OpenSubtitle;

public interface OpenSubtitleService {


	List<OpenSubtitle> search(IMDbId iMDbId, String language) throws XmlRpcException, IOException;

}
