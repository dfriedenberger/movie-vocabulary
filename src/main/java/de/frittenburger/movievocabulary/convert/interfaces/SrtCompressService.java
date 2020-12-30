package de.frittenburger.movievocabulary.convert.interfaces;

import java.util.List;

import de.frittenburger.movievocabulary.model.SrtRecord;

public interface SrtCompressService {

	List<SrtRecord> compress(List<SrtRecord> records);

}
