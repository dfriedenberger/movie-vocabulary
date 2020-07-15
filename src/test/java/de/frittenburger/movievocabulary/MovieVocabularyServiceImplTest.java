package de.frittenburger.movievocabulary;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.frittenburger.movievocabulary.impl.MovieVocabularyServiceImpl;
import de.frittenburger.movievocabulary.interfaces.Filter;
import de.frittenburger.movievocabulary.interfaces.LanguageDetectorService;
import de.frittenburger.movievocabulary.interfaces.MovieVocabularyService;
import de.frittenburger.movievocabulary.interfaces.NlpService;
import de.frittenburger.movievocabulary.interfaces.NlpServiceFactory;
import de.frittenburger.movievocabulary.interfaces.SrtCompressService;
import de.frittenburger.movievocabulary.interfaces.SrtReaderService;
import de.frittenburger.movievocabulary.interfaces.UnzipService;
import de.frittenburger.movievocabulary.model.Paragraph;
import de.frittenburger.movievocabulary.model.SrtRecord;


public class MovieVocabularyServiceImplTest {

	@Mock
	UnzipService unzipService;
	
	@Mock 
	SrtReaderService srtReaderService;
	
	@Mock 
	SrtCompressService srtCompressService;
	
	@Mock
	NlpService nlpService;
	
	@Mock
	NlpServiceFactory nlpServiceFactory;
	
	@Mock
	LanguageDetectorService languageDetectorService;
	
	@Rule
	public TemporaryFolder folder= new TemporaryFolder();
	 
	@Before
    public void init(){
        MockitoAnnotations.initMocks(this);
    }
	
	
	@Test
	public void test() throws IOException {
		
		InputStream zipInputStream = new ByteArrayInputStream(new byte[30]);
		InputStream srtInputStream = new ByteArrayInputStream(new byte[10]);
		File dataFolder = folder.newFolder();
		
		List<SrtRecord> rawRecords = new ArrayList<>();
		rawRecords.add(new SrtRecord());
		rawRecords.add(new SrtRecord());
		List<SrtRecord> compressRecords = new ArrayList<>();
		SrtRecord rec = new SrtRecord();
		rec.addText("Hola mundo");

		compressRecords.add(rec);

		when(unzipService.extractSrt(zipInputStream)).thenReturn(srtInputStream);
		when(srtReaderService.read(eq(srtInputStream), eq("UTF-8"), any(Filter.class))).thenReturn(rawRecords);
		when(srtCompressService.compress(eq(rawRecords))).thenReturn(compressRecords);
		Paragraph paragraph = new Paragraph();
		paragraph.setSentences(new ArrayList<>());
		when(nlpService.parse(eq("Hola mundo"))).thenReturn(paragraph);
		when(languageDetectorService.detect(eq("Hola mundo"))).thenReturn("es");
		when(nlpServiceFactory.getNlpService(eq("es"))).thenReturn(nlpService);
		MovieVocabularyService movieVocabularyService = new MovieVocabularyServiceImpl(unzipService,srtReaderService,srtCompressService,languageDetectorService,nlpServiceFactory,dataFolder);
		String id = movieVocabularyService.importFile("irgendwas.zip",zipInputStream);
		
		assertThat(id).isEqualTo("irgendwas");
		assertThat(new File(dataFolder,"irgendwas/subtitle.json").exists()).isTrue();
	}
}
