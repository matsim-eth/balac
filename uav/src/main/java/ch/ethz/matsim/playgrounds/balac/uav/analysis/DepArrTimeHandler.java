package ch.ethz.matsim.playgrounds.balac.uav.analysis;

import java.util.HashMap;
import java.util.Map;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.events.PersonArrivalEvent;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.events.handler.PersonArrivalEventHandler;
import org.matsim.api.core.v01.events.handler.PersonDepartureEventHandler;
import org.matsim.api.core.v01.network.Network;

public class DepArrTimeHandler implements PersonDepartureEventHandler, PersonArrivalEventHandler {

	private static int counter = 0;
	Map<String, Double> departureTimes = new HashMap<>();
	Map<String, Coord> depLocations = new HashMap<>();

	Map<Integer, Double> depTimesOut = new HashMap<>();
	Map<Integer, Double> travelTimesOut = new HashMap<>();
	
	Map<Integer, Coord> depLocOut = new HashMap<>();
	Map<Integer, Coord> arrLocOut = new HashMap<>();
	private Network network;
	
	public DepArrTimeHandler(Network network) {
		
		this.network = network;
	}
	
	@Override
	public void reset(int iteration) {
		
	}

	@Override
	public void handleEvent(PersonArrivalEvent event) {
		
		if (event.getLegMode().equals("uav")) {
			counter++;
			depTimesOut.put(counter, departureTimes.get(event.getPersonId().toString()));
			travelTimesOut.put(counter, event.getTime() - departureTimes.get(event.getPersonId().toString()));	
			
			depLocOut.put(counter, depLocations.get(event.getPersonId().toString()));
			arrLocOut.put(counter, network.getLinks().get(event.getLinkId()).getCoord());
			
		}
	}

	@Override
	public void handleEvent(PersonDepartureEvent event) {
		
		if (event.getLegMode().equals("uav")) {			
			departureTimes.put(event.getPersonId().toString(), event.getTime());
			depLocations.put(event.getPersonId().toString(), network.getLinks().get(event.getLinkId()).getCoord());
		}
	}

	public Map<Integer, Double> getDepTimesOut() {
		return depTimesOut;
	}

	public Map<Integer, Double> getTravelTimesOut() {
		return travelTimesOut;
	}

	public Map<Integer, Coord> getDepLocOut() {
		return depLocOut;
	}

	public Map<Integer, Coord> getArrLocOut() {
		return arrLocOut;
	}

}
