package org.oki.transmodel.IpfWeighting;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
		
		/*
		 * Temporary and trying
		 */
		boolean warmStart=true;
		boolean saveObjects=false;
		
		//tableSetup.put("dataFile", "C:\\Users\\Andrew\\Documents\\NewMetroWeight.mdb");
		tableSetup.put("dataFile", "S:\\User\\Rohne\\Projects\\Transit OB Survey\\Weighting\\NewMetroWeight.mdb");
		tableSetup.put("datapath", "S:\\User\\Rohne\\Projects\\Transit OB Survey\\Weighting\\");
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
		tableSetup.put("BoardingLocationCode", "BRDCODE");
		tableSetup.put("AlightingLocationCode","ALTCODE");

		List<String> routesRTD=Controller.getRoutes(tableSetup);
		List<MarginalData> marginals=new ArrayList<MarginalData>();
		List<SeedData> seeds=new ArrayList<SeedData>();
		
		if(!warmStart){
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
			
			
		}else{
			try {
				FileInputStream f_in=new FileInputStream(tableSetup.get("datapath")+"RouteRTDObject.obj");
				ObjectInputStream obj_in=new ObjectInputStream(f_in);
				Object inputObject=obj_in.readObject();
				if(inputObject instanceof List<?>)
					routesRTD=(List<String>) inputObject; 
				obj_in.close();
				f_in.close();
				
				f_in=new FileInputStream(tableSetup.get("datapath")+"RoutesObject.obj");
				obj_in=new ObjectInputStream(f_in);
				Object inputObject2=obj_in.readObject(); //Not serializable error
				if(inputObject2 instanceof List<?>)
					Routes=(List<RouteNodes>) inputObject2;
				obj_in.close();
				f_in.close();
				
				f_in=new FileInputStream(tableSetup.get("datapath")+"otherRouteStopsObject.obj");
				obj_in=new ObjectInputStream(f_in);
				Object inputObject3=obj_in.readObject();
				if(inputObject3 instanceof List<?>)
					otherRouteStops=(List<OtherStops>) inputObject3;
				obj_in.close();
				f_in.close();
				
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			/*
			 * load obects from disk object array
			 */
		}
		
		if(saveObjects){
			try {
				//RoutesRTD
				FileOutputStream f_out = new FileOutputStream(tableSetup.get("datapath")+"RouteRTDObject.obj");
				ObjectOutputStream obj_out=new ObjectOutputStream(f_out);
				obj_out.writeObject(routesRTD);
				obj_out.close();
				f_out.close();
				//Routes
				f_out=new FileOutputStream(tableSetup.get("datapath")+"RoutesObject.obj"); //die - object not serializable
				obj_out=new ObjectOutputStream(f_out);
				obj_out.writeObject(Routes);
				obj_out.close();
				f_out.close();
				//OtherRouteStops
				f_out=new FileOutputStream(tableSetup.get("datapath")+"otherRouteStopsObject.obj");
				obj_out=new ObjectOutputStream(f_out);
				obj_out.writeObject(otherRouteStops);
				obj_out.close();
				f_out.close();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		for(String rtd:routesRTD){
			logger.info("Filling marginal objects. "+rtd);
			//Fill Marginals Objects
			
			marginals=MarginalData.getMarginals(tableSetup,rtd);
			seeds=SeedData.getSeeds(tableSetup, Routes, rtd);
			List<SeedData>outSD2=new ArrayList<SeedData>();
			try {
				logger.info("Reseeding Table");
				outSD2=SeedData.reSeedTable(tableSetup, Routes, seeds, marginals, rtd);
				//TODO: go into IPF Procedure
				outSD2=IPFWork.runIPF(outSD2,marginals);
				
			
				//TODO: PNR location adjustments
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			//NOTE: This is good to here.  The marginals and seeds have been used to make weights in Excel.

			//break;
		}
		
		int a=1;
		System.out.println(a);
		//TODO: Non-sampled route adjustment (vehicle adjustments)
		
		
	}



}
