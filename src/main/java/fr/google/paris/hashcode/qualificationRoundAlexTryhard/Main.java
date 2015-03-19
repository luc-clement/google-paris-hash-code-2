package fr.google.paris.hashcode.qualificationRoundAlexTryhard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
		
		// idea : while the distribution with the subrow algo permits to fill the rows, get the score, exclude the largest server (with lowest score) and do the same

		//best distribution :
		int bestScore = 0;
		ArrayList<Integer> bestExcludedServerIds = new ArrayList<Integer>();
		
		ArrayList<Server> placedServers = new ArrayList<Server>();
		Server serverToExclude;
		int serverToExcludeIndex;
		boolean serverToExcludeFounded;
		ArrayList<Integer> excludedServerIds = new ArrayList<Integer>();
		int score;
		int maxServerSize;
		int nOpenSlots = 0;
		
		while(excludedServerIds.size() <= 160){
			//placeServersInRows(excludedServerIds);
			placeServersInRows_stopWhenFull(excludedServerIds);
			placedServers = getPlacedServers();
			//placeServersInGroups(placedServers);
			placeServersInGroups_groupFirst(placedServers);
			//placeServersInGroups_rowFirst();
			//placeServersInGroups_rowFirst_groupSecond();
			
			score = calculateScore();
			LOGGER.debug("distribution score : " + score + ", nServerPlaced : " + placedServers.size() + ", nExcludedServers : " + excludedServerIds.size());
			
			if(score > bestScore){
				bestScore = score;
				bestExcludedServerIds = new ArrayList<Integer>(excludedServerIds);
			}
			
			/*Collections.sort(placedServers, Server.sizeComparator);
			serverToExcludeIndex = placedServers.size() - 1;
			maxServerSize = placedServers.get(serverToExcludeIndex).getSize();
			serverToExcludeFounded = false;
			while(!serverToExcludeFounded){
				if(serverToExcludeIndex == 0 || placedServers.get(serverToExcludeIndex - 1).getSize() < maxServerSize)
					serverToExcludeFounded = true;
				else
					-- serverToExcludeIndex;
			}
			serverToExclude = placedServers.get(serverToExcludeIndex);*/
			
			Collections.sort(placedServers, Server.ratioComparator);
			serverToExclude = placedServers.get(0);
			int searchSize = Math.min(14, placedServers.size());
			searchSize = Math.min(searchSize, placedServers.size());
			for(int i = 1; i < searchSize; ++i)
				if(placedServers.get(i).getSize() > serverToExclude.getSize())
					serverToExclude = placedServers.get(i);
			
			excludedServerIds.add(serverToExclude.getId());
			
			nOpenSlots = totalOpenSlots();
			cleanDistribution();
		}
		
		//placeServersInRows(bestExcludedServerIds);
		placeServersInRows_stopWhenFull(bestExcludedServerIds);
		placedServers = getPlacedServers();
		//placeServersInGroups(placedServers);
		placeServersInGroups_groupFirst(placedServers);
		//placeServersInGroups_rowFirst();
		//placeServersInGroups_rowFirst_groupSecond();
		int finalScore = calculateScore();
		//for(Group group : Group.groups)
			//LOGGER.debug("Group " + group.getId() + " : score " + group.getScore() + ", capacity " + group.getCapacity() + ", maxRowCapacity " + group.getMaxRowCapacity() + ", nServers " + group.getServers().size());
		//LOGGER.info("nServerPlaced : " + placedServers.size() + ", nExcludedServers : " + bestExcludedServerIds.size());
		LOGGER.info("FINAL SCORE : " + finalScore);
		//writeAnswer();
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
		
		LOGGER.debug("Parse unavailable slots.");
		ArrayList<ArrayList<Integer>> unavailableSlots = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < nbRows; ++i)
			unavailableSlots.add(new ArrayList<Integer>());
		
		String[] slotAsString;
		int row, slot;
		for (int i=0; i<nbUnavailable; ++i) {
			line = FileUtils.readNextLine();
			slotAsString = StringUtils.split(line, ' ');
			row = Integer.parseInt(slotAsString[0]);
			slot = Integer.parseInt(slotAsString[1]);
			unavailableSlots.get(row).add(slot);
		}
		
		LOGGER.debug("Create rows array.");
		Row.rows = new Row[nbRows];
		for (int i=0; i<nbRows; ++i) {
			Row.rows[i] = new Row(i, nbSlots, unavailableSlots.get(i));
		}
		
		LOGGER.debug("Create servers array.");
		Server.servers = new Server[nbServers];
		for (int i=0; i<nbServers; ++i) {
			line = FileUtils.readNextLine();
			String[] serverAsString = StringUtils.split(line, ' ');
			Server server = new Server(i, Integer.parseInt(serverAsString[1]), Integer.parseInt(serverAsString[0]));
			Server.servers[i] = server;
		}
		
		LOGGER.debug("Create groups array.");
		Group.groups = new Group[nbPools];
		for (int i=0; i<nbPools; ++i) {
			Group.groups[i] = new Group(i, nbRows);
		}
		
		FileUtils.readNextLine();
		LOGGER.debug("Parsing input file : done.");
		
	}
	
	private static void placeServersInRows(ArrayList<Integer> excludedServerIds) throws PlacementException {
		ArrayList<Server> serversToPlace = new ArrayList<Server>(); // servers sorted by growing ratio
		
		Server server;
		for(int i = 0; i < nbServers; ++i){
			server = Server.servers[i];
			if(!excludedServerIds.contains(server.getId()))
				serversToPlace.add(server);
		}
		
		ArrayList<SubRow> subRows = new ArrayList<SubRow>(); // subRows sorted by growing size
		for(int i = 0; i < nbRows; ++i)
			subRows.addAll(Row.rows[i].subRows());
		Collections.sort(subRows);
		
		int serverSlot;
		while(serversToPlace.size() > 0){
			//Collections.sort(serversToPlace, Server.ratioComparator);
			//Collections.sort(serversToPlace, Server.sizeComparator);
			Collections.sort(serversToPlace, Server.capacityComparator);
			
			Server serverToPlace = serversToPlace.remove(serversToPlace.size() - 1);
			//Server serverToPlace = serversToPlace.remove(0);
			for(SubRow subRow : subRows){
				serverSlot = subRow.possibleSlot(serverToPlace);
				if(serverSlot >= 0){
					//LOGGER.debug("placing server " + server.getId() + " in row " + subRow.getRow().getId() + " at slot " + serverSlot +"...");
					serversToPlace.addAll(subRow.addServer(serverToPlace, serverSlot));
					//LOGGER.debug("	server " + server.getId() + " placed.");
					break;
				}
			}
		}
	}
	
	private static void placeServersInRows_stopWhenFull(ArrayList<Integer> excludedServerIds) throws PlacementException {
		ArrayList<Server> serversToPlace = new ArrayList<Server>(); // servers sorted by growing ratio
		
		Server server;
		for(int i = 0; i < nbServers; ++i){
			server = Server.servers[i];
			if(!excludedServerIds.contains(server.getId()))
				serversToPlace.add(server);
		}
		
		ArrayList<SubRow> subRows = new ArrayList<SubRow>(); // subRows sorted by growing size
		for(int i = 0; i < nbRows; ++i)
			subRows.addAll(Row.rows[i].subRows());
		Collections.sort(subRows);
		
		int serverSlot;
		while(totalOpenSlots() > 0 && serversToPlace.size() > 0){
			Collections.sort(serversToPlace, Server.sizeComparator);
			
			Server serverToPlace = serversToPlace.remove(0);
			for(SubRow subRow : subRows){
				serverSlot = subRow.possibleSlot(serverToPlace);
				if(serverSlot >= 0){
					//LOGGER.debug("placing server " + server.getId() + " in row " + subRow.getRow().getId() + " at slot " + serverSlot +"...");
					serversToPlace.addAll(subRow.addServer(serverToPlace, serverSlot));
					//LOGGER.debug("	server " + server.getId() + " placed.");
					break;
				}
			}
		}
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
	
	private static ArrayList<Server> getPlacedServers(){
		ArrayList<Server> placedServers = new ArrayList<Server>(); // sorted by growing capacity
		for(int i = 0; i < nbServers; ++i){
			Server server = Server.servers[i];
			if(server.getRow() >= 0)
				placedServers.add(server);
		}
		return placedServers;
	}
	
	private static void placeServersInGroups(ArrayList<Server> serversToPlace){
		ArrayList<Server> servers = new ArrayList<Server>(serversToPlace);
		Collections.sort(servers, Server.capacityComparator);
		
		ArrayList<Group> groups = new ArrayList<Group>(); // sorted by growing score
		for(int i = 0; i < nbPools; ++i)
			groups.add(Group.groups[i]);
		
		boolean serverPlaced;
		while(servers.size() > 0){
			Collections.sort(groups);
			Server server = servers.remove(servers.size() - 1);
			serverPlaced = false;
			for(Group group : groups){
				if(group.isAddServerUseful(server)){
					group.addServer(server);
					serverPlaced = true;
					break;
				}
			}
			if(!serverPlaced)
				groups.get(0).addServer(server);
		}
	}
	
	private static void placeServersInGroups_groupFirst(ArrayList<Server> serversToPlace){
		ArrayList<Server> servers = new ArrayList<Server>(serversToPlace);
		ArrayList<Group> groups = new ArrayList<Group>(); // sorted by growing score
		for(int i = 0; i < nbPools; ++i)
			groups.add(Group.groups[i]);
		
		Group group;
		Server bestServer;
		int bestBonusScore;
		int bonusScore;
		while(servers.size() > 0){
			Collections.sort(groups);
			group = groups.get(0);
			bestServer = servers.get(0);
			bestBonusScore = group.getBonusScore(bestServer);
			for(Server server : servers){
				bonusScore = group.getBonusScore(server);
				if(bonusScore > bestBonusScore){
					bestServer = server;
					bestBonusScore = bonusScore;
				}
			}
			group.addServer(bestServer);
			servers.remove(bestServer);
		}
	}
	
	private static void placeServersInGroups_rowFirst(){
		ArrayList<Row> rows = new ArrayList<Row>();
		for(Row row : Row.rows)
			rows.add(row);
		Collections.sort(rows, Row.nServersComparator);
		
		/*for(int i = nbRows - 1; i >= 0; --i)
			placeServersInGroups(rows.get(i).getServers());*/
		for(Row row : rows)
			placeServersInGroups(row.getServers());
	}
	
	private static void placeServersInGroups_rowFirst_groupSecond(){
		ArrayList<Row> rows = new ArrayList<Row>();
		for(Row row : Row.rows)
			rows.add(row);
		Collections.sort(rows, Row.nServersComparator);
		for(Row row : rows)
			placeServersInGroups_groupFirst(row.getServers());
		
		/*Collections.sort(rows, Row.capacityComparator);
		for(int i = nbRows - 1; i >= 0; --i)
			placeServersInGroups_groupFirst(rows.get(i).getServers());*/
	}
	
	private static int calculateScore() {
		int score = Integer.MAX_VALUE;
		Group group;
		int groupScore;
		for (int i = 0; i < nbPools; ++i) {
			group = Group.groups[i];
			groupScore = group.getScore();
			if (groupScore < score) {
				score = groupScore;
			}
			//LOGGER.debug("Group " + i + " : score " + groupScore + ", capacity " + group.getCapacity() + ", maxRowCapacity " + group.getMaxRowCapacity() + ", nServers " + group.getServers().size());
		}
		
		//LOGGER.info("FINAL SCORE : " + score);
		return score;
	}
	
	private static void cleanDistribution() throws PlacementException{
		for(int i = 0; i < nbPools; ++i)
			Group.groups[i].clean();
		for(int i = 0; i < nbRows; ++i)
			Row.rows[i].clean();
	}
	
	private static void writeAnswer() {
		FileUtils.openFile(outputDirectory);
		Server server;
		for (int i=0; i<nbServers; ++i) {
			server = Server.servers[i];
			if (server.getRow() != -1 && server.getSlot() != -1 && server.getGroup() != -1)
				FileUtils.writeNewLine(server.getRow() + " " + server.getSlot() + " " + server.getGroup());
			else
				FileUtils.writeNewLine("x");
		}
		FileUtils.closeFile();
	}

}
