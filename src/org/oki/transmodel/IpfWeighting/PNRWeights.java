package org.oki.transmodel.IpfWeighting;

import java.util.ArrayList;

public class PNRWeights extends ArrayList<PNRWeighting> {
	private static final long serialVersionUID = -5545618633775669736L;
	protected static PNRWeights instance;
	
	PNRWeights(){
	}
	
	//TODO: Document
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
