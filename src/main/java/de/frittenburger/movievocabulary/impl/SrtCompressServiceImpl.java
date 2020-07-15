package de.frittenburger.movievocabulary.impl;

import java.util.ArrayList;
import java.util.List;

import de.frittenburger.movievocabulary.interfaces.SrtCompressService;
import de.frittenburger.movievocabulary.model.SrtRecord;

public class SrtCompressServiceImpl implements SrtCompressService {

	private final long distance;

	public SrtCompressServiceImpl(long distance) {
		this.distance = distance;
	}

	@Override
	public List<SrtRecord> compress(List<SrtRecord> records) {

		
		List<SrtRecord> recs = new ArrayList<SrtRecord>();
		
		for(int i = 0;i < records.size();i++)
		{
			SrtRecord r = records.get(i);
			
			if(i == 0)
			{
				recs.add(r);
				continue;
			}
			
			SrtRecord p = recs.get(recs.size()-1);
			
			long dist = r.getFrom() - p.getTo();
			if(dist > distance)
			{
				recs.add(r);
				continue;
			}
			
			//join
			for(String t : r.getText())
				p.addText(t);
			p.setTime(p.getFrom(), r.getTo());
			
			
			
		}
		return recs;
	}



}
