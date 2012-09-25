package org.oki.transmodel.IpfWeighting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

/**
 * Class to hold non-Metro stops
 * @author arohne
 *
 */
public class OtherStops {

	String RouteId;
	List<Node> Nodes=new ArrayList<Node>();
	
	/**
	 * @param tableSetup The hashtable that defines the database tables, fields, etc.
	 * @return List of non-Metro stops
	 */
	public static List<OtherStops> getOtherStops(Hashtable<String,String> tableSetup){
		List<OtherStops> output=new ArrayList<OtherStops>();
		//Logger logger=IPFMain.logger;
		File dFile = new File((String) tableSetup.get("dataFile"));
		Table table;
		try {
			table = Database.open(dFile).getTable("Stops");
			for(Map<String,Object> row:table){
				if(!row.get("BusNum").toString().matches("MET.*")){
					Node newNode=new Node();
					newNode.Id=((Double) row.get("StopID")).intValue();
					newNode.x=(double) row.get("X");
					newNode.y=(double) row.get("Y");
					output=checkOrAdd(output,row.get("BusNum").toString(),newNode);
					//logger.debug("Found "+row.get("BusNum"));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return output;
	}
	
	private static List<OtherStops> checkOrAdd(List<OtherStops> inStops, String routeID, Node addStop){
		boolean found=false;
		for(int i=0;i<inStops.size();i++){
			if(inStops.get(i).RouteId.equals(routeID)){
				inStops.get(i).Nodes.add(addStop);
				found=true;
				break;
			}
		}
		if(!found){
			OtherStops nStop=new OtherStops();;
			nStop.RouteId=routeID;
			nStop.Nodes.add(addStop);
			inStops.add(nStop);
		}
		return inStops;
		
	}
}
