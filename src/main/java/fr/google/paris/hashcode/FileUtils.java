package fr.google.paris.hashcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class FileUtils {

	private final static Logger LOGGER = Logger.getLogger(FileUtils.class);

	private static Scanner scanner = null;
	private static FileOutputStream fos = null;
	
	public static void loadFile(String filePath) {
		try {
			scanner = new Scanner(new File(filePath));
			LOGGER.debug("Opening scanner on file " + filePath);
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
	
	
	public static void openFile(String destinationPath) {
	    DateFormat dateFormat = new SimpleDateFormat("-ddMM-HHmmss");
	    Date currentDate = new Date();
	    String fileName = destinationPath + "output" + dateFormat.format(currentDate);
	    
	    LOGGER.debug("Opening fileOutputStream on file " + fileName);
	    
	    try {
		    File fileOutput = new File(fileName);
			fos = new FileOutputStream(fileOutput);
			if (!fileOutput.exists()) {
				fileOutput.createNewFile();
			}
		} catch (FileNotFoundException e) {
			LOGGER.error("Unable to open fileOutputStream on file " + fileName);
		} catch (IOException e) {
			LOGGER.error("IOException while trying to open fileOutputStream.");
		}
	}
	
	public static void writeNewLine(String line) {
		try {
			fos.write((line + "\n").getBytes());
		} catch (IOException e) {
			LOGGER.error("IOException. Error while writing new line in output file.");
		}
	}
	
	public static void closeFile() {
		try {
			LOGGER.debug("Closing fileOutputStream.");
			fos.flush();
			fos.close();
		} catch (IOException e) {
			LOGGER.error("IOException. Error while closing the fileOutputStream.");
		}
	}
}
