package org.oki.transmodel.IpfWeighting;

import java.util.ArrayList;
import java.util.List;

/**
 * Experimental class to adjust marginal data to be able to weight all records.  This will adjust the marginals where a
 * seed value exists and the marginal claims that no boardings/alightings happened at that stop for that route and time
 * period.
 * @author arohne
 *
 */
public class MarginalDataAdjust {
	/**
	 * Procedure to adjust marginals
	 * @param inputM Input MarginalData object
	 * @param seeds Input SeedData object
	 * @return Adjusted marginals
	 */
	public static List<MarginalData> AdjustMarginals(List<MarginalData> inputM, List<SeedData> seeds){
		List<MarginalData> out=new ArrayList<MarginalData>();
		for(MarginalData md:inputM){
			for(SeedData seed:seeds){
				if(md.StopID==seed.BoardLocation && md.Boardings==0)
					if(seed.SeedValue>0)
						md.Boardings=(int) seed.SeedValue;
					else
						md.Boardings++;
				if(md.StopID==seed.AlightLocation && md.Alightings==0)
					if(seed.SeedValue>0)
						md.Alightings=(int) seed.SeedValue;
					else
						md.Alightings++;
			}
			out.add(md);
		}
		return out;
	}
}
