package org.oki.transmodel.IpfWeighting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class DebugDropper {
	public static void WriteOut(List<?> o, String f) throws IOException{
		File file=new File(f);
		FileOutputStream fos=new FileOutputStream(file);
		OutputStreamWriter out=new OutputStreamWriter(fos,"UTF-8");
		boolean headerWritten=false;
		
		for(Object j:o){
			if(j instanceof SeedData){
				if(!headerWritten){
					out.write("BoardLocation,AlightLocation,SeedValue,WeightValue,Sequence\n");
					headerWritten=true;
				}
				out.write(((SeedData) j).BoardLocation+","+((SeedData) j).AlightLocation+","+((SeedData) j).SeedValue+","+((SeedData) j).WeightValue+","+((SeedData) j).Sequence+"\n");
			}else if(j instanceof MarginalData){
				if(!headerWritten){
					out.write("StopID,Boardings,Alightings\n");
					headerWritten=true;
				}
				out.write(((MarginalData) j).StopID+","+((MarginalData) j).Boardings+","+((MarginalData) j).Alightings+"\n");
			}
			
		}
		out.close();
		
		

		
	}
}
