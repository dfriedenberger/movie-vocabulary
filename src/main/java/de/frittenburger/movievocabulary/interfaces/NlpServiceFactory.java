package de.frittenburger.movievocabulary.interfaces;

import java.io.IOException;

public interface NlpServiceFactory {

	NlpService getNlpService(String language) throws IOException;

}
