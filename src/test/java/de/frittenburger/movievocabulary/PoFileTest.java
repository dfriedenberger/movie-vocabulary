package de.frittenburger.movievocabulary;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.frittenburger.movievocabulary.model.PoFile;

public class PoFileTest {

	@Test
	public void test() throws IOException {
		
		PoFile poFile = new PoFile();
		
		
		for(int i = 0;i < 5;i++)
		{
			poFile.addExtractedComment("comment"+i);
			poFile.addMessageId("key"+i);
			poFile.addMessageStr("value"+i);
		}
		
		assertThat(poFile.getLines()).hasSize(15);
		assertThat(poFile.createMap("comment0")).hasSize(1);
		assertThat(poFile.createMap("comment4")).hasSize(1);

		
		Map<String,String> map = new HashMap<>();
		map.put("key10", "value10");
		map.put("key11", "value11");
		
		poFile.replace("comment0", map);
		
		assertThat(poFile.getLines()).hasSize(17);
		assertThat(poFile.createMap("comment0")).hasSize(2);
		
		poFile.replace("comment4", map);
		assertThat(poFile.getLines()).hasSize(19);
		assertThat(poFile.createMap("comment4")).hasSize(2);
	}

}
