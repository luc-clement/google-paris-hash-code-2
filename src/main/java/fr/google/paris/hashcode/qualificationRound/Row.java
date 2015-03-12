package fr.google.paris.hashcode.qualificationRound;

import java.util.HashMap;

public class Row {

	public static Row[] rows;
	
	private int id;
	private int capacity;
	private int[] occupation;
	private HashMap<Integer, Server> layout = new HashMap<Integer, Server>();
	
	public void addServer(int slot, Server server) throws PlacementException {
		// Check if it's possible
		for (int i=0; i<server.getSize(); ++i) {
			if (occupation[slot+i] != 0) {
				throw new PlacementException("The server can not be add, the slots are not available.");
			}
		}
		
		// If we are here then it's ok to add it
		for (int i=0; i<server.getSize(); ++i) {
			occupation[slot+i] = 0;
		}
		layout.put(slot, server);
		capacity += server.getCapacity();
	}

	public Row(int id, int size) {
		this.id = id;
		occupation = new int[size];
		for (int i=0; i < size; ++i) {
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
	
	
}
