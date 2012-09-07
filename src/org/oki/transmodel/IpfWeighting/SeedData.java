package org.oki.transmodel.IpfWeighting;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

public class SeedData {
	int BoardLocation;
	int AlightLocation;
	int SeedValue;
	
	public static int getNumStops(Hashtable<String,String> tableSetup){
		File dFile = new File((String) tableSetup.get("dataFile"));
		Table table;
		try {
			table = Database.open(dFile).getTable((String) tableSetup.get("surveyTable"));
			for(Map<String,Object> row:table){
				row.get("line"); //1, 2, 3, 82, etc.
				row.get("direction"); //Inbound, Outbound, Eastbound, Westbound
				row.get("sequence"); //1, 2, 3, etc
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			
		return 0;
	}
	
	public static void createSeedTable(Hashtable<String,String> tableSetup){
		/*
		 * Creates the initial seed table with zero-ed values
		 */
		
	}
	
	public static void fillSeedTable(Hashtable<String,String> tableSetup){
		/*
		 * Fills seed table with records from the spreadsheet
		 */
	}
	
	public static void reSeedTable(Hashtable<String,String> tableSetup){
		/*
		 * Fills the 0-and-possible seed values in the seed table with 0.1
		 */
	}
}
