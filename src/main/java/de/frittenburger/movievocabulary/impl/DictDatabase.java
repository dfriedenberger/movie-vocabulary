package de.frittenburger.movievocabulary.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class DictDatabase {

	public DictDatabase(File file) throws IOException {

		try(
				InputStream is = new FileInputStream(file);
				BufferedReader in = new BufferedReader(new InputStreamReader(is,StandardCharsets.UTF_8))
		   )
		{
			
			while(true)
			{
				String line = in.readLine();
				if(line == null) break;
				if(line.trim().equals("")) continue;
				if(line.trim().startsWith("#")) continue;
				System.out.println(line);
			}
		}
	
	}

}
