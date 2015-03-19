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
	
	public static void main(String[] args) throws PlacementException {

		LOGGER.info("Welcome to the Public Static Team Qualification Round software :)");
		
		parseInputFile();
		
		for (int i=0; i<nbRows; ++i) {
			Row.rows[i]._buildFreeSpaces();
		}
		Row.placeServers();
		LOGGER.debug("nOpenSlots after placing servers : " + totalOpenSlots());
		LOGGER.debug("nb of servers placed : " + nTotalServers());
		LOGGER.debug("total capacity : " + totalCapacity());
		
		Group.sortRowsByGroup();
		
		calculateAnswer();
		
		writeAnswer();
	}
	
	private static int totalOpenSlots(){
		int nOpenSlots = 0;
		for(int i = 0; i < nbRows; ++i)
			nOpenSlots += Row.rows[i].getnOpenSlots();
		return nOpenSlots;
	}
	
	private static int nTotalServers(){
		int nServers = 0;
		for(int i = 0; i < nbRows; ++i)
			nServers += Row.rows[i].getnServers();
		return nServers;
	}
	
	private static int totalCapacity(){
		int capacity = 0;
		for(int i = 0; i < nbRows; ++i)
			capacity += Row.rows[i].getCapacity();
		return capacity;
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
		
		LOGGER.debug("Create groups array.");
		Group.groups = new Group[nbPools];
		for (int i=0; i<nbPools; ++i) {
			Group.groups[i] = new Group(i);
		}
		
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
	
	private static int calculateAnswer() {
		int result = Integer.MAX_VALUE;
		
		for (int i=0; i<nbPools; ++i) {
			int scoreGroup = calculateGroupScore(i);
			
			if (scoreGroup < result) {
				result = scoreGroup;
			}
		}
		
		LOGGER.info("FINAL SCORE : " + result);
		return result;
	}
	
	private static int calculateGroupScore(int groupId) {
		int GroupTotalCapacity = 0;
		for (Server server : Group.groups[groupId].getListServers()) {
			GroupTotalCapacity += server.getCapacity();
		}
		
		LOGGER.info("Group " + groupId + " total capacity : " + GroupTotalCapacity);
		int Score = GroupTotalCapacity;
		for (int i=0; i<nbRows; ++i) {
			int RowGroupCapacity = 0;
			for (Server server : Group.groups[groupId].getListServers()) {
				if (server.getRow() == i) {
					RowGroupCapacity += server.getCapacity();
				}
			}
			int GroupWithoutRow = GroupTotalCapacity - RowGroupCapacity;
			if (GroupWithoutRow < Score) {
				Score = GroupWithoutRow;
			}
		}
		LOGGER.info("Group " + groupId + " final score : " + Score);
		return Score;
	}
	
	private static void writeAnswer() {
			FileUtils.openFile(outputDirectory);

			for (int i=0; i<nbServers; ++i) {
				if (Server.servers[i].getRow() != -1 
						&& Server.servers[i].getSlot() != -1 
						&& Server.servers[i].getGroup() != -1) {
					FileUtils.writeNewLine(Server.servers[i].getRow() + " " + Server.servers[i].getSlot() + " " + Server.servers[i].getGroup());
				} else {
					FileUtils.writeNewLine("x");
				}
			}
			
			FileUtils.closeFile();
	}

}
