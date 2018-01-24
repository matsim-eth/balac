package ch.ethz.matsim.playgrounds.balac.uav.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.matsim.core.utils.io.IOUtils;

public class LPPrepare {

	public static void main(String[] args) throws IOException {

		int numberOfTrips = Integer.parseInt(args[1]);

		BufferedWriter writer = IOUtils.getBufferedWriter(args[2]);
		
		writer.write("Minimize\n");
		writer.write("c1e");
		for (int i = 2; i <= numberOfTrips; i++) {
			
			writer.write(" + c" + Integer.toString(i) + "e");
		}
		
		writer.write("\n");
		writer.write("Subject to\n");
		
		int j = 1;
		
		Map<Integer, Set<String>> inEdges = new HashMap<>();
		Map<Integer, Set<String>> outEdges = new HashMap<>();
		Set<String> allEdges = new HashSet<>();
		
		
		//read in all the edges
		
		BufferedReader reader = IOUtils.getBufferedReader(args[0]);
		String s = reader.readLine();
		Map<Integer, Set<Integer>> edges = new HashMap<>();

		
		while (s != null) {
			String[] arr = s.split(",");

			if (edges.containsKey(Integer.parseInt(arr[0]))){
				Set<Integer> ss =edges.get(Integer.parseInt(arr[0]));
				ss.add(Integer.parseInt(arr[1]));
			}
			else {
				
				Set<Integer> ss = new HashSet<>();
				ss.add(Integer.parseInt(arr[1]));
				edges.put(Integer.parseInt(arr[0]), ss);
			}
				
			s = reader.readLine();
		}
		
		
		
		
		for (int i = 1; i <= numberOfTrips; i++) {
			if (i % 100 == 0)
				System.out.println("Done " + Integer.toString(i));
			//allEdges.add("c" + Integer.toString(i));	
			Set<String> in;
			if (inEdges.containsKey(i))
				in = inEdges.get(i);
			else
				in = new HashSet<>();
			Set<String> out = new HashSet<>();
			allEdges.add("c" + Integer.toString(i) + "s");
			allEdges.add("c" + Integer.toString(i) + "e");

			in.add("c" + Integer.toString(i) + "s");
			inEdges.put(i, in);
			out.add("c" + Integer.toString(i) + "e");
			outEdges.put(i, out);
			if (edges.get(i) != null) {
				for (int k : edges.get(i)) {
					
					int start = i;
					int end = k;
					
					
						
					out.add("c" + Integer.toString(i) + "_" + Integer.toString(k));
					
					allEdges.add("c" + Integer.toString(i) + "_" + Integer.toString(k));
	
					Set<String> inN;
					if (inEdges.containsKey(end))
						inN = inEdges.get(end);
					else
						inN = new HashSet<>();
					inN.add("c" + Integer.toString(i) + "_" + Integer.toString(k));
					outEdges.put(start, out);
					inEdges.put(end, inN);		
	
				}
			}
		}
		
		
		
		for (int i = 1; i <= numberOfTrips; i++) {
			boolean start = true;
			for (String inEdge : inEdges.get(i)) {
				if (start) {
					writer.write(inEdge);
					start = false;
				}
				else
					writer.write(" + " + inEdge);
			}
			writer.write(" - c" + Integer.toString(i) + " = 0\n");
			writer.write("c" + Integer.toString(i));
			for (String outEdge : outEdges.get(i)){
				
				writer.write(" - " + outEdge);
			}
			
			writer.write(" = 0\n");
			
		}
		
		writer.write("Bounds\n");

		for (int i = 1; i <=numberOfTrips; i++)
			writer.write("1 <= c" + Integer.toString(i) + " <= 1\n");

		for (String s2 : allEdges) {
			
			writer.write("0 <= " + s2 + " <= 1\n");
		}
		writer.write("Integers\n");
		
		for (String s2 : allEdges) {
			writer.write(s2 + " ");
		}
		writer.write("\n");
		writer.write("End");
	}

}
