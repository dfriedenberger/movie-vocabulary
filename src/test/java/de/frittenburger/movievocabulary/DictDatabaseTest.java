package de.frittenburger.movievocabulary;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;




import de.frittenburger.movievocabulary.impl.DictDatabase;

public class DictDatabaseTest {

	@Test
	public void test() throws IOException {
		
		DictDatabase db = new DictDatabase(new File("dict/dict_es_de.txt"));
		fail("Not yet implemented");
	}

}
