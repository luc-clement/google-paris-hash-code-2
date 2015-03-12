package fr.google.paris.hashcode.qualificationRound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Group {

	public static Group[] groups;

	private int id;
	private int capacity = 0;
	private List<Server> listServers = new ArrayList<Server>();

	private void addServer(Server server) {
		capacity += server.getCapacity();
		listServers.add(server);
		server.setGroup(id);
	}

	public static void sortRowsByGroup(){
		for(Row r : Row.rows){
			sortRowByGroup(r);
		}
	};

	public static void sortRowByGroup(Row row){
		List<Integer> alreadyChosenGroup = new ArrayList<Integer>();
		List<Integer> alreadyChosenServer = new ArrayList<Integer>();
		HashMap<Integer, Server> servers = row.getLayout();
		List<Server> serverList = new ArrayList<Server>(servers.values());
		int size = serverList.size();
		Collections.sort(serverList);
		for(int i=0; i<size; i++){
			int group = groupWhereInsertBestServer(alreadyChosenGroup);
			Server server = serverList.get(size-i-1);
			
			groups[group].addServer(server);
			alreadyChosenGroup.add(group);
			alreadyChosenServer.add(server.getId());
		}
	};

	public static int groupWhereInsertBestServer(List<Integer> alreadyChosen) {
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

	public Group(int id) {
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
