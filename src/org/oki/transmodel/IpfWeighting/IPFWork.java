package org.oki.transmodel.IpfWeighting;

import java.util.ArrayList;
import java.util.List;

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
		
		
		//Initialize variables
		observed=new double[md.size()];
		adjustment=new double[md.size()];
		for(int m=0;m<md.size();m++){
			observed[m]=0.0;
			adjustment[m]=0.0;
			marginalRows.add((double) md.get(m).Boardings);
			marginalCols.add((double) md.get(m).Alightings);
		}
		
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
				adjustment[m]=0;
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
				adjustment[m]=0;
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
				adjustment[m]=0;
			if(Math.abs(marginalRows.get(m)-observed[m])>0){
				aad+=Math.abs(marginalRows.get(m)-observed[m]);
				aadCnt++;
			}
		}
		aad/=aadCnt;
		//TODO: Loop!  This works!
		
		int a=1;		
		System.out.println(a);
		
		
		
		
		
		return null;
	}
	
	/*
	 * Sub:
	 * input List of SeedData
	 * input MarginalData
	 * output SeedData object with weight values filled
	 */
	
	
}
