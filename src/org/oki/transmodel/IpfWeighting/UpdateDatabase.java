package org.oki.transmodel.IpfWeighting;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

//TODO: Document
public class UpdateDatabase {
	public static void updateSurveyTableWeights(Hashtable<String,String> tableSetup, List<WeightData> FinalWeights){
		File dFile = new File((String) tableSetup.get("dataFile"));
		try {
			Table table = Database.open(dFile).getTable((String) tableSetup.get("surveyTable"));
			Cursor c=Cursor.createCursor(table);
			c.reset();
			c.getNextRow();
			while(c!=null){
				Map<String,Object> row=c.getCurrentRow();
				for(WeightData wd:FinalWeights){
					if(row.get(tableSetup.get("routeField")).toString().equals(wd.RouteName) &&
							row.get(tableSetup.get("directionField")).toString().equals(wd.Direction) &&
							row.get(tableSetup.get("timeField")).toString().equals(wd.TimePeriod) &&
							CInt(row.get(tableSetup.get("BoardingLocationCode")))==wd.BoardLocation &&
							CInt(row.get(tableSetup.get("AlightingLocationCode")))==wd.AlightLocation){
						row.put("ODWeight", wd.ODWeightValue);
						if(wd.Direction.equalsIgnoreCase("Inbound") && wd.TimePeriod.equalsIgnoreCase("AM Peak")){
							if(CInt(row.get("OGET"))<=2){ //TODO: Hash
								row.remove("StationWeight"); //TODO: Hash
								row.put("StationWeight", wd.StationWalkWeight); //TODO: Hash
							}else if(CInt(row.get("OGET"))==5){ //TODO: Hash
								row.remove("StationWeight"); //TODO: Hash
								row.put("StationWeight", wd.StationKNRWeight); //TODO: Hash
							}
							else{
								row.remove("StationWeight"); //TODO: Hash
								row.put("StationWeight", wd.StationPNRWeight); //TODO: Hash
							}
							
						}else if(wd.Direction.equalsIgnoreCase("Outbound") && wd.TimePeriod.equalsIgnoreCase("PM Peak")){
							if(CInt(row.get("DGET"))<=2){ //TODO: Hash
								row.remove("StationWeight"); //TODO: Hash
								row.put("StationWeight", wd.StationWalkWeight); //TODO: Hash
							}
							else if(CInt(row.get("DGET"))==5){ //TODO: Hash
								row.remove("StationWeight"); //TODO: Hash
								row.put("StationWeight", wd.StationKNRWeight); //TODO: Hash
							}
							else{
								row.remove("StationWeight"); //TODO: Hash
								row.put("StationWeight", wd.StationPNRWeight); //TODO: Hash
							}
						}
						c.updateCurrentRow(row);
						
						break;
					}
				}
				c.getNextRow();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * Private sub to convert to integer
	 * @param input Any numerical Object (integer or double)
	 * @return integer value of input
	 */
	private static int CInt(Object input){
		if(input instanceof Integer)
			return (int)input;
		else if(input instanceof Double)
			return ((Double)input).intValue();
		return -1;
	}
}
