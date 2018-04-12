package ch.ethz.matsim.playgrounds.balac.uav.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.io.NetworkReaderMatsimV1;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.io.IOUtils;

public class TotalDistanceVehicle {

	public static void main(String[] args) throws IOException {

		BufferedReader reader = IOUtils.getBufferedReader(args[0]);
		
		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);
				
		NetworkReaderMatsimV1 networkReader = new NetworkReaderMatsimV1(scenario.getNetwork());
		
		networkReader.readFile(args[2]);
		BufferedWriter writer = IOUtils.getBufferedWriter(args[3]);
		writer.write("distance,distance_f\n");
		EventsManager eventsManager = EventsUtils.createEventsManager();
		
		DepArrTimeHandler handler = new DepArrTimeHandler(scenario.getNetwork());
		
		eventsManager.addHandler(handler);
		
		MatsimEventsReader eventsReader = new MatsimEventsReader(eventsManager);
		eventsReader.readFile(args[1]);
		
		Map<Integer, Double> depTimes = handler.getDepTimesOut();
		Map<Integer, Double> travelTimes = handler.getTravelTimesOut();
		
		Map<Integer, Coord> depLocOut = handler.getDepLocOut();
		Map<Integer, Coord> arrLocOut = handler.getArrLocOut();		
		
		double speed = 25;
		double liftofftime = 60;
		
		String s = reader.readLine();
		int start = -1;
		int end = -1;
		while (s != null) {
			String ss = s.substring(0, s.length() - 1);
			
			String[] arr = ss.split(",");
			double dist = 0.0;
			double distF = 0.0;

			for (int i = 1; i < arr.length; i++) {
				end = Integer.parseInt(arr[i]);
				if (start != -1) { 
					dist += CoordUtils.calcEuclideanDistance(depLocOut.get(end), arrLocOut.get(start));
					dist += travelTimes.get(end) * speed;
					distF += travelTimes.get(end) * speed;
				}
				else {
					dist += travelTimes.get(end) * speed;
					distF += travelTimes.get(end) * speed;

				}
				start = end;
			}		
			writer.write(Double.toString(dist/1000.0) + "," + Double.toString(distF/1000) + "\n");
			s = reader.readLine();
						
		}	
		writer.flush();
		writer.close();
	}

}
