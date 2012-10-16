package org.oki.transmodel.IpfWeighting;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class IPFWork {
	
	/**
	 * Runs the IPF Procedure
	 * @param sd Input SeedData object
	 * @param md Input MarginalData object
	 * @return a SeedData object with weights
	 */
	public static List<SeedData> runIPF(List<SeedData> sd, List<MarginalData> md){
		double aad=0;
		int aadCnt=0;
		double[] observed;
		double[] adjustment;
		List<Double> marginalRows=new ArrayList<Double>();
		List<Double> marginalCols=new ArrayList<Double>();
		int Iter=0;
		Logger logger=IPFMain.logger;
		
		//Initialize variables
		observed=new double[md.size()];
		adjustment=new double[md.size()];
		for(int m=0;m<md.size();m++){
			observed[m]=0.0;
			adjustment[m]=0.0;
			marginalRows.add((double) md.get(m).Boardings);
			marginalCols.add((double) md.get(m).Alightings);
		}
		
		/*
		 * Debugging
		 */
		logger.debug("writing debugging objects");
		
		try {
			DebugDropper.WriteOut(md, "S:\\User\\Rohne\\Projects\\Transit OB Survey\\Weighting\\md.csv");
			DebugDropper.WriteOut(sd, "S:\\User\\Rohne\\Projects\\Transit OB Survey\\Weighting\\sd.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		 * End Debugging
		 */
		
		
		//Rows (Boardings) first
		for(int m=0;m<md.size();m++){
			for(int i=0;i<sd.size();i++){
				if(sd.get(i).BoardLocation==md.get(m).StopID){
					observed[m]+=sd.get(i).SeedValue;
				}
			}
		}
		
		for(int m=0;m<md.size();m++){
			if(observed[m]>0)
				adjustment[m]=marginalRows.get(m)/observed[m];
			else
				adjustment[m]=0.01;
			if(Math.abs(marginalRows.get(m)-observed[m])>0){
				aad+=Math.abs(marginalRows.get(m)-observed[m]);
				aadCnt++;
			}
		}
		aad/=aadCnt;
		
		//Columns (alightings)
		for(int m=0;m<md.size();m++){
			for(int i=0;i<sd.size();i++){
				if(sd.get(i).BoardLocation==md.get(m).StopID){
					SeedData nsd=sd.get(i);
					nsd.WeightValue=sd.get(i).SeedValue*adjustment[m];
					sd.remove(i);
					sd.add(i, nsd);
				}
			}
		}
		
		//Loop
		while(aad>0.1 && Iter<10){
			Iter++;
			for(int m=0;m<observed.length;m++)
				observed[m]=0;
			
			for(int m=0;m<md.size();m++){
				for(int i=0;i<sd.size();i++){
					if(sd.get(i).AlightLocation==md.get(m).StopID){
						observed[m]+=sd.get(i).WeightValue;
					}
				}
			}
			aad=0;
			aadCnt=0;
			for(int m=0;m<md.size();m++){
				if(observed[m]>0)
					adjustment[m]=marginalCols.get(m)/observed[m];
				else
					adjustment[m]=0.01;
				if(Math.abs(marginalCols.get(m)-observed[m])>0){ 
					aad+=Math.abs(marginalCols.get(m)-observed[m]);
					aadCnt++;
				}
			}
			aad/=aadCnt;
			//Good to here
			
			//Do seed weights
			for(int m=0;m<md.size();m++){
				for(int i=0;i<sd.size();i++){
					if(sd.get(i).AlightLocation==md.get(m).StopID){ //alight
						SeedData nsd=sd.get(i);
						nsd.WeightValue=sd.get(i).WeightValue*adjustment[m];
						sd.remove(i);
						sd.add(i,nsd);
						//sd.get(i).WeightValue*=adjustment[m]; // Check what is going on here.
					}
				}
			}
			
			//Do row weights
			for(int m=0;m<observed.length;m++)
				observed[m]=0;
			
			for(int m=0;m<md.size();m++){
				for(int i=0;i<sd.size();i++){
					if(sd.get(i).BoardLocation==md.get(m).StopID){ //alight
						observed[m]+=sd.get(i).WeightValue;
					}
				}
			}
			aad=0;
			aadCnt=0;
			for(int m=0;m<md.size();m++){
				if(observed[m]>0)
					adjustment[m]=marginalRows.get(m)/observed[m];
				else
					adjustment[m]=0.01;
				if(Math.abs(marginalRows.get(m)-observed[m])>0){
					aad+=Math.abs(marginalRows.get(m)-observed[m]);
					aadCnt++;
				}
			}
			aad/=aadCnt;
			//Do seed weights
			for(int m=0;m<md.size();m++){
				for(int i=0;i<sd.size();i++){
					if(sd.get(i).BoardLocation==md.get(m).StopID){ //alight
						SeedData nsd=sd.get(i);
						nsd.WeightValue=sd.get(i).WeightValue*adjustment[m];
						sd.remove(i);
						sd.add(i,nsd);
					}
				}
			}
		}
		return sd;
	}	
}
