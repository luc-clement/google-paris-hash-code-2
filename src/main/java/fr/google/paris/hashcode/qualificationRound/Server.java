package fr.google.paris.hashcode.qualificationRound;

import java.util.Comparator;

public class Server implements Comparable<Server> {

	public static Server[] servers;
	
	private int id;
	private int capacity;
	private int size;
	private float ratio;
	
	private int row = -1;
	private int slot = -1;
	private int group = -1;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	public float getRatio() {
		return ratio;
	}
	public void setRatio(float ratio) {
		this.ratio = ratio;
	}
	
	public Server(int id, int capacity, int size) {
		super();
		this.capacity = capacity;
		this.size = size;
		this.ratio = calculateRatio(capacity, size);
	}
	
	private float calculateRatio(int capacity, int size) {
		return (float) capacity / (float) size;
	}
	
	@Override
	public int compareTo(Server s2) {
		
		if (ratio > s2.getRatio()) {
			return 1;
		} else if (ratio < s2.getRatio()) {
			return -1;
		} 

		// Here the ratio are the same for s1 and s2
		if (size < s2.getSize()) {
			return 1;
		} else if (size > s2.getSize()) {
			return -1;
		}
		
		// They can be equals at this point 
		return 0;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}
	
	
}
