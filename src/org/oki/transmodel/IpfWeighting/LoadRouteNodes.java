package org.oki.transmodel.IpfWeighting;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

/**
 * Loads and manages RouteNode objects
 * @author arohne
 *
 */
public class LoadRouteNodes {
	
	/** 
	 * Loads route nodes
	 * @param tableSetup The hashtable that defines the database tables, fields, etc.
	 * @param RTD The route, time, and day (pipe-delimited) to work on.
	 * @return a RouteNode object with a list of nodes for the selected RTD
	 * @throws IOException
	 */
	public static RouteNodes Load(Hashtable<String,String> tableSetup, String RTD) throws IOException{
		Logger logger=IPFMain.logger;
		logger.debug("Route Nodes Loading"+RTD);
		String RTD1=RTD.substring(0, RTD.indexOf("|")); //Line name
		String RTD2=RTD.substring(RTD.indexOf("|")+1, RTD.indexOf("|", RTD.indexOf("|")+1)); //Time
		String RTD3=RTD.substring(RTD.lastIndexOf("|")+1,RTD.length()); //Direction		
		RouteNodes rn=new RouteNodes();
		File dFile = new File((String) tableSetup.get("dataFile"));
		Table rcTransTable=Database.open(dFile).getTable("MetroRuncutPattern");
		for(Map<String,Object> rcRow:rcTransTable){
			if(rcRow.get("RTCODE").toString().equals(RTD1)){ 
				Table pTable = Database.open(dFile).getTable("RuncutsStops"); 
				for(Map<String,Object> sRow:pTable){	
					if(sRow.get("pattern_id").equals(rcRow.get("PatternId"))){
						rn.routeID=RTD1;
						rn.Direction=RTD3;
						rn.TimePeriod=RTD2;
						Node nn=new Node();
						nn.Id=((Double) (sRow.get("stop_id"))).intValue();
						Table betterStopTable=Database.open(dFile).getTable("Stops");
						for(Map<String,Object> bstRow:betterStopTable){
							if(((Double)bstRow.get("StopID")).intValue()==nn.Id){
								nn.x=((Double) bstRow.get("X"));
								nn.y=((Double) bstRow.get("Y"));
								break;
							}
						}
						rn.Nodes.add(nn);
					}
				}
			}
		}
		rcTransTable.getDatabase().close();
		dFile=null;
		
		return rn;
		
	}
}
