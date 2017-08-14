import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RouterTable implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private transient BufferedReader brObj;
	private ArrayList<String> destPrevNeighbors = new ArrayList<String>();
	private String fileName;
	boolean flag = false;

	//Setting infinity value to avoid count to infinity issue
	private double infinity = 16.0;
	private ArrayList<String> nextHopNeighbor = new ArrayList<String>();
	private HashMap<String, Double> originalCosts = new HashMap<String, Double>();
	private String routerName;
	private ArrayList<RTableDetails> rTableDetailsList = new ArrayList<RTableDetails>();

	public RouterTable() {

	}

	public RouterTable(RouterTable rTable) {
		this.rTableDetailsList = rTable.rTableDetailsList;
	}

	public String getRouterName() {
		return routerName;
	}

	public void setRouterName(String routerName) {
		this.routerName = routerName;
	}

	public ArrayList<String> getdestPrevNeighbors() {
		return destPrevNeighbors;
	}

	public void setdestPrevNeighbors(ArrayList<String> destPrevNeighbors) {
		this.destPrevNeighbors = destPrevNeighbors;
	}

	public ArrayList<String> getNextHopNeighbors() {
		return nextHopNeighbor;
	}

	public void setNextHopNeighbors(ArrayList<String> nextHopNeighbor) {
		this.nextHopNeighbor = nextHopNeighbor;
	}

	public ArrayList<RTableDetails> getTableDetails() {
		return rTableDetailsList;
	}

	public void setTableDetails(ArrayList<RTableDetails> rtable) {
		this.rTableDetailsList = rtable;
	}

	@Override
	public String toString() {
		return "RouterTable [brObj=" + brObj + ", destPrevNeighbors=" + destPrevNeighbors + ", fileName=" + fileName
				+ ", flag=" + flag + ", infinity=" + infinity + ", nextHopNeighbor=" + nextHopNeighbor
				+ ", originalCosts=" + originalCosts + ", routerName=" + routerName + ", rTableDetails=" + rTableDetailsList
				+ "]";
	}



	//constructing initial table from file fileName
	public void constructInitialTable(String fileNam) throws IOException {

		String[] fileParts = fileNam.split(Pattern.quote("."));
		//reading rest of the entries
		FileReader file = new FileReader(new File(fileNam));	
		String line;
		String routerNam = String.valueOf(fileParts[0].charAt(fileParts[0].length()-1));
		RTableDetails rTableDetails;
		//entry for the same router
		rTableDetails = new RTableDetails(routerNam, routerNam, "-", 0.0, "");

		brObj = new BufferedReader(file);
		destPrevNeighbors.add(routerNam);
		fileName = fileNam;
		originalCosts.put(routerNam, 0.0);
		routerName = routerNam;
		rTableDetailsList.add(rTableDetails);

		//scanning every line for edge and cost
		while((line = brObj.readLine())!=null) 
		{
			String[] parts = line.split("\\s+");
			if(parts.length == 2) {
				String destination = parts[0];
				Double cost = Double.parseDouble(parts[1]);

				rTableDetails = new RTableDetails(routerNam, destination, destination, cost, "");
				destPrevNeighbors.add(destination);
				nextHopNeighbor.add(destination);
				originalCosts.put(destination, cost);
				rTableDetailsList.add(rTableDetails);

			}
		}
	}

	// receivedTable is the table being sent from the other routers
	public synchronized void updateRouterTable(RouterTable ownTable, RouterTable receivedTable) {

		//check for edges in the receivedTable and add them to ownTable
		ArrayList<String> receivedNeighbors = new ArrayList<String>(receivedTable.getdestPrevNeighbors());
		ArrayList<String> ownNeighbors = new ArrayList<String>(ownTable.getdestPrevNeighbors());
		for(String neighbor : receivedNeighbors) {
			if(!ownNeighbors.contains(neighbor)) {
				RTableDetails rObj = new RTableDetails(ownTable.getRouterName(), neighbor, "-", infinity, "");
				ownTable.rTableDetailsList.add(rObj);
				ownTable.destPrevNeighbors.add(neighbor);
			}
		}

		//Adding the distance between ownNode and the other nodes to all entries in the receivedTable
		RouterTable receivedUpdated = new RouterTable(receivedTable);
		Double costToAdd = 0.0;
		for (RTableDetails entry : receivedTable.getTableDetails()) {
			if(entry.getDest().equals(ownTable.getRouterName())) {
				costToAdd = entry.getCost();
				break;
			}
		}

		for (RTableDetails receivedEntry : receivedUpdated.getTableDetails()) {
			receivedEntry.setNextHop(receivedEntry.getSource());
			receivedEntry.setPath(receivedEntry.getSource());
			Double costOriginal = receivedEntry.getCost();
			receivedEntry.setCost(costOriginal+costToAdd);
		}

		//to resolve count to infinity problem
		for (RTableDetails entry : receivedUpdated.getTableDetails()) {
			if(entry.getNextHop().equals(ownTable.getRouterName())) {
				entry.setCost(Double.MAX_VALUE);
				System.out.println("count to infinity");
				flag = true;
			}
		}

		//compare receivedModified and own table and make updates
		for (RTableDetails receivedEntry : receivedUpdated.getTableDetails()) {
			String destRouter = receivedEntry.getDest();
			String nextHopRouter = receivedEntry.getNextHop();
			Double costReceived = receivedEntry.getCost();
			for (RTableDetails ownEntr : ownTable.getTableDetails()) {
				//for the same destination
				if(ownEntr.getDest().equals(ownEntr.getSource())) {
					continue;
				}
				if((ownEntr.getDest().equals(destRouter))) {
					if(!ownEntr.getNextHop().equals(nextHopRouter)) {
						if (ownEntr.getCost() > costReceived) {
							ownEntr.setCost(costReceived);
							ownEntr.setNextHop(nextHopRouter);
							if (ownTable.getNextHopNeighbors().contains(nextHopRouter))
								ownEntr.setPath(nextHopRouter);
						}
					}
					else {
						ownEntr.setCost(costReceived);
						ownEntr.setNextHop(nextHopRouter);
						if (ownTable.getNextHopNeighbors().contains(nextHopRouter))
							ownEntr.setPath(nextHopRouter);
					}
				}
			}
		}
	}

	//printing table details in the required format
	public synchronized void printTableDetails(ArrayList<RTableDetails> t) {
		for (RTableDetails entry : t) {
			if (entry.getPath().length() == 0)
				System.out.println("Shortest path "+entry.getSource()+"-"+entry.getDest()+": the next hop is "+entry.getNextHop()+" and the cost is "+entry.getCost());
			else
				System.out.println("Shortest path "+entry.getSource()+"-"+entry.getDest()+": the next hop is "+entry.getPath()+" and the cost is "+entry.getCost());
		}
	}

	//method to check link state change
	public synchronized void checkEdgeCostChange(String routerNam) throws NumberFormatException, FileNotFoundException, IOException {
		ArrayList<String> changedRouters = new ArrayList<String>();
		//read the file again and save values in a freshTable object
		RouterTable freshTable = new RouterTable();
		String[] fileParts = this.fileName.split(Pattern.quote("."));
		String routerName = String.valueOf(fileParts[0].charAt(fileParts[0].length()-1));
		RTableDetails rTableDetails;
		FileReader file = new FileReader(new File(this.fileName));		
		brObj = new BufferedReader(file);
		String line = brObj.readLine(); // Reading number of entries in file
		boolean topologyChanged = false; 

		//entry for the same router
		rTableDetails = new RTableDetails(routerName, routerName, "-", 0.0, "");
		freshTable.destPrevNeighbors.clear();
		freshTable.destPrevNeighbors.add(routerName);
		freshTable.nextHopNeighbor.clear();
		freshTable.rTableDetailsList.clear();
		freshTable.rTableDetailsList.add(rTableDetails);

		if (Integer.parseInt(line) != this.nextHopNeighbor.size()){
			System.out.println(this.nextHopNeighbor.size());
			System.out.println(line);
			topologyChanged = true;
		}

		while((line = brObj.readLine())!=null) {
			String[] lineParts = line.split("\\s+");
			if(lineParts.length == 2) {
				String destination = lineParts[0];
				Double cost = Double.parseDouble(lineParts[1]);
				rTableDetails = new RTableDetails(routerName, destination, destination, cost, "");
				freshTable.destPrevNeighbors.add(destination);
				freshTable.nextHopNeighbor.add(destination);
				freshTable.rTableDetailsList.add(rTableDetails);
				freshTable.originalCosts.put(destination, cost);
			}
		}
		if(topologyChanged){
			this.rTableDetailsList = freshTable.rTableDetailsList;
			this.destPrevNeighbors = freshTable.destPrevNeighbors;
			this.nextHopNeighbor = freshTable.nextHopNeighbor;
			this.originalCosts = freshTable.originalCosts;
			System.out.println("Topology Changed! Link added or removed. Re-constructing table...");
		}
		else{
			//compareing original costs and new costs. Adding changed links to changedRouters arraylist
			for (Map.Entry<String, Double> entry : originalCosts.entrySet()) {
				for (RTableDetails entryNew : freshTable.getTableDetails()) {
					if(entryNew.getDest().equals(entry.getKey())) {
						if(Double.compare(entryNew.getCost(), entry.getValue()) != 0) {
							System.out.println("Link State Change Found!");
							changedRouters.add(entry.getKey());
						}
					}
				}
			}

			//scanning changedRouters and changing their cost to the new cost                   
			for (RTableDetails entry : rTableDetailsList) {
				for (RTableDetails entryNew : freshTable.getTableDetails()) {
					if(entryNew.getDest().equals(entry.getDest())) {
						if(changedRouters.contains(entryNew.getDest())) {
							entry.setCost(entryNew.getCost());
							originalCosts.put(entryNew.getDest(), entryNew.getCost());
							System.out.println("Link State Change Fixed!");
						}
						else if(entry.getCost() - entryNew.getCost() > 0){
							entry.setCost(entryNew.getCost());
							entry.setNextHop(entryNew.getNextHop());
						}                       
					}
				}
			}
		}
	}
}
