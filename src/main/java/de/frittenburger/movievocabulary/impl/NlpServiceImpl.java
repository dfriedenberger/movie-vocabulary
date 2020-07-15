package de.frittenburger.movievocabulary.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;










import de.frittenburger.movievocabulary.interfaces.AnnotationWrapper;
import de.frittenburger.movievocabulary.interfaces.NlpService;
import de.frittenburger.movievocabulary.interfaces.WordFrequencyService;
import de.frittenburger.movievocabulary.model.Annotation;
import de.frittenburger.movievocabulary.model.Paragraph;
import de.frittenburger.movievocabulary.model.Sentence;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class NlpServiceImpl implements NlpService {

	private final StanfordCoreNLP pipeline;
	private final WordFrequencyService frequencyService;
	private final AnnotationWrapper wrapper;
	private final String language;
	private final Map<String,String> unknown;
	public NlpServiceImpl(String language, Map<String, String> unknown) throws IOException {
		
		this.language =language;
		this.frequencyService = Factory.getWordFrequencyServiceInstance(language);
		this.pipeline = Factory.getPipelineInstance(language);
		this.wrapper = Factory.getAnnotationWrapperInstance(language);
		this.unknown = unknown;
	}
	
	
	@Override
	public Paragraph parse(String text) {
		
		Paragraph paragraph = new Paragraph();

	    // create an empty Annotation just with the given text
		edu.stanford.nlp.pipeline.Annotation document = new edu.stanford.nlp.pipeline.Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);
		
		
	
		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and
		// has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for (CoreMap sent : sentences) {

			Sentence sentence = new Sentence();
			paragraph.addSentencesItem(sentence);
			List<Annotation> annotations = new ArrayList<>();
			sentence.setAnnotations(annotations);
			sentence.setText(sent.toString());
			
			String token = "";
			
			for (CoreLabel tok : sent.get(TokensAnnotation.class)) {

			
			
				// this is the text of the token
				String word = tok.get(TextAnnotation.class);
				
				// this is the POS tag of the token
				String pos = tok.get(PartOfSpeechAnnotation.class);

				//TODO pos
			
				// this is the NER label of the token
				String ne = tok.get(NamedEntityTagAnnotation.class);
				//Todo ne

				
				token += ne+":"+pos+" ";
				
				String type = wrapper.map(pos, ne);
				
				if(type.equals(AnnotationWrapper.UNKNOWN))
				{
					System.err.println("Unknown wrap for '"+word+"' lang="+language+" pos="+pos+" ne="+ne);
					unknown.put(ne+":"+pos,ne+":"+pos+" I:IGNORE #"+word);
				}
				if(type.startsWith("I:")) continue;
				
				int level = -1;
				
				if(type.startsWith("T:"))
					level = frequencyService.level(word);
				Annotation annotation = new Annotation();
				annotation.setKey(word);
				annotation.setLevel(level);
				annotation.setType(type);
				annotations.add(annotation);
							   
				//System.out.println(word + " " + pos + " " + ne);

			}
			sentence.setToken(token);

			
		}
		
		
		
		return paragraph;
	}

}
