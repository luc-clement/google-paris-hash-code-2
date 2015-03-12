package fr.google.paris.hashcode.qualificationRound;

import java.util.Comparator;

public class Server implements Comparator<Server> {

	public static Server[] servers;
	
	int id;
	int capacity;
	int size;
	float ratio;
	
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
	public int compare(Server s1, Server s2) {
		
		if (s1.getRatio() > s2.getRatio()) {
			return 1;
		} else if (s1.getRatio() < s2.getRatio()) {
			return -1;
		} 

		// Here the ratio are the same for s1 and s2
		if (s1.getSize() < s2.getSize()) {
			return 1;
		} else if (s1.getSize() > s2.getSize()) {
			return -1;
		}
		
		// They can be equals at this point 
		return 0;
	}
	
	
}
