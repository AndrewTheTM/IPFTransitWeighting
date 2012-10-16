package org.oki.transmodel.IpfWeighting;

import java.util.ArrayList;

/**
 * Extension of ArrayList for PNRWeighting objects that adds a lookup and update routine
 * @author arohne
 *
 */
public class PNRWeights extends ArrayList<PNRWeighting> {
	private static final long serialVersionUID = -5545618633775669736L;
	protected static PNRWeights instance;
	
	PNRWeights(){
	}

	/**
	 * Finds and returns a PNRWeighting object based on the route name and stop provided
	 * @param routeID The route name
	 * @param Stop The stop
	 * @return PNRWeighting object for that route and stop 
	 */
	public PNRWeighting findByRouteStop(String routeID, int Stop){
		for(PNRWeighting i:this){
			if(i.RouteID.equals(routeID) && i.StopNum==Stop)
				return i;
		}
		return null;
	}
	
	public void update(int i, PNRWeighting PNR){
		this.remove(i);
		this.add(i, PNR);
	}
	
}
