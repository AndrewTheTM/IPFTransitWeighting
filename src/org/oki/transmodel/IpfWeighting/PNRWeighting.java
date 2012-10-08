package org.oki.transmodel.IpfWeighting;

import java.io.Serializable;

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
	
	//TODO: Document
	public double getPkPNRWeight()
	{
		return (double)Math.min(PkPNRCount/Math.max(PkPNRSurvey,0.0001),1);
	}
	
	public double getPkKNRWeight()
	{
		return (double)Math.min(PkKNRCount/Math.max(PkKNRSurvey,0.0001),1);
	}
	
	public double getPkNonMWeight()
	{
		return (double)Math.min(PkNonMCount/Math.max(PkNonMSurvey,0.0001),1);
	}
	
	public double getOpPNRWeight()
	{
		return (double)Math.min(OpPNRCount/Math.max(OpPNRSurvey,0.0001),1);
	}
	public double getOpKNRWeight()
	{
		return (double)Math.min(OpKNRCount/Math.max(OpKNRSurvey,0.0001),1);
	}
	public double getOpNonMWeight()
	{
		return (double)Math.min(OpNonMCount/Math.max(OpNonMSurvey,0.0001),1);
	}
}
