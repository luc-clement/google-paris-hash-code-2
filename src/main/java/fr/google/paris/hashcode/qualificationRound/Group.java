package fr.google.paris.hashcode.qualificationRound;

import java.util.ArrayList;
import java.util.List;

public class Group {

	public static Group[] groups;
	
	private int id;
	private int capacity;
	private List<Server> listServers = new ArrayList<Server>();
	
	private void addServer(Server server) {
		capacity += server.getCapacity();
		listServers.add(server);
	}
	
	public int groupWhereInsertBestServer(List<Integer> alreadyChosen) {
		int minCapacity = Integer.MAX_VALUE;
		int result = -1;
		for (int i=0; i<groups.length; ++i) {
			if (!alreadyChosen.contains(i) && groups[i].capacity < minCapacity) {
				minCapacity = groups[i].capacity;
				result = i;
			}
		}
	    return result;
	}
	
	public Group(int id, int capacity) {
		super();
		this.id = id;
		this.capacity = 0;
	}

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

	public List<Server> getListServers() {
		return listServers;
	}

	public void setListServers(List<Server> listServers) {
		this.listServers = listServers;
	}
	
	
	
}
