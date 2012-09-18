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
	
	/**
	 * Gets ONE marginal from the Stop On-Off Table
	 * @param tableSetup The hashtable that defines the database tables, fields, etc.
	 * @param RTD The route, time, and day (pipe-delimited) to work on.
	 * @return A list of marginals for the input RTD
	 */
	public static List<MarginalData> getMarginals(Hashtable<String,String> tableSetup, String RTD){
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
			}
			return output;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
