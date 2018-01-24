package ch.ethz.matsim.playgrounds.balac.uav.analysis;

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

public class UAVTrips {

	public static void main(String[] args) throws IOException {
		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);
				
		NetworkReaderMatsimV1 networkReader = new NetworkReaderMatsimV1(scenario.getNetwork());
		
		networkReader.readFile(args[1]);
		BufferedWriter writer = IOUtils.getBufferedWriter(args[2]);
		BufferedWriter writer2 = IOUtils.getBufferedWriter(args[3]);

		EventsManager eventsManager = EventsUtils.createEventsManager();
		
		DepArrTimeHandler handler = new DepArrTimeHandler(scenario.getNetwork());
		
		eventsManager.addHandler(handler);
		
		MatsimEventsReader eventsReader = new MatsimEventsReader(eventsManager);
		eventsReader.readFile(args[0]);
		
		Map<Integer, Double> depTimes = handler.getDepTimesOut();
		Map<Integer, Double> travelTimes = handler.getTravelTimesOut();
		
		Map<Integer, Coord> depLocOut = handler.getDepLocOut();
		Map<Integer, Coord> arrLocOut = handler.getArrLocOut();		
		
		double speed = 30;
		double liftofftime = 60;
		
		for (Integer key : depTimes.keySet()) {
			
			double dist = CoordUtils.calcEuclideanDistance(depLocOut.get(key), arrLocOut.get(key));
			writer2.write(key + "," + dist + "\n");
			
			double arrivalTime = depTimes.get(key) + travelTimes.get(key);
			
			for (Integer key2 : depTimes.keySet()) {
				
				if (depTimes.get(key2) > arrivalTime) {
					
					double distance = CoordUtils.calcEuclideanDistance(depLocOut.get(key2), arrLocOut.get(key));
					
					double time = distance / speed + 2 *liftofftime;
					
					if (arrivalTime + time < depTimes.get(key2)) {
						writer.write(key + "," + key2 + "," + (int)distance/1000 + "\n");
					}
					
				}				
			}
		}
		
		writer.flush();
		writer.close();	
		
		writer2.flush();
		writer2.close();	
		
	}
}
