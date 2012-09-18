package org.oki.transmodel.IpfWeighting;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to hold route node listings, in sequence and provide a search interface to find transfer points
 * @author arohne
 *
 */
public class RouteNodes {
	String routeID;
	String TimePeriod;
	String Direction;
	List<Integer>Nodes=new ArrayList<Integer>();
	
	/**
	 * Locates transfer nodes based on a provided node and all input routes
	 * @param FromNode A node that is expected to not be on the current route
	 * @param InputRouteNodes All of the route nodes
	 * @return The transfer node
	 */
	public static int FindTransferLocation(Integer FromNode)
	{
		//Check to see if the node is in this group
		boolean checkNode=false;
		for(RouteNodes rn:IPFMain.Routes){
			for(Integer node:rn.Nodes){
				if(node==FromNode){
					//found a route with the fromnode
					//TODO: check and see if there is a common node
					checkNode=true;
					break;
				}
			}
			if(!checkNode)
			return -1;
			
			
		}
		
		
		
		

		//If it gets to here, there is no common node
		return -1;
	}
}
