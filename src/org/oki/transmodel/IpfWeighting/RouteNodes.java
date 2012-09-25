package org.oki.transmodel.IpfWeighting;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Class to hold route node listings, in sequence and provide a search interface to find transfer points
 * @author arohne
 *
 */
public class RouteNodes {
	String routeID;
	String TimePeriod;
	String Direction;
	List<Node>Nodes=new ArrayList<Node>();
	
	/**
	 * Locates transfer nodes based on a provided node and all input routes
	 * @param FromNode A node that is expected to not be on the current route
	 * @param InputRouteNodes All of the route nodes
	 * @return The transfer node
	 */
	public static int FindTransferLocation(Integer FromNode, RouteNodes currentRoute, List<OtherStops> other)
	{
		//Check to see if the node is in this group
		Logger logger=IPFMain.logger;
		logger.debug("Finding Transfer Location for "+FromNode);
		
		for(RouteNodes rn:IPFMain.Routes){
			for(Node node:rn.Nodes){
				if(node.Id==FromNode){
					for(Node ORxfrNode:currentRoute.Nodes){
						for(Node xfrNode:rn.Nodes){
							if(ORxfrNode.Id==xfrNode.Id)
								return xfrNode.Id; //This is the transfernode
						}
					}
				}
			}
		}
		
		logger.debug("Looking at Metro within 0.25 miles");
		for(RouteNodes rn:IPFMain.Routes){
			for(Node node:rn.Nodes){
				if(node.Id==FromNode){
					
					for(Node ORxfrNode:currentRoute.Nodes){
						for(Node xfrNode:rn.Nodes){
							if(getDistance(ORxfrNode.x,ORxfrNode.y,xfrNode.x,xfrNode.y)<=0.25){
								logger.debug("Found Metro 0.25 mile transfer node "+xfrNode.Id);
								return xfrNode.Id;
							}
						}
					}
				}
			}
		}
		
		logger.debug("No Transfer-node found.  Looking for a walk node within 0.25 miles");
		for(OtherStops os:other){
			boolean hasFrom=false;
			logger.debug("Looking in "+os.RouteId);
			for(Node n:os.Nodes){
				if(n.Id==FromNode){
					hasFrom=true;
					logger.debug("Has From Node");
				}
			}
			
			if(hasFrom){
				for(Node n:os.Nodes){
					for(Node crn:currentRoute.Nodes){
						if(getDistance(n.x,n.y,crn.x,crn.y)<=0.25){
							logger.debug("Found one! "+crn.Id);
							return n.Id;
						}
					}
				}
			}	
		}
		logger.warn("NO TRANSFER: "+FromNode);
		//If it gets to here, there is no common node
		return -1;
	}
	
	private static double getDistance(double x1, double y1, double x2, double y2){
		return Math.sqrt(Math.pow((x2-x1),2)+Math.pow((y2-y1), 2))/5280;
	}
}
