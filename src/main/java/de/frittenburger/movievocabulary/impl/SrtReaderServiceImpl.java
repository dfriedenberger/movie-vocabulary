package de.frittenburger.movievocabulary.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.frittenburger.movievocabulary.interfaces.Filter;
import de.frittenburger.movievocabulary.interfaces.SrtReaderService;
import de.frittenburger.movievocabulary.model.SrtRecord;

public class SrtReaderServiceImpl implements SrtReaderService {

	private static final String UTF8_BOM = "\uFEFF";

	private static final Pattern numberLine = Pattern.compile("^(\\d+)$");
	private static final Pattern timeLine = Pattern
			.compile("(\\d+):(\\d+):(\\d+),(\\d+) --> (\\d+):(\\d+):(\\d+),(\\d+)");

	@Override
	public List<SrtRecord> read(InputStream is, String encoding, Filter filter)
			throws IOException {

		BufferedReader in = new BufferedReader(new InputStreamReader(is,
				encoding));

		List<SrtRecord> records = null;
		String str;
		SrtRecord rec = null;
		int line = 0;
		int recordNumber = -1;
		while ((str = in.readLine()) != null) {

			line++;

			// BOM am Anfang entfernen
			if (line == 1 && str.startsWith(UTF8_BOM)) {
				str = str.substring(1);
			}

			Matcher mNumber = numberLine.matcher(str.trim());
			if (mNumber.find()) {
				// it's a Number first Line?
				recordNumber = Integer.parseInt(mNumber.group(1));
				continue;
			}

			Matcher mTime = timeLine.matcher(str);
			if (mTime.find()) {
				rec = new SrtRecord();
				rec.setId(recordNumber);
				// set time
				long msec = Integer.parseInt(mTime.group(1)) * 60 * 60 * 1000
						+ Integer.parseInt(mTime.group(2)) * 60 * 1000
						+ Integer.parseInt(mTime.group(3)) * 1000
						+ Integer.parseInt(mTime.group(4));

				long mto = Integer.parseInt(mTime.group(5)) * 60 * 60 * 1000
						+ Integer.parseInt(mTime.group(6)) * 60 * 1000
						+ Integer.parseInt(mTime.group(7)) * 1000
						+ Integer.parseInt(mTime.group(8));
				rec.setTime(msec, mto);
				continue;
			}

			// filter
			str = filter.filter(str);

			if (str.trim().isEmpty()) {
				// ignore
				continue;
			}

			if (rec == null)
				throw new RuntimeException("Unknown process line=" + line);

			if (records == null)
				records = new ArrayList<>();

			if (!records.contains(rec))
				records.add(rec);

			rec.addText(str.trim());
		}

		in.close();
		return records;

	}

}
