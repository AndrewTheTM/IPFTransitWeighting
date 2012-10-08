package org.oki.transmodel.IpfWeighting;

import java.io.Serializable;

public class WeightData implements Serializable{
	private static final long serialVersionUID = 6733451823452183834L;
	String RouteName;
	String Direction;
	String TimePeriod;
	int BoardLocation;
	int AlightLocation;
	double ODWeightValue;
	double StationWalkWeight;
	double StationPNRWeight;
	double StationKNRWeight;
}
