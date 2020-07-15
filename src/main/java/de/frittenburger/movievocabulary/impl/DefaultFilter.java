package de.frittenburger.movievocabulary.impl;

import de.frittenburger.movievocabulary.interfaces.Filter;


public class DefaultFilter implements Filter {

	//@Override
	public String filter(String str) {
		
		while(true)
		{
			int s = str.indexOf("(");
			int e = str.indexOf(")");
			if(s < 0) break;
			if(e <= s) break;
			str = str.substring(0,s).trim()+" "+str.substring(e+1).trim();
			
		}
		
		while(true)
		{
			int s = str.indexOf("<");
			int e = str.indexOf(">");
			if(s < 0) break;
			if(e <= s) break;
			str = str.substring(0,s).trim()+" "+str.substring(e+1).trim();
			
		}
		while(true)
		{
			int s = str.indexOf("[");
			int e = str.indexOf("]");
			if(s < 0) break;
			if(e <= s) break;
			str = str.substring(0,s).trim()+" "+str.substring(e+1).trim();
			
		}
		
		
		if(str.trim().startsWith("-"))
		{
			int i = str.indexOf("-");
			str = str.substring(i+1).trim();
		}
		
		
		if(str.trim().startsWith("..."))
		{
			int i = str.indexOf("...");
			str = str.substring(i+3).trim();
		}
		
		if(str.trim().endsWith("..."))
		{
			int i = str.lastIndexOf("...");
			str = str.substring(0,i).trim();
		}
		
		char c[] = str.toCharArray();
		int cb = 0;
		int cs = 0;
		for(int i = 0;i < c.length;i++)
		{
			if('a' <= c[i] && c[i] <= 'z') cs++;
			if('A' <= c[i] && c[i] <= 'Z') cb++;
		}
		
		if(cs == 0 && cb >= 5) return "";
		
		return str;
	}

}
