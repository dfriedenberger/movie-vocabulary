package de.frittenburger.movievocabulary;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.junit.Test;

import de.frittenburger.movievocabulary.impl.TranslateServiceImpl;
import de.frittenburger.movievocabulary.interfaces.TranslateService;

public class TranslateServiceImplTest {

	@Test
	public void test() throws IOException {
		
		TranslateService service = new TranslateServiceImpl("es","de");
		

		String trans = service.translate("depilación");
		assertThat(trans).isEqualTo("Haarentfernung");
		
	
	}

}
