package fr.google.paris.hashcode.qualificationRoundAlexTryhard;

import java.util.ArrayList;

public class Group implements Comparable<Group> {

	public static Group[] groups;

	private int id;
	private int capacity;
	private int maxRowId; // id of the row where is the highest total capacity for the group
	//private int minRowId;
	private int[] rowCapacity;
	private ArrayList<Server> servers;
	
	public Group(int id, int nRows){
		this.id = id;
		capacity = 0;
		maxRowId = 0;
		rowCapacity = new int[nRows];
		for(int i = 0; i < nRows; ++i)
			rowCapacity[i] = 0;
		servers = new ArrayList<Server>();
	}
	
	public boolean isAddServerUseful(Server server){
		return server.getRow() != maxRowId;
	}
	
	public int getBonusScore(Server server){
		if(server.getRow() == maxRowId)
			return 0;
		int serverCapacity = server.getCapacity();
		int newRowScore = rowCapacity[server.getRow()] + serverCapacity;
		int maxRowCapacity = getMaxRowCapacity();
		if(newRowScore > maxRowCapacity)
			return capacity + serverCapacity - newRowScore;
		return capacity + serverCapacity - maxRowCapacity;
	}
	
	public void addServer(Server server){
		servers.add(server);
		capacity += server.getCapacity();
		rowCapacity[server.getRow()] += server.getCapacity();
		if(rowCapacity[server.getRow()] > rowCapacity[maxRowId])
			maxRowId = server.getRow();
	}
	
	public int getId() {
		return id;
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	public int getMaxRowId() {
		return maxRowId;
	}
	
	public int getMaxRowCapacity() {
		return rowCapacity[maxRowId];
	}
	
	/*public int getMinRowId() {
		return minRowId;
	}
	
	public int getMinRowCapacity() {
		return rowCapacity[minRowId];
	}*/
	
	public int getScore() {
		return capacity - rowCapacity[maxRowId];
	}
	
	public ArrayList<Server> getServers() {
		return new ArrayList<Server>(servers);
	}
	
	public void clean(){
		for(Server server : servers)
			server.setGroup(-1);
		capacity = 0;
		maxRowId = 0;
		for(int i = 0; i < rowCapacity.length; ++i)
			rowCapacity[i] = 0;
		servers = new ArrayList<Server>();
	}

	@Override
	public int compareTo(Group g) {
		int scoreComp = Integer.compare(getScore(), g.getScore());
		if(scoreComp != 0)
			return scoreComp;
		int maxComp = Integer.compare(getMaxRowCapacity(), g.getMaxRowCapacity());
		if(maxComp != 0)
			return maxComp;
		return Integer.compare(id, g.getId());
	}
	
}
