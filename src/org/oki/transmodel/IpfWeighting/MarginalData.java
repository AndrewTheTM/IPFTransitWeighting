package org.oki.transmodel.IpfWeighting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

public class MarginalData {
	int StopID;
	int Boardings;
	int Alightings;
	public MarginalData(){
		StopID=0;
		Boardings=0;
		Alightings=0;
	}
	public MarginalData(int BusStopID){
		StopID=BusStopID;
		Boardings=0;
		Alightings=0;
	}
	public MarginalData(int BusStopID, int Board, int Alight){
		StopID=BusStopID;
		Boardings=Board;
		Alightings=Alight;
	}
	public void addBoardings(int Boards){
		Boardings+=Boards;
	}
	
	public static List<MarginalData> getMarginals(Hashtable<String,String> tableSetup, String RTD){
		/*
		 * Gets ONE marginal from the Stop On-Off Table
		 */
		List<MarginalData> output=new ArrayList<MarginalData>();
		File dFile = new File((String) tableSetup.get("dataFile"));
		String RTD1=RTD.substring(0, RTD.indexOf("|"));
		String RTD2=RTD.substring(RTD.indexOf("|")+1, RTD.indexOf("|", RTD.indexOf("|")+1));
		String RTD3=RTD.substring(RTD.lastIndexOf("|")+1,RTD.length());
		try {
			Table table=Database.open(dFile).getTable((String) tableSetup.get("StopOnOffTable"));
			for(Map<String,Object> row:table){
				if(row.get((String)tableSetup.get("sof_routeField")).toString().equalsIgnoreCase(RTD1) && 
						row.get((String)tableSetup.get("sof_timeField")).toString().equalsIgnoreCase(RTD2) &&
						row.get((String)tableSetup.get("sof_directionField")).toString().equalsIgnoreCase(RTD3))
					output.add(new MarginalData((int)row.get((String)tableSetup.get("sof_stopid")),
							((Double)row.get((String)tableSetup.get("sof_boardingField"))).intValue(),
							((Double)row.get((String)tableSetup.get("sof_alightingField"))).intValue()));  
			} //looping through table records
			
			int brdTot=0, altTot=0;
			for(MarginalData md:output){
				brdTot+=md.Boardings;
				altTot+=md.Alightings;
			}
			System.out.println(brdTot);
			System.out.println(altTot);
			
			return output;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
