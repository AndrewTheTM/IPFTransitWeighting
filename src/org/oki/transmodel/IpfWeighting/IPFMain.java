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

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {	
		logger.info("Program Start");
		//Get list of route-time-direction sets to work on
		Hashtable<String, String> tableSetup=new Hashtable<String, String>();
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
		tableSetup.put("PNRTable", "PNRData");
		tableSetup.put("OriginAccess", "OGET");
		tableSetup.put("DestinationEgress","DGET");
		tableSetup.put("ODWeightField", "ODWeight");
		tableSetup.put("StationWeightField","StationWeight");
		tableSetup.put("RuncutPatternTable", "MetroRuncutPattern");
		tableSetup.put("RuncutStopTable", "RuncutsStops");
		tableSetup.put("RuncutPatternIDField", "PatternId");
		tableSetup.put("RuncutStopIDField","stop_id");
		tableSetup.put("StopsTable","Stops");
		tableSetup.put("StopXField","X");
		tableSetup.put("StopYField","Y");
		tableSetup.put("StopsStopIDField","StopID");
		

		List<String> routesRTD=Controller.getRoutes(tableSetup);
		List<MarginalData> marginals=new ArrayList<MarginalData>();
		List<SeedData> seeds=new ArrayList<SeedData>();
		List<WeightData> FinalWeights=new ArrayList<WeightData>();
		PNRWeights PNRs=new PNRWeights();
		
		try {
			PNRs=PNRWeightsDatabase.LoadPNR(tableSetup);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		if(!warmStart){
			//Load Routes into RouteNodes Object - takes a while!
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
			// Loads objects from disk array
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
		}
		
		if(saveObjects){
			// Saves objects to disk to allow for retrieval in subsequent runs
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
			marginals=MarginalData.getMarginals(tableSetup,rtd);
			seeds=SeedData.getSeeds(tableSetup, Routes, rtd);
			List<MarginalData> AdjustedMarginals=new ArrayList<MarginalData>();
			AdjustedMarginals=MarginalDataAdjust.AdjustMarginals(marginals, seeds);
			List<SeedData>outSD2=new ArrayList<SeedData>();
			try {
				logger.info("Reseeding Table");
				outSD2=SeedData.reSeedTable(tableSetup, Routes, seeds, AdjustedMarginals, rtd);
				outSD2=IPFWork.runIPF(outSD2,AdjustedMarginals);
				
				String RTD1=rtd.substring(0, rtd.indexOf("|")); //Route ID
				String RTD2=rtd.substring(rtd.indexOf("|")+1, rtd.indexOf("|", rtd.indexOf("|")+1)); //Time
				String RTD3=rtd.substring(rtd.lastIndexOf("|")+1,rtd.length()); //Direction
				
				
				for(int sdCnt=0;sdCnt<outSD2.size();sdCnt++){
					WeightData fwd=new WeightData();
					fwd.RouteName=RTD1;
					fwd.TimePeriod=RTD2;
					fwd.Direction=RTD3;
					fwd.BoardLocation=outSD2.get(sdCnt).DBBoardLocation;
					fwd.AlightLocation=outSD2.get(sdCnt).DBAlightLocation;
					fwd.ODWeightValue=outSD2.get(sdCnt).WeightValue;
					if((RTD2.equalsIgnoreCase("AM Peak") && RTD3.equalsIgnoreCase("Inbound")) || (RTD2.equalsIgnoreCase("PM Peak") && RTD3.equalsIgnoreCase("Outbound"))){
						for(int pnrCnt=0;pnrCnt<PNRs.size();pnrCnt++){
							if(outSD2.get(sdCnt).BoardLocation==PNRs.get(pnrCnt).StopNum && RTD2.equalsIgnoreCase("AM Peak")){
								fwd.StationWalkWeight=PNRs.get(pnrCnt).getPkNonMWeight();
								fwd.StationPNRWeight=PNRs.get(pnrCnt).getPkPNRWeight();
								fwd.StationKNRWeight=PNRs.get(pnrCnt).getPkKNRWeight();
							}else if(outSD2.get(sdCnt).AlightLocation==PNRs.get(pnrCnt).StopNum && RTD2.equalsIgnoreCase("PM Peak")){
								fwd.StationWalkWeight=PNRs.get(pnrCnt).getPkNonMWeight();
								fwd.StationPNRWeight=PNRs.get(pnrCnt).getPkPNRWeight();
								fwd.StationKNRWeight=PNRs.get(pnrCnt).getPkKNRWeight();
							}
						}
					}else{
						fwd.StationKNRWeight=1;
						fwd.StationPNRWeight=1;
						fwd.StationWalkWeight=1;
					}
					FinalWeights.add(fwd);
				}
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		UpdateDatabase.updateSurveyTableWeights(tableSetup, FinalWeights);
	}



}
