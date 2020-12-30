package de.frittenburger.movievocabulary.convert.impl;

import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;

import de.frittenburger.movievocabulary.model.Paragraph;

public class JsonToParagraphMapper implements Function<JsonNode, Paragraph> {

	@Override
	public Paragraph apply(JsonNode t) {
		// {"text":{"sentences":[{"tokens":[{"text":"Yo","namedEntity":"O","partOfSpeech":"pronoun","level":1},{"text":"vivo","namedEntity":"O","partOfSpeech":"verb","level":9},{"text":"en","namedEntity":"O","partOfSpeech":"adposition","level":0},{"text":"Barcelona","namedEntity":"CITY","partOfSpeech":"proper-noun","level":-1}]}]},"status":"OK"}

		return null;
	}

}
