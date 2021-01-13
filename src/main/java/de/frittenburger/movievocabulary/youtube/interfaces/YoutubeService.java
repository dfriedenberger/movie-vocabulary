package de.frittenburger.movievocabulary.youtube.interfaces;

import java.io.IOException;
import java.util.List;

import de.frittenburger.movievocabulary.model.YoutubeId;
import de.frittenburger.movievocabulary.youtube.model.YoutubeVideo;
import de.frittenburger.movievocabulary.youtube.model.YoutubeVideoInfo;

public interface YoutubeService {

	List<YoutubeVideo> search(String q) throws IOException;

	YoutubeVideoInfo get(YoutubeId youtubeId) throws IOException;
}
