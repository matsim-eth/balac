package ch.ethz.matsim.playgrounds.balac.uav.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.matsim.core.utils.io.IOUtils;

public class DistanceVehicleExtraction {

	public static void main(String[] args) throws IOException {

		
		BufferedReader reader = IOUtils.getBufferedReader(args[0]);
		BufferedWriter writer = IOUtils.getBufferedWriter(args[1]);
		String s = reader.readLine();
		
		Map<String, String> mapo = new HashMap<>();
		
		Set<String> sources = new HashSet<>();
		
		while (s != null) {
			
			String[] arrS = s.split(" ");
			
			if (arrS[1].equals("1")) {
				
				
				String[] arrS2 = arrS[0].split("_");
				if (arrS2.length > 1) {
					
					mapo.put(arrS2[0].substring(1), arrS2[1]);
				}
				else if (arrS[0].contains("s")) {
					sources.add(arrS[0]);
					String temp = arrS2[0].substring(0, arrS2[0].length() - 1);
					mapo.put(arrS2[0], arrS2[0].substring(1, arrS2[0].length() - 1));

				}
				
			}
			s = reader.readLine();
		}
		
		for (String so : sources) {
			
			writer.write(so + ",");
			
			String nextEdge = mapo.get(so);
			while (nextEdge != null) {
				
				writer.write(nextEdge + "," );
				nextEdge = mapo.get(nextEdge);
			}
			
			writer.write("\n");;
		}
		
		writer.flush();
		writer.close();
		
		
	}

}
