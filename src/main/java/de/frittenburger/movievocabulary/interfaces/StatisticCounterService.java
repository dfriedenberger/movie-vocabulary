package de.frittenburger.movievocabulary.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.frittenburger.movievocabulary.model.Paragraph;

public interface StatisticCounterService {


	Map<String, Integer> count(List<Paragraph> paragraphs);

}
