package de.frittenburger.movievocabulary;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import de.frittenburger.movievocabulary.impl.ReadabilityIndexServiceImpl;
import de.frittenburger.movievocabulary.interfaces.ReadabilityIndexService;

public class ReadabilityIndexServiceImplTest {

	@Test
	public void test() {
		
		ReadabilityIndexService service = new ReadabilityIndexServiceImpl();
		
		
		assertThat(service.calculate("Hello World")).isEqualTo(4);
		assertThat(service.calculate("Pero en la escuela no había lugar para eso.")).isEqualTo(11);

		
		
	}

}
