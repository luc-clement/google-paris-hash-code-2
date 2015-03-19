package fr.google.paris.hashcode.qualificationRoundAlexTryhard;

import java.util.Comparator;

public class Server {

	public static Server[] servers;
	
	public static CapacityComparator capacityComparator = new CapacityComparator();
	public static SizeComparator sizeComparator = new SizeComparator();
	public static RatioComparator ratioComparator = new RatioComparator();
	
	private int id;
	private int capacity;
	private int size;
	private float ratio;
	
	private int row;
	private int slot;
	private int group;
	
	public Server(int id, int capacity, int size) {
		super();
		this.id = id;
		this.capacity = capacity;
		this.size = size;
		ratio = ((float) capacity) / ((float) size);
		row = -1;
		slot = -1;
		group = -1;
	}
	
	public static class CapacityComparator implements Comparator<Server> {
		
		@Override
		public int compare(Server s1, Server s2) {
			int capacityComp = Integer.compare(s1.getCapacity(), s2.getCapacity());
			if(capacityComp != 0)
				return capacityComp;
			int inversedSizeComp = Integer.compare(s2.getSize(), s1.getSize());
			if(inversedSizeComp != 0)
				return inversedSizeComp;
			return Integer.compare(s1.getId(), s2.getId());
		}
	}
	
	public static class SizeComparator implements Comparator<Server> {
		
		@Override
		public int compare(Server s1, Server s2) {
			int sizeComp = Integer.compare(s1.getSize(), s2.getSize());
			if(sizeComp != 0)
				return sizeComp;
			int capacityComp = Integer.compare(s1.getCapacity(), s2.getCapacity());
			if(capacityComp != 0)
				return capacityComp;
			return Integer.compare(s1.getId(), s2.getId());
		}
	}
	
	public static class RatioComparator implements Comparator<Server> {
		
		@Override
		public int compare(Server s1, Server s2) {
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
	}
	
	public int getId() {
		return id;
	}
	
	public int getCapacity() {
		return capacity;
	}

	public int getSize() {
		return size;
	}
	
	public float getRatio() {
		return ratio;
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
