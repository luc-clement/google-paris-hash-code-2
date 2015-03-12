package fr.google.paris.hashcode.qualificationRound;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import fr.google.paris.hashcode.FileUtils;

public class Main {

	private final static String inputFile = "src/main/resources/dc.in";
	private final static String outputDirectory = "src/main/resources/output/";
	
	private final static Logger LOGGER = Logger.getLogger(Main.class);

	public static int nbRows;
	public static int nbSlots;
	public static int nbUnavailable;
	public static int nbPools;
	public static int nbServers;
	
	public static void main(String[] args) {

		LOGGER.info("Welcome to the Public Static Team Qualification Round software :)");
		
		parseInputFile();
		writeAnswer();
	}
	
	private static void parseInputFile() {
		FileUtils.loadFile(inputFile);
		String line = FileUtils.readNextLine();
		
		String[] initialData = StringUtils.split(line, ' ');
		nbRows = Integer.parseInt(initialData[0]);
		nbSlots = Integer.parseInt(initialData[1]);
		nbUnavailable = Integer.parseInt(initialData[2]);
		nbPools = Integer.parseInt(initialData[3]);
		nbServers = Integer.parseInt(initialData[4]);
		
		LOGGER.debug("Number of rows : " + nbRows);
		LOGGER.debug("number of slots per row : " + nbSlots);
		LOGGER.debug("number of unavailable slots : " + nbUnavailable);
		LOGGER.debug("number of pools : " + nbPools);
		LOGGER.debug("number of servers : " + nbServers);
		
		LOGGER.debug("Create rows array.");
		Row.rows = new Row[nbRows];
		for (int i=0; i<nbRows; ++i) {
			Row.rows[i] = new Row(i, nbSlots);
		}
		
		LOGGER.debug("Parse unavailable slots.");
		for (int i=0; i<nbUnavailable; ++i) {
			line = FileUtils.readNextLine();
			String[] unavailableSlot = StringUtils.split(line, ' ');
			Row.rows[Integer.parseInt(unavailableSlot[0])].addUnavailableSlot(Integer.parseInt(unavailableSlot[1]));
		}
		
		LOGGER.debug("Create servers array.");
		Server.servers = new Server[nbServers];
		for (int i=0; i<nbServers; ++i) {
			line = FileUtils.readNextLine();
			String[] serverAsString = StringUtils.split(line, ' ');
			Server server = new Server(i, Integer.parseInt(serverAsString[1]), Integer.parseInt(serverAsString[0]));
			Server.servers[i] = server;
		}
		
		FileUtils.readNextLine();
		LOGGER.debug("Parsing input file : done.");
		
	}
	
	private static void writeAnswer() {
			FileUtils.openFile(outputDirectory);

			for (int i=1; i<=15; ++i) {
				FileUtils.writeNewLine("Line " + i);
			}
			
			FileUtils.closeFile();
	}

}
