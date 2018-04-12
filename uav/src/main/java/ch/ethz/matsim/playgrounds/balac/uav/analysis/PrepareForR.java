package ch.ethz.matsim.playgrounds.balac.uav.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import org.matsim.core.utils.io.IOUtils;

public class PrepareForR {

	public static void main(String[] args) throws IOException {

		String[] speed = {"20.0","25.0","30.0"};
		String[] liftoff = {"60.0","120.0","180.0"};
		String[] waiting = {"120.0","300.0","600.0"};
		BufferedWriter writer = IOUtils.getBufferedWriter(args[1]);
		writer.write("distance,tt,rentals,speed,x,gr");
		writer.newLine();
		for (String s1 : speed) {
			int x = 1;
			int y = 1;
			for (String s2 : liftoff) {
				
				for (String s3 : waiting) {
					String filePath = args[0] +"/" + s1 + "_" + s2 +"_" + s3 +"_0.5price.csv";
					BufferedReader reader = IOUtils.getBufferedReader(filePath);

					reader.readLine();
					
					double distance = 0.0;
					int rentals = 0;
					double tt = 0.0;
					double turnover = 0.0;
					String s = reader.readLine();
					while (s != null) {
						
						String[] arr = s.split(",");
						if (Double.parseDouble(arr[1]) > 0.0) {
						distance += Double.parseDouble(arr[1]);
						rentals++;
						tt += Double.parseDouble(arr[2]);
						}
						s = reader.readLine();
						
					}
					
					writer.write(distance + "," + tt + "," + rentals + "," + s1 + "," + x + "," + y);
					writer.newLine();
					x++;
				}
				y++;
			}
		}
		
		writer.flush();
		writer.close();
		
		
		
	}

}
