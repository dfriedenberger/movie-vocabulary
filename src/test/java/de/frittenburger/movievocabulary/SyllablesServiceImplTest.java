package de.frittenburger.movievocabulary;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import de.frittenburger.movievocabulary.impl.SyllablesServiceImpl;
import de.frittenburger.movievocabulary.interfaces.SyllablesService;

public class SyllablesServiceImplTest {

	@Test
	public void test() {
		
		SyllablesService service = new SyllablesServiceImpl();
		
		assertThat(service.count("Eis")).isEqualTo(1);
		assertThat(service.count("Hello")).isEqualTo(2);
		assertThat(service.count("escuela")).isEqualTo(3); //TODO: 4?
		assertThat(service.count("había")).isEqualTo(2); //Todo 3?

		
		
	}

}
