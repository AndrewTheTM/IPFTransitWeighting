package org.oki.transmodel.IpfWeighting;

import java.io.Serializable;

/**
 * Class that holds PNR count and survey data and returns station weights
 * @author arohne
 *
 */
public class PNRWeighting implements Serializable{
	private static final long serialVersionUID = 177593569545028188L;
	String RouteID;
	int StopNum;
	int PkPNRCount;
	int PkKNRCount;
	int PkNonMCount;
	int PkPNRSurvey;
	int PkKNRSurvey;
	int PkNonMSurvey;
	int OpPNRCount;
	int OpKNRCount;
	int OpNonMCount;
	int OpPNRSurvey;
	int OpKNRSurvey;
	int OpNonMSurvey;
	
	/**
	 * Value of peak PNR station weight
	 * @return weight value
	 */
	public double getPkPNRWeight()
	{
		if(PkPNRSurvey>0)
			return (double)PkPNRCount/PkPNRSurvey;
		else
			return 1;
	}
	
	/**
	 * Value of peak KNR station weight
	 * @return weight value
	 */
	public double getPkKNRWeight()
	{
		if(PkKNRSurvey>0)
			return (double)PkKNRCount/PkKNRSurvey;
		else
			return 1;
	}
	
	/**
	 * Value of peak non-motorized station weight
	 * @return weight value
	 */
	public double getPkNonMWeight()
	{
		if(PkNonMSurvey>0)
			return (double)PkNonMCount/PkNonMSurvey;
		else
			return 1;
	}
	
	/**
	 * Value of off-peak PNR station weight
	 * @return weight value
	 */
	public double getOpPNRWeight()
	{
		if(OpPNRSurvey>0)
			return (double)OpPNRCount/OpPNRSurvey;
		else
			return 1;
	}
	/**
	 * Value of off-peak KNR station weight
	 * @return weight value
	 */
	public double getOpKNRWeight()
	{
		if(OpKNRSurvey>0)
			return (double)OpKNRCount/OpKNRSurvey;
		else
			return 1;
	}
	/**
	 * Value of off-peak non-motorized station weight
	 * @return weight value
	 */
	public double getOpNonMWeight()
	{
		if(OpNonMSurvey>0)
			return (double)OpNonMCount/OpNonMSurvey;
		else
			return 1;
	}
}
