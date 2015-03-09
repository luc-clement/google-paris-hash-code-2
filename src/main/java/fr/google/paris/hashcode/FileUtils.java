package fr.google.paris.hashcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class FileUtils {

	private final static Logger LOGGER = Logger.getLogger(FileUtils.class);

	private static Scanner scanner = null;
	private static FileOutputStream fos = null;
	
	public static void loadFile(String filePath) {
		try {
			scanner = new Scanner(new File(filePath));
		} catch (FileNotFoundException e) {
			LOGGER.error("Couldn't load file " + filePath + ". Aboarding...");
		}
	};
	
	public static String readNextLine() {
		if (scanner == null) {
			LOGGER.debug("scanner is null. Nothing to read.");
			return "EOF";
		}
		
		if (!scanner.hasNextLine()) {
			LOGGER.debug("scanner doesn't have next line. Nothing to read.");
			scanner.close();
			return "EOF";
		}
		
		return scanner.nextLine();
	}
	
	
	public static void openFile(String destinationPath) throws FileNotFoundException {
	    File fileOut= new File(destinationPath);
	    fos = new FileOutputStream(fileOut);
	}
	
	public static void writeNewLine(String line) throws IOException {
		fos.write((line + "\n").getBytes());
	}
	
	public static void closeFile() throws IOException {
		fos.close();
	}
}
