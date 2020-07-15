package de.frittenburger.movievocabulary.impl;

import java.util.regex.Pattern;

public class AnnotationMapper {

	private final String value;
	private final String regex;

	public AnnotationMapper(String pattern, String value) {
		this.regex = "^" + pattern.replaceAll("[.$]", "[$0]").replace("*", ".*") + "$";
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public boolean match(String text) {
		return Pattern.matches(regex,text);
	}

	

}
