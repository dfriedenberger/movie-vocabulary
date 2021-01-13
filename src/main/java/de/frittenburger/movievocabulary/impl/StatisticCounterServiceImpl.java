package de.frittenburger.movievocabulary.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import de.frittenburger.movievocabulary.interfaces.StatisticCounterService;
import de.frittenburger.movievocabulary.model.Paragraph;
import de.frittenburger.movievocabulary.model.Sentence;
import de.frittenburger.movievocabulary.model.Token;

@Service
public class StatisticCounterServiceImpl implements StatisticCounterService {

	@Override
	public Map<String, Integer> count(List<Paragraph> paragraphs) {
		
		Map<String,Integer> info = new HashMap<>();
		info.put("paragraphs",paragraphs.size());
		info.put("sentences",0);

		for(Paragraph paragraph : paragraphs)
			for(Sentence sentence : paragraph.getSentences())
			{
				increment(info,"sentences");					
				for(Token annotation : sentence.getTokens())
				{
					String key = annotation.getPartOfSpeech();
					increment(info,key);
					increment(info,"words");
				}
			}
		
		return info;
	}

	private static void increment(Map<String, Integer> map, String key) {

		if(!map.containsKey(key)) map.put(key, 0);
		
		int count = map.get(key);
		count++;
		map.put(key, count);
		
	}
}
