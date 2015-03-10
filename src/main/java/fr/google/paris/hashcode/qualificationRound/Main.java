package fr.google.paris.hashcode.qualificationRound;

import org.apache.log4j.Logger;

import fr.google.paris.hashcode.FileUtils;

public class Main {

	private final static String inputFile = "src/main/resources/input";
	private final static String outputDirectory = "src/main/resources/output/";
	
	private final static Logger LOGGER = Logger.getLogger(Main.class);

	public static void main(String[] args) {

		LOGGER.debug("Hello World !");
		LOGGER.debug("Welcome to the Public Static Team Qualification Round program :)");
		
		parseInputFile();
		writeAnswer();
	}
	
	private static void parseInputFile() {
		FileUtils.loadFile(inputFile);
		String line = FileUtils.readNextLine();
		
		while (!line.equals("EOF")) {
			LOGGER.info("Next line is : " + line);
			line = FileUtils.readNextLine();
		}
	}
	
	private static void writeAnswer() {
			FileUtils.openFile(outputDirectory);

			for (int i=1; i<=15; ++i) {
				FileUtils.writeNewLine("Line " + i);
			}
			
			FileUtils.closeFile();
	}

}
