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
	
	public Group(int id, int capacity) {
		super();
		this.id = id;
		this.capacity = capacity;
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
