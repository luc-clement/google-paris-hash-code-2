package fr.google.paris.hashcode.qualificationRoundAlexTryhard;

import java.util.ArrayList;
import java.util.Collections;

public class SubRow implements Comparable<SubRow> {
	
	private Row row;
	private int startSlot;
	private int endSlot; // excluded
	private int size;
	private int nOpenSlots;
	private ArrayList<Server> servers;
	
	public SubRow(Row row, int start, int end) {
		this.row = row;
		startSlot = start;
		endSlot = end;
		size = end - start;
		nOpenSlots = size;
		servers = new ArrayList<Server>(); // sorted by growing size, biggest server placed at the beginning of the subRow
	}
	
	public Row getRow(){
		return row;
	}
	
	public int getStartSlot() {
		return startSlot;
	}

	public int getEndSlot() {
		return endSlot;
	}

	public int getSize() {
		return size;
	}
	
	public int getnOpenSlots() {
		return nOpenSlots;
	}

	public ArrayList<Server> getServers() {
		return new ArrayList<Server>(servers);
	}
	
	public boolean hasServer(Server server) {
		return servers.contains(server);
	}
	
	// add the server, removing the smallest ones if necessary and return them (sorted by growing size), never removes bigger servers
	public ArrayList<Server> addServer(Server server, int slot) throws PlacementException {
		if(server.getRow() != -1)
			throw new PlacementException("The server can't be added, it's already placed");
		
		ArrayList<Server> removedServers = new ArrayList<Server>();
		int serverSize = server.getSize();
		while(nOpenSlots < serverSize)
			removedServers.add(rmSmallestServer());

		servers.add(server);
		Collections.sort(servers, Server.sizeComparator);
		int serverIndex = servers.indexOf(server);
		int smallerServerSlot;
		Server smallerServer;
		for(int i = 0; i < serverIndex; ++i){
			smallerServer = servers.get(i);
			smallerServerSlot = smallerServer.getSlot();
			smallerServerSlot += serverSize;
			row.rmServer(smallerServer);
			row.addServer(smallerServer, smallerServerSlot);
		}
		
		row.addServer(server, slot);
		nOpenSlots -= serverSize;
		return removedServers;
	}
	
	public Server rmSmallestServer() throws PlacementException {
		if(servers.size() == 0)
			return null;
		Server removedServer = servers.remove(0);
		nOpenSlots += removedServer.getSize();
		row.rmServer(removedServer);
		return removedServer;
	}
	
	// return -1 if we can't add the server
	public int possibleSlot(Server server) {
		if(server.getSize() > size)
			return -1;
		
		servers.add(server);
		Collections.sort(servers, Server.sizeComparator);
		
		int serverIndex = servers.indexOf(server);
		int lastIndex = servers.size() - 1;
		int serverSlot = startSlot;
		if(serverIndex != lastIndex){
			Server smallestBiggerServer = servers.get(serverIndex + 1); // ie the predecessor in the row
			serverSlot = smallestBiggerServer.getSlot() + smallestBiggerServer.getSize();
		}
		if(serverSlot + server.getSize() > endSlot)
			serverSlot = -1;
		servers.remove(server);
		return serverSlot;
	}
	
	@Override
	public int compareTo(SubRow subRow) {
		int sizeComp = Integer.compare(size, subRow.getSize());
		if(sizeComp != 0)
			return sizeComp;
		return Integer.compare(row.getId(), subRow.getRow().getId());
	}
	
}
