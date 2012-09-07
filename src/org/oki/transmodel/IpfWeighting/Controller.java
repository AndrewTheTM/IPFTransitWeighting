package org.oki.transmodel.IpfWeighting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

/*
 * author: Andrew Rohne
 * date: 7-Sep-2012
 */
public class Controller {
	/*
	 * Class to open main survey table and prepare a list of individual routes, times, and directions to IPF on.
	 */
	public static List<String> getRoutes(Hashtable<String, String> tableSetup){
		try {
			List<String> outputRTDs=new ArrayList<String>();
			File dFile = new File((String) tableSetup.get("dataFile"));
			Table table=Database.open(dFile).getTable((String) tableSetup.get("surveyTable"));
			for(Map<String,Object> row:table){
				String tableRTD=(String)row.get((String) tableSetup.get("routeField"))+"|"+(String)row.get((String) tableSetup.get("timeField"))+"|"+(String)row.get((String) tableSetup.get("directionField"));
				if(outputRTDs.size()==0)
					outputRTDs.add(tableRTD);
				boolean addMe=true;
				for(String rtd:outputRTDs){
					if(rtd.equalsIgnoreCase(tableRTD)){
						addMe=false;
						break;
					}
				}
				if(addMe)
					outputRTDs.add(tableRTD);
			}
			return outputRTDs;
		} catch (IOException e) {
			e.printStackTrace();
			List<String> output=new ArrayList<String>();
			output.add("Error");
			return output;
		}
	}

}
