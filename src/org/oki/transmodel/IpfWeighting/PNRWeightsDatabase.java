package org.oki.transmodel.IpfWeighting;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

public class PNRWeightsDatabase {
	/**
	 * Gets PNR trips from the PNR and Survey data tables
	 * @param tableSetup The hashtable that defines the database tables, fields, etc.
	 * @return A PNRWeightList object loaded with counted and surveyed PNR-Location trips
	 * @throws IOException
	 */
	public static PNRWeights LoadPNR(Hashtable<String,String> tableSetup) throws IOException{
		PNRWeights outList=new PNRWeights();
		
		// Read PNR Observed datatable
		File dFile = new File((String) tableSetup.get("dataFile"));
		Table table=Database.open(dFile).getTable(tableSetup.get("PNRTable"));
		for(Map<String,Object> row:table){
			PNRWeighting pnr=new PNRWeighting();
			pnr.RouteID=(String)row.get("Route").toString();
			pnr.StopNum=CInt(row.get("PNRStop"));
			pnr.PkPNRCount=CInt(row.get("PkPNR"));
			pnr.PkKNRCount=CInt(row.get("PkKNR"));
			pnr.PkNonMCount=CInt(row.get("PkNonM"));
			pnr.OpPNRCount=CInt(row.get("OpPNR"));
			pnr.OpKNRCount=CInt(row.get("OpKNR"));
			pnr.OpNonMCount=CInt(row.get("OpNonM"));
			outList.add(pnr);
		}
		
		dFile = new File((String) tableSetup.get("dataFile"));
		table=Database.open(dFile).getTable(tableSetup.get("surveyTable"));
		for(Map<String,Object> row:table){
			if(outList.findByRouteStop(row.get(tableSetup.get("routeField")).toString(), CInt(row.get(tableSetup.get("BoardingLocationCode"))))!=null ||
					outList.findByRouteStop(row.get(tableSetup.get("routeField")).toString(), CInt(row.get(tableSetup.get("AlightingLocationCode"))))!=null){
				PNRWeighting pnr=outList.findByRouteStop(row.get(tableSetup.get("routeField")).toString(), CInt(row.get(tableSetup.get("BoardingLocationCode"))));
				if(pnr==null) pnr=outList.findByRouteStop(row.get(tableSetup.get("routeField")).toString(), CInt(row.get(tableSetup.get("AlightingLocationCode"))));
				PNRWeighting pnrUpd=pnr;
				
				//Check for pk or op
				if((row.get("TOD").equals("AM Peak") && row.get(tableSetup.get("directionField")).equals("INBOUND"))){
					//AM Peak Period Boarded at PNR
					if(CInt(row.get("OGET"))<=2)
						pnrUpd.PkNonMSurvey=pnr.PkNonMSurvey+1;
					else if(CInt(row.get("OGET"))==5)
						pnrUpd.PkKNRSurvey=pnr.PkKNRSurvey+1;
					else
						pnrUpd.PkPNRSurvey=pnr.PkPNRSurvey+1;
				}else if(row.get("TOD").equals("PM Peak") && row.get(tableSetup.get("directionField")).equals("OUTBOUND")){
					//PM Peak Period Alighted at PNR
					if(CInt(row.get("OGET"))<=2)
						pnrUpd.PkNonMSurvey=pnr.PkNonMSurvey+1;
					else if(CInt(row.get("OGET"))==5)
						pnrUpd.PkKNRSurvey=pnr.PkKNRSurvey+1;
					else
						pnrUpd.PkPNRSurvey=pnr.PkPNRSurvey+1;
				}else if(row.get("TOD").equals("PM Peak") && row.get(tableSetup.get("directionField")).equals("INBOUND")){
					//PM Peak Period Boarded at PNR (Reverse Commute)
					if(CInt(row.get("OGET"))<=2)
						pnrUpd.OpNonMSurvey=pnr.OpNonMSurvey+1;
					else if(CInt(row.get("OGET"))==5)
						pnrUpd.OpKNRSurvey=pnr.OpKNRSurvey+1;
					else
						pnrUpd.OpPNRSurvey=pnr.OpPNRSurvey+1;
				}
				outList.update(outList.indexOf(pnr), pnrUpd);
				
			}else if(outList.findByRouteStop(row.get(tableSetup.get("routeField")).toString(), CInt(row.get(tableSetup.get("AlightingLocationCode"))))!=null){
				//This trip Alighted at a PNR location
				PNRWeighting pnr=outList.findByRouteStop(row.get(tableSetup.get("routeField")).toString(), CInt(row.get(tableSetup.get("AlightingLocationCode"))));
				PNRWeighting pnrUpd=pnr;
				//Check for pk or op
				if((row.get("TOD").equals("PM Peak") && row.get("DIRECTION").equals("INBOUND"))){
					//PM Peak Period Alighted at PNR
					if(CInt(row.get("DGET"))<=2)
						pnrUpd.PkNonMSurvey=pnr.PkNonMSurvey+1;
					else if(CInt(row.get("DGET"))==5)
						pnrUpd.PkKNRSurvey=pnr.PkKNRSurvey+1;
					else
						pnrUpd.PkPNRSurvey=pnr.PkPNRSurvey+1;
					
				}else if(row.get("TOD").equals("AM Peak") && row.get("DIRECTION").equals("INBOUND")){
					//AM Peak Period Alighted at PNR (Reverse Commute)
					if(CInt(row.get("DGET"))<=2)
						pnrUpd.OpNonMSurvey=pnr.OpNonMSurvey+1;
					else if(CInt(row.get("DGET"))==5)
						pnrUpd.OpKNRSurvey=pnr.OpKNRSurvey+1;
					else
						pnrUpd.OpPNRSurvey=pnr.OpPNRSurvey+1;
				}
				outList.update(outList.indexOf(pnr), pnrUpd);
			}
			
		}
		return outList;
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
