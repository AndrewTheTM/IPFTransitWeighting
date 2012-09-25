package org.oki.transmodel.IpfWeighting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
/*
 * Author: Andrew Rohne
 * Date: 7-Sep-2012
 * 
 * This program applies the weighting method used by Cambridge Systematics in Madison, WI
 * See:
 * Komanduri, Anurag and Kimon Proussaloglou. "Getting the Most out of a Transit Onboard Survey: Results from Madison", TRB Annual Meeting,
 *   Washington, DC, np, 2010, 10-1331.
 */

public class IPFMain {

	public static Logger logger = Logger.getLogger(IPFMain.class);
	public static List<RouteNodes> Routes=new ArrayList<RouteNodes>();
	public static List<OtherStops> otherRouteStops=new ArrayList<OtherStops>();

	public static void main(String[] args) {	
		logger.info("Program Start");
		//Get list of route-time-direction sets to work on
		Hashtable<String, String> tableSetup=new Hashtable<String, String>();
		tableSetup.put("dataFile", "S:\\User\\Rohne\\Projects\\Transit OB Survey\\Weighting\\NewMetroWeight.mdb");
		tableSetup.put("surveyTable","MetroData");
		tableSetup.put("scheduleTable", "SCHEDULE");
		tableSetup.put("StopOnOffTable", "BusStopRTD");
		tableSetup.put("routeField","RTCODE");
		tableSetup.put("timeField","TOD");
		tableSetup.put("directionField","DIRECTION");
		tableSetup.put("sof_routeField","route");
		tableSetup.put("sof_timeField","tod");
		tableSetup.put("sof_directionField","direction");
		tableSetup.put("sof_stopid", "geoid");
		tableSetup.put("sof_boardingField", "board");
		tableSetup.put("sof_alightingField", "alight");

		List<String> routesRTD=Controller.getRoutes(tableSetup);
		
		//Load Routes into RouteNodes Object
		logger.info("Preparing to load nodes");
		int cnt=0;
		for(String RTD:routesRTD)
			try {
				if(++cnt%10==0)
					logger.info("Loading "+cnt+" of "+routesRTD.size());
				Routes.add(LoadRouteNodes.Load(tableSetup, RTD));
				//if(cnt==10)
					//break;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		logger.info("Nodes loaded");
		
		logger.info("Loading Other Routes...");
		otherRouteStops=OtherStops.getOtherStops(tableSetup);
		logger.info("Finished loading other routes.");
		
		//TODO: Sampled route IPF adjustments
		
		List<MarginalData> marginals=new ArrayList<MarginalData>();
		List<SeedData> seeds=new ArrayList<SeedData>();
		
		for(String rtd:routesRTD){
			logger.info("Filling marginal objects. "+rtd);
			//Fill Marginals Objects
			marginals=MarginalData.getMarginals(tableSetup,rtd);
			seeds=SeedData.getSeeds(tableSetup, Routes, rtd);
			List<SeedData>outSD2=new ArrayList<SeedData>();
			try {
				logger.info("Reseeding Table");
				outSD2=SeedData.reSeedTable(tableSetup, Routes, seeds, marginals, rtd);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			//break;
		}
		int a=1;
		System.out.println(a);
		//TODO: Non-sampled route adjustment (vehicle adjustments)
		
		
	}



}
