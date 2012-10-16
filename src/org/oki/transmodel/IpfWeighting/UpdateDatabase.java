package org.oki.transmodel.IpfWeighting;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

//import org.apache.log4j.Logger;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

/**
 * Class to update database
 * @author arohne
 */
public class UpdateDatabase {
	/**
	 * Updates survey table with weights
	 * @param tableSetup The hashtable that defines the database tables, fields, etc.
	 * @param FinalWeights The object holding the final weight values to be written
	 */
	public static void updateSurveyTableWeights(Hashtable<String,String> tableSetup, List<WeightData> FinalWeights){
		File dFile = new File((String) tableSetup.get("dataFile"));
		//Logger logger=IPFMain.logger;
		try {
			Table table = Database.open(dFile).getTable((String) tableSetup.get("surveyTable"));
			Cursor cur=Cursor.createCursor(table);
			
			cur.reset();
			while(cur.moveToNextRow()){
				Map<String,Object> row=cur.getCurrentRow();
				for(WeightData wd:FinalWeights){
					Map<String,Object> newRow=new HashMap<String,Object>();
					//logger.debug("row\t"+row.get(tableSetup.get("routeField")).toString()+"\t"+row.get(tableSetup.get("directionField")).toString()+"\t"+row.get(tableSetup.get("timeField")).toString()+"\t"+CInt(row.get(tableSetup.get("BoardingLocationCode")))+"\t"+CInt(row.get(tableSetup.get("AlightingLocationCode"))));
					//logger.debug("wd \t"+wd.RouteName+"\t"+wd.Direction+"\t"+wd.TimePeriod+"\t"+wd.BoardLocation+"\t"+wd.AlightLocation);
					if(row.get(tableSetup.get("routeField")).toString().equalsIgnoreCase(wd.RouteName) &&
							row.get(tableSetup.get("directionField")).toString().equalsIgnoreCase(wd.Direction) &&
							row.get(tableSetup.get("timeField")).toString().equalsIgnoreCase(wd.TimePeriod) &&
							CInt(row.get(tableSetup.get("BoardingLocationCode")))==wd.BoardLocation &&
							CInt(row.get(tableSetup.get("AlightingLocationCode")))==wd.AlightLocation){
						//FIXME: Somehow in the last round of changes, execution never gets to this point.
						newRow.put("ODWeight", wd.ODWeightValue); //TODO: Hash
						if(wd.Direction.equalsIgnoreCase("Inbound") && wd.TimePeriod.equalsIgnoreCase("AM Peak")){
							if(CInt(row.get(tableSetup.get("OriginAccess")))<=2){
								newRow.put(tableSetup.get("StationWeightField"), wd.StationWalkWeight);
							}else if(CInt(row.get(tableSetup.get("OriginAccess")))==5){ 
								newRow.put(tableSetup.get("StationWeightField"), wd.StationKNRWeight); 
							}else{
								newRow.put(tableSetup.get("StationWeightField"), wd.StationPNRWeight); 
							}
							
						}else if(wd.Direction.equalsIgnoreCase("Outbound") && wd.TimePeriod.equalsIgnoreCase("PM Peak")){
							if(CInt(row.get(tableSetup.get("DestinationEgress")))<=2){ 
								newRow.put(tableSetup.get("StationWeightField"), wd.StationWalkWeight); 
							}else if(CInt(row.get(tableSetup.get("DestinationEgress")))==5){ 
								newRow.put(tableSetup.get("StationWeightField"), wd.StationKNRWeight);
							}else{
								newRow.put(tableSetup.get("StationWeightField"), wd.StationPNRWeight); 
							}
						}
						Column col=table.getColumn(tableSetup.get("StationWeightField"));
						cur.setCurrentRowValue(col, newRow.get(tableSetup.get("StationWeightField")));
						col=table.getColumn("ODWeight"); //TODO: Hash
						cur.setCurrentRowValue(col, newRow.get("ODWeight"));
						//break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e){
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
