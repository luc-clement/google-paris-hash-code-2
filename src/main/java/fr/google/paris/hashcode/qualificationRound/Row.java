package fr.google.paris.hashcode.qualificationRound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;

public class Row implements Comparable<Row>{

	public static Row[] rows;
	private int length;
	private int id;
	private int capacity;
	private int[] occupation;
	private HashMap<Integer, Server> layout = new HashMap<Integer, Server>();
	private int maxServerSize = 0;  
	
	public class FreeSpace implements Comparable<FreeSpace>{
		public int getSize() {
			return size;
		}

		public void setSize(int size) {
			this.size = size;
		}

		public int getFirst_slot() {
			return first_slot;
		}

		public void setFirst_slot(int first_slot) {
			this.first_slot = first_slot;
		}

		private int size;
		private int first_slot;
		
		public FreeSpace(int f_s,int s){
			first_slot = f_s;
			size = s;
		}

		@Override
		public int compareTo(FreeSpace o1) {
			// TODO Auto-generated method stub
			if (size > o1.size){
				return 1;
			}
			else if (size == o1.size){
				return 0;
			}
			
			else {
				return -1;
			}
			
		}
	}
	
	private ArrayList<FreeSpace> freespaces;
	
	public ArrayList<FreeSpace> getFreespaces() {
		return freespaces;
	}

	public void _buildFreeSpaces(){
		freespaces = new ArrayList<FreeSpace>();
		int size = 0;
		int first_slot = 0;
		for (int i = 0 ; i < length ; i++ ){
			if (occupation[i] == 0) {
				size++;
			}
			else if ( size == 0){
				first_slot++;
			}
			else {
				freespaces.add(new FreeSpace(first_slot,size));
				first_slot++;
				size = 0;
			}
		}
		if (size != 0 ) {
			freespaces.add(new FreeSpace(first_slot,size));
		}
	}
	
	public boolean canAddServer(Server server){
		Collections.sort(freespaces);
		int maxSpaceSize = freespaces.get(freespaces.size() - 1).size;
		return server.getSize() <= maxSpaceSize;
	}
	
	public void addServer(Server server) throws PlacementException{ 
		if (!canAddServer(server)){
			throw new PlacementException("The server was to big to bee added");
		}
		
		Collections.sort(freespaces);
		for (FreeSpace fs : freespaces){
			if(fs.getSize() >= server.getSize()){
				addServer(fs.first_slot,server);
				fs.setSize(fs.getSize() - server.getSize());
				fs.setFirst_slot(fs.first_slot + server.getSize());
				Collections.sort(freespaces);
				return;
			}
		}
	}
	
	public void addServer(int slot, Server server) throws PlacementException {
		// Check if it's possible
		for (int i=0; i<server.getSize(); ++i) {
			if (occupation[slot+i] != 0) {
				throw new PlacementException("The server can not be add, the slots are not available.");
			}
		}
		
		// If we are here then it's ok to add it
		for (int i=0; i<server.getSize(); ++i) {
			occupation[slot+i] = 1;
		}
		layout.put(slot, server);
		capacity += server.getCapacity();
		server.setRow(id);
		server.setSlot(slot);
	}

	public Row(int id, int length) {
		this.id = id;
		this.length = length;
		occupation = new int[length];
		for (int i=0; i < length; ++i) {
			occupation[i] = 0;
		}
		this.capacity = 0;
	}
	
	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	public void addUnavailableSlot(int slot) {
		occupation[slot] = -1;
	} 
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int[] getOccupation() {
		return occupation;
	}
	
	public void setOccupation(int[] occupation) {
		this.occupation = occupation;
	}
	
	public HashMap<Integer, Server> getLayout() {
		return layout;
	}
	
	public void setLayout(HashMap<Integer, Server> layout) {
		this.layout = layout;
	} 
	
	
	public static void placeServers() throws PlacementException{
		Server[] servers = Server.servers;
		ArrayList<Server> sortedServers = new ArrayList<Server>();
		ArrayList<Row> sortedRows = new ArrayList<Row>();
		
		for(int i=0; i < servers.length; ++i){
			sortedServers.add(servers[i]);
		}
		Collections.sort(sortedServers);
		
		for(int i=0; i < rows.length; ++i){
			sortedRows.add(rows[i]);
		}
		Collections.sort(sortedRows);
		
		Server server;
		int currentServerIndex = 0;
		int rowIndexToAdd;
		while(currentServerIndex < sortedServers.size()){
			server = sortedServers.get(currentServerIndex);
			rowIndexToAdd = -1;
			for(int i=0; i < sortedRows.size(); ++i){
				if(sortedRows.get(i).canAddServer(server)){
					rowIndexToAdd = i;
					break;
				}
			}
			if(rowIndexToAdd != -1){
				sortedRows.get(rowIndexToAdd).addServer(server);
				Collections.sort(sortedRows);
			}
			currentServerIndex++;
		}
	}

	@Override
	public int compareTo(Row o) {
		if( capacity > o.getCapacity())
			return 1;
		else
			return -1;
	}
	
}
