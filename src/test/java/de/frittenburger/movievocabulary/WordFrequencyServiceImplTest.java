package de.frittenburger.movievocabulary;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.junit.Test;

import de.frittenburger.movievocabulary.impl.Factory;
import de.frittenburger.movievocabulary.interfaces.WordFrequencyService;

public class WordFrequencyServiceImplTest {

	@Test
	public void test() throws IOException {
		
		WordFrequencyService frequencyService = Factory.getWordFrequencyServiceInstance("es");
		
		assertThat(frequencyService.level("Hola")).isEqualTo(0);
		
		assertThat(frequencyService.level("ignorar")).isEqualTo(4);
	}

}
