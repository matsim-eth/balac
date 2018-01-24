package ch.ethz.matsim.playgrounds.balac.uav.run;

import java.util.List;

import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.router.LinkWrapperFacility;
import org.matsim.core.utils.geometry.CoordUtils;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.mnl.prediction.PublicTransitTripPrediction;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPrediction;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPredictor;
import ch.ethz.matsim.r5.R5TeleportationRoutingModule;

public class R5PTPredictor implements TripPredictor {
	final private R5TeleportationRoutingModule routingModule;

	public R5PTPredictor(R5TeleportationRoutingModule routingModule) {
		this.routingModule = routingModule;
	}

	@Override
	public TripPrediction predictTrip(ModeChoiceTrip trip) {
		List<? extends PlanElement> elements = routingModule.calcRoute(new LinkWrapperFacility(trip.getOriginLink()),
				new LinkWrapperFacility(trip.getDestinationLink()), trip.getDepartureTime(), trip.getPerson());
		
		boolean atLeastOnePtLeg = false;
		
		double travelTime = 0.0;
		double travelDistance = 0.0;
		double transferTime = 0.0;
		double transferDistance = 0.0;
		double waitingTime = 0.0;
		int numberOfLineSwitches = -2;
		
		for (PlanElement element : elements) {
			if (element instanceof Activity) {
				numberOfLineSwitches++;
				waitingTime += ((Activity) element).getMaximumDuration();
			} else {
				Leg leg = (Leg) element;
				
				if (leg.getMode().equals("pt")) {
					travelTime += leg.getRoute().getTravelTime();
					travelDistance += leg.getRoute().getDistance();
					atLeastOnePtLeg = true;
				} else {
					transferTime += leg.getRoute().getTravelTime();
					transferDistance += leg.getRoute().getDistance();
				}
			}
		}
		double distance = CoordUtils.calcEuclideanDistance(trip.getOriginLink().getCoord(), trip.getDestinationLink().getCoord());
		return new PublicTransitTripPrediction(travelTime, distance, transferTime, transferDistance, waitingTime,
				numberOfLineSwitches, !atLeastOnePtLeg);
	}
}