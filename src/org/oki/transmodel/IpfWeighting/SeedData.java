package org.oki.transmodel.IpfWeighting;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

/**
 * Holds seed data objects - holds observed trips from boarding-alighting pairs
 * @author arohne
 *
 */
public class SeedData implements Serializable{
	private static final long serialVersionUID = -7914903899670463694L;
	int BoardLocation;
	int AlightLocation;
	int DBBoardLocation;
	int DBAlightLocation;
	double SeedValue;
	double WeightValue;
	int Sequence;

	/**
	 * @param BoardLocation Boarding Location
	 * @param AlightLocation Alighting Location
	 */
	public SeedData(int BoardLocation, int AlightLocation){
		this.BoardLocation=BoardLocation;
		this.AlightLocation=AlightLocation;
		this.DBBoardLocation=BoardLocation;
		this.DBAlightLocation=AlightLocation;
		this.SeedValue=1;
		this.WeightValue=0;
		this.Sequence=0;
	}
	
	/**
	 * @param BoardLocation Boarding Location
	 * @param AlightLocation Alighting Location
	 * @param SeedValue Seed value (observed trips from that boarding-alighting location)
	 */
	public SeedData(int BoardLocation, int AlightLocation, double SeedValue){
		this.BoardLocation=BoardLocation;
		this.AlightLocation=AlightLocation;
		this.DBBoardLocation=BoardLocation;
		this.DBAlightLocation=AlightLocation;
		this.SeedValue=SeedValue;
		this.Sequence=0;
		this.WeightValue=0;
	}
	
	public SeedData() {
		
		this.BoardLocation=0;
		this.AlightLocation=0;
		this.DBBoardLocation=0;
		this.DBAlightLocation=0;
		this.SeedValue=0.1;
		this.Sequence=0;
		this.WeightValue=0;
	}
	
