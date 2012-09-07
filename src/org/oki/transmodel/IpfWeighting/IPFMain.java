package org.oki.transmodel.IpfWeighting;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {		
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
		
		//TODO: Sampled route IPF adjustments
		
		List<MarginalData> marginals=new ArrayList<MarginalData>();
		for(String rtd:routesRTD){
			System.out.println(rtd);
			//Fill Marginals Objects
			marginals=MarginalData.getMarginals(tableSetup,rtd);
			
			break;
		}
		
		//TODO: Non-sampled route adjustment (vehicle adjustments)
		
		
		
		int a=1;
		System.out.println(a);
	}



}
