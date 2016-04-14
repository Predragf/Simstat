package Simstat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipException;

import org.conqat.lib.commons.logging.*;
import org.conqat.lib.simulink.builder.*;
import org.conqat.lib.simulink.model.*;

public class Sim_stat {
	
	public List<Simulink_blocks_stat> sb_stat_list = new ArrayList<Simulink_blocks_stat>();
    public  Simulink_blocks_stat sb_stat = new Simulink_blocks_stat();
    public Integer model_depth = 0;
    public int depth = 0;
    
	  public int block_type_exist(String block_type){
		  int block_index = -1; int index = 0;
	      for (Iterator<Simulink_blocks_stat> iterator = sb_stat_list.iterator(); iterator.hasNext();) {
				Simulink_blocks_stat simulink_blocks_stat = (Simulink_blocks_stat) iterator.next();
				//System.out.println(block_type + simulink_blocks_stat.getBlock_type());
				
				if (block_type.equalsIgnoreCase(simulink_blocks_stat.getBlock_type())){
					return index;
				}
				++index;
			}
	      
	      return block_index;
	  }
  
  public void parse_smodel(SimulinkBlock model){
	 
	  ++depth;
	  
	  int block_index = -1;
	  
      for (SimulinkBlock block : model.getSubBlocks()) {
        String block_type = block.getType();
        
        if (block_type == "SubSystem")	
        	parse_smodel(block);
        
        //check if block type already exists (-1: doesn't exist)
        block_index = block_type_exist(block_type);
        
     	//block type exists
     	if (block_index != -1){	//System.out.println("match:" + sb_stat_list.get(block_index).getBlock_type());
     		++sb_stat_list.get(block_index).block_quantity;
     	
     	}else// new block type
     	{
     		Simulink_blocks_stat sb_stat = new Simulink_blocks_stat();
     		sb_stat.block_type = block_type;
     		sb_stat.block_quantity = 1;
     		sb_stat_list.add(sb_stat);
     	}
     	
      }
      if (depth > model_depth)
    	  model_depth = depth;
      
      --depth;

  }
  
  public static void main(String[] args)
      throws SimulinkModelBuildingException, ZipException, IOException {
		
  	if (args.length != 1){
  		System.out.println("usage: #simstat [path to simulink directory]");
  		System.exit(0);
  	}
	  	
	  Sim_stat sd = new Sim_stat();
	  String format = "%-20s %s%n";
		 
	  File folder = new File(args[0]);
	  File[] listOfFiles = folder.listFiles();
      Boolean per_smodel = false;
      
	  for (File file : listOfFiles) {
	      
		  if (file.isFile()) {
	        //  System.out.println(file.getName() + file.getAbsolutePath());

		    try (SimulinkModelBuilder builder = new SimulinkModelBuilder(file,
		        new SimpleLogger())) {
		      SimulinkModel model = builder.buildModel();

		     // System.out.println("-------------------------------------------");
		      sd.parse_smodel(model);
		      System.out.println( "model: " + model.getName() + "depth: " + sd.model_depth);

			  if (per_smodel){
		
				  for (Iterator<Simulink_blocks_stat> iterator = sd.sb_stat_list.iterator(); iterator.hasNext();) {
					Simulink_blocks_stat simulink_blocks_stat = (Simulink_blocks_stat) iterator.next();
					System.out.printf(format, simulink_blocks_stat.block_type, simulink_blocks_stat.block_quantity);
			      }
				  
				  //reset previous statistics
				  sd.sb_stat_list.clear();
				  sd.model_depth = 0;
				  sd.depth = 0;
			  }
		  
		    }
		    
		  }
	    }//if-file
	  
  	  if (!per_smodel){

  		  System.out.println("-------------------------------------------");
	      System.out.printf(format, "Block type", "Quantity");
		  System.out.println();
		  
		  for (Iterator<Simulink_blocks_stat> iterator = sd.sb_stat_list.iterator(); iterator.hasNext();) {
			Simulink_blocks_stat simulink_blocks_stat = (Simulink_blocks_stat) iterator.next();
			System.out.printf(format, simulink_blocks_stat.block_type, simulink_blocks_stat.block_quantity);
	      }
	  
		  //reset previous statistics
		  sd.sb_stat_list.clear();
		  sd.model_depth = 0;
		  sd.depth = 0;
	 }//for all files
  }//main
}
