package fr.google.paris.hashcode.qualificationRoundAlexTryhard;

import java.util.ArrayList;
import java.util.Comparator;

public class Row {

	public static Row[] rows;

	public static NServersComparator nServersComparator = new NServersComparator();
	public static CapacityComparator capacityComparator = new CapacityComparator();
	
	private int id;
	private int length;
	private int size;
	private int nOpenSlots;
	private int nServers;
	private int capacity;
	private int[] occupation; // -2 unavailable, -1 open, else serverID
	private ArrayList<Server> servers;
	
	public Row(int id, int length, ArrayList<Integer> unavailableSlots) {
		this.id = id;
		this.length = length;
		size = length - unavailableSlots.size();
		nOpenSlots = size;
		nServers = 0;
		capacity = 0;
		occupation = new int[length];
		for (int i = 0; i < length; ++i) {
			occupation[i] = -1;
		}
		for(int slot : unavailableSlots)
			occupation[slot] = -2;
		servers = new ArrayList<Server>();
	}
	
	public static class CapacityComparator implements Comparator<Row> {
		
		@Override
		public int compare(Row r1, Row r2) {
			int capacityComp = Integer.compare(r1.getCapacity(), r2.getCapacity());
			if(capacityComp != 0)
				return capacityComp;
			int inversedSizeComp = Integer.compare(r2.getnServers(), r1.getnServers());
			if(inversedSizeComp != 0)
				return inversedSizeComp;
			return Integer.compare(r1.getId(), r2.getId());
		}
	}
	
	public static class NServersComparator implements Comparator<Row> {
		
		@Override
		public int compare(Row r1, Row r2) {
			int nServersComp = Integer.compare(r1.getnServers(), r2.getnServers());
			if(nServersComp != 0)
				return nServersComp;
			int capacityComp = Integer.compare(r2.getCapacity(), r1.getCapacity());
			if(capacityComp != 0)
				return capacityComp;
			return Integer.compare(r1.getId(), r2.getId());
		}
	}
	
	/*public static class RatioComparator implements Comparator<Row> {
		
		@Override
		public int compare(Row r1, Row r2) {
			float r1 = s1.getRatio();
			float r2 = s2.getRatio();
			float result = r1 / r2; // ratios are supposed to be > 0
			float epsilon = 0.001f;
			if(result > 1 + epsilon) return 1;
			if(result < 1 - epsilon) return -1;
			int capacityComp = Integer.compare(s1.getCapacity(), s2.getCapacity());
			if(capacityComp != 0)
				return capacityComp;
			return Integer.compare(s1.getId(), s2.getId());
		}
	}*/
	
	public ArrayList<SubRow> subRows(){
		ArrayList<SubRow> subRows = new ArrayList<SubRow>();
		int iStart = 0,
		iEnd = 0;
		for(int i = 0; i < length; ++i){
			if(occupation[i] != -2)
				++iEnd;
			else {
				if(iEnd > iStart)
					subRows.add(new SubRow(this, iStart, iEnd));
				iStart = i+1;
				iEnd = i+1;
			}
		}
		if(iEnd > iStart)
			subRows.add(new SubRow(this, iStart, iEnd));
		return subRows;
	}
	
	public void addServer(Server server, int slot) throws PlacementException {
		int serverID = server.getId();
		int serverSize = server.getSize();
		
		if(slot < 0 || slot + serverSize > length)
			throw new PlacementException("The server is outisde the row");
		
		boolean canAddServer = true;
		for(int i = slot; i < slot + serverSize; ++i)
			if(occupation[i] != -1)
				canAddServer = false;
		
		if(!canAddServer)
			throw new PlacementException("Not enough space to add the server");
		
		for(int i = slot; i < slot + serverSize; ++i)
			occupation[i] = serverID;
		server.setRow(id);
		server.setSlot(slot);
		nOpenSlots -= serverSize;
		++nServers;
		capacity += server.getCapacity();
		servers.add(server);
		
	}
	
	public void rmServer(Server server) throws PlacementException {
		if(!servers.contains(server))
			return;
		
		int serverID = server.getId();
		int serverSize = server.getSize();
		int slot = server.getSlot();
		
		boolean canRmServer = true;
		for(int i = slot; i < slot + serverSize; ++i)
			if(occupation[i] != serverID)
				canRmServer = false;
		
		if(!canRmServer)
			throw new PlacementException("Server placement integrity broken");
		
		for(int i = slot; i < slot + serverSize; ++i)
			occupation[i] = -1;
		server.setRow(-1);
		server.setSlot(-1);
		nOpenSlots += serverSize;
		--nServers;
		capacity -= server.getCapacity();
		servers.remove(server);
	}

	public int getId() {
		return id;
	}

	public int getLength() {
		return length;
	}
	
	public int getSize() {
		return size;
	}

	public int getnOpenSlots() {
		return nOpenSlots;
	}

	public int getnServers() {
		return nServers;
	}

	public int getCapacity() {
		return capacity;
	}
	
	public ArrayList<Server> getServers() {
		return new ArrayList<Server>(servers);
	}
	
	public void clean() throws PlacementException{
		ArrayList<Server> serversCopy = getServers();
		for(Server server : serversCopy)
			rmServer(server);
	}
	
}
