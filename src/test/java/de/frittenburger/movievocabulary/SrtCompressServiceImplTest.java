package de.frittenburger.movievocabulary;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.frittenburger.movievocabulary.convert.impl.SrtCompressServiceImpl;
import de.frittenburger.movievocabulary.convert.interfaces.SrtCompressService;
import de.frittenburger.movievocabulary.model.SrtRecord;

public class SrtCompressServiceImplTest {

	@Test
	public void test() {
		
		
		int pause[] = new int[]{
			2000,
			1000,
			100,
			500,
			2000,
			600,
			700,
			2000,
			1000,
			400,
			0
		};
		
		List<SrtRecord> records = new ArrayList<>();
		int time = 20000;
		for(int i = 0;i < pause.length;i++)
		{
			SrtRecord r = new SrtRecord();
			r.addText("Text "+i);
			r.setTime(time, time+1000);
			records.add(r);
			time += 1000 + pause[i];
		}
		
		
		
		
		SrtCompressService srtCompressService = new SrtCompressServiceImpl(900);
		
		List<SrtRecord> r = srtCompressService.compress(records);
		
		assertEquals(6, r.size());
		
		assertEquals("Text 0",r.get(0).getText().get(0));
		assertEquals("Text 1",r.get(1).getText().get(0));
		assertEquals("Text 2",r.get(2).getText().get(0));
		assertEquals("Text 3",r.get(2).getText().get(1));
		assertEquals("Text 4",r.get(2).getText().get(2));
		assertEquals("Text 5",r.get(3).getText().get(0));

	}

}