	/**
	 * Gets observed survey records to be used as IPF seed
	 * @param tableSetup The hashtable that defines the database tables, fields, etc.
	 * @param routeNodes The RouteNodes object that has all the nodes for the route
	 * @param RTD The route, time, and day (pipe-delimited) to work on.
	 * @return A List of observed boarding-alighting pairs from the survey table
	 * @author arohne
	 */
	public static List<SeedData> getSeeds(Hashtable<String,String> tableSetup, List<RouteNodes> routeNodes, String RTD){
		Logger logger=IPFMain.logger;
		File dFile = new File((String) tableSetup.get("dataFile"));
		String RTD1=RTD.substring(0, RTD.indexOf("|"));
		String RTD2=RTD.substring(RTD.indexOf("|")+1, RTD.indexOf("|", RTD.indexOf("|")+1));
		String RTD3=RTD.substring(RTD.lastIndexOf("|")+1,RTD.length());
		Table table;
		List<SeedData> inSD=new ArrayList<SeedData>();
		List<SeedData> outSD=new ArrayList<SeedData>();
		
		try {
			table = Database.open(dFile).getTable((String) tableSetup.get("surveyTable"));
			for(Map<String,Object> row:table){
				if(row.get(tableSetup.get("routeField")).toString().equals(RTD1) &&
				row.get(tableSetup.get("directionField")).toString().equals(RTD3) &&
				row.get(tableSetup.get("timeField")).toString().equals(RTD2)){
					int bc=0, ac=0;
					if(row.get(tableSetup.get("BoardingLocationCode")) instanceof Double){
						Double bc1=((double)row.get(tableSetup.get("BoardingLocationCode")));
						bc=bc1.intValue();
					}
					else if(row.get(tableSetup.get("BoardingLocationCode")) instanceof Integer){
						bc=(int)row.get(tableSetup.get("BoardingLocationCode"));
					}
					
					if(row.get(tableSetup.get("AlightingLocationCode")) instanceof Double){
						Double ac1=((double)row.get(tableSetup.get("AlightingLocationCode")));
						ac=ac1.intValue();
					}else if(row.get(tableSetup.get("AlightingLocationCode"))instanceof Integer){
						ac=(int) row.get(tableSetup.get("AlightingLocationCode"));
					}
				
					if(bc!=ac){
						SeedData newSD=new SeedData(bc,ac);
						inSD.add(newSD);
					}
				}
			}
			table.getDatabase().close();
			for(int s=inSD.size()-1;s>=0;s--){
				int bc=0, ac=0;
				bc=inSD.get(s).BoardLocation;
				ac=inSD.get(s).AlightLocation;
				inSD.remove(s);
				boolean found=false;
				for(int t=0;t<outSD.size();t++){
					if(outSD.get(t).BoardLocation==bc && outSD.get(t).AlightLocation==ac){
						outSD.get(t).SeedValue+=1;
						found=true;
						break;
					}
				}
				if(!found){
					if(bc>0 && ac>0){
						SeedData sd=new SeedData(bc,ac);
						outSD.add(sd);
					}
				}
			}
			
			for(int i=0;i<outSD.size();i++){
				SeedData sd=outSD.get(i);
				for(RouteNodes rn:routeNodes){
					boolean foundB=false, foundA=false;
					if(rn.routeID.equals(RTD1) && rn.TimePeriod.equals(RTD2) && rn.Direction.equals(RTD3)){
						for(Node rnStop:rn.Nodes){
							if(sd.BoardLocation==rnStop.Id)
								foundB=true;
							if(sd.AlightLocation==rnStop.Id)
								foundA=true;
							if(foundB && foundA)
								break;
						}
						if(!foundB){
							logger.info("Boarding location not found. Boarding="+sd.BoardLocation);
							int tn=0;
							tn=RouteNodes.FindTransferLocation((Integer)sd.BoardLocation, rn,IPFMain.otherRouteStops);
							if(tn>0){
								logger.debug("Found transfer node of "+tn); //Direct transfer
								sd.BoardLocation=tn;
								outSD.get(i).BoardLocation=tn;
							}
							else
								logger.debug("Transfer node not found.  Looking for "+sd.BoardLocation+" in "+RTD);
						}
						if(!foundA){
							logger.info("Alighting location not found. Alighting="+sd.AlightLocation);
							int tn=0;
							tn=RouteNodes.FindTransferLocation((Integer)sd.AlightLocation, rn, IPFMain.otherRouteStops);
							if(tn>0){
								logger.debug("Found transfer node of "+tn); //Direct transfer
								sd.AlightLocation=tn;
								outSD.get(i).AlightLocation=tn;
							}
							else
								logger.debug("Transfer node not found.  Looking for "+sd.AlightLocation+" in "+RTD);
						}
					}
				}
				
			}
			
			
			return outSD;
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Fills the 0-and-possible seed values in the seed table with 0.1
	 * @param tableSetup The hashtable that defines the database tables, fields, etc.
	 * @param routeNodes The RouteNodes object that has all the nodes for the route
	 * @param seeds The Observed boarding-alighting pairs
	 * @param marginals The marginals (AKA the on-off counts)
	 * @param RTD The Route, Time, and Direction (pipe-delimited)
	 * @return List<SeedData> Seed table
	 * @throws IOException
	 */	
	public static List<SeedData> reSeedTable(Hashtable<String,String> tableSetup, List<RouteNodes> routeNodes, List<SeedData> seeds, List<MarginalData>marginals, String RTD) throws IOException{
		Logger logger=IPFMain.logger;
		logger.debug("In reseed table process");
		String RTD1=RTD.substring(0, RTD.indexOf("|")); //Line name
		String RTD2=RTD.substring(RTD.indexOf("|")+1, RTD.indexOf("|", RTD.indexOf("|")+1)); //Time
		String RTD3=RTD.substring(RTD.lastIndexOf("|")+1,RTD.length()); //Direction		
		
		// Get Stop Order
		Hashtable<Integer, Integer> stopOrder=new Hashtable<Integer,Integer>();
		for(RouteNodes rn:routeNodes){
			int count=0;
			if(rn.routeID.equals(RTD1) && rn.Direction.equals(RTD3) && rn.TimePeriod.equals(RTD2)){
				for(Node n:rn.Nodes)
					stopOrder.put(n.Id, ++count);
			}
		}
		
		// Marginals that are not in the route sequence

		for(MarginalData md:marginals){
			if(stopOrder.get(md.StopID) == null)
				logger.debug("Route "+RTD1+" Stop ID "+md.StopID+" in marginals but not route stops listing");
		}
		
		for(SeedData sd:seeds){
			if(stopOrder.get(sd.BoardLocation)==null)
				logger.debug("Route "+RTD+" Stop ID "+sd.BoardLocation+" in seed boarding locations but not route stops listing");
			if(stopOrder.get(sd.AlightLocation)==null)
				logger.debug("Route "+RTD+" Stop ID "+sd.AlightLocation+" in seed alighting locations but not route stops listing");
		}
		
		// Fill output seed data with actual value or 0.1
		List<SeedData> outSD=new ArrayList<SeedData>();
		for(MarginalData m:marginals){
			for(MarginalData n:marginals){
				boolean skip=false;
				for(SeedData sd:seeds){
					if((m.StopID==sd.BoardLocation && n.StopID==sd.AlightLocation)){   // || (m.StopID==sd.AlightLocation && n.StopID==sd.BoardLocation)
						outSD.add(sd);
						skip=true;
					}
				}
				if(!skip){
					if(stopOrder.get(m.StopID)==null){
						logger.debug("Route "+RTD+" stop "+m.StopID+" not in sequence");
					}else if(stopOrder.get(n.StopID)==null){
						logger.debug("Route "+RTD+" stop "+n.StopID+" not in sequence");
					}else if(stopOrder.get(m.StopID)<stopOrder.get(n.StopID) && m.Boardings>0 && n.Alightings>0){
						SeedData newsd=new SeedData(m.StopID,n.StopID,0.1);
						outSD.add(newsd);
						newsd=new SeedData(n.StopID, m.StopID,0.1);
						outSD.add(newsd);
					}
				}
			}
		}
		return outSD;
	}
}
