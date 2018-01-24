package ch.ethz.matsim.playgrounds.balac.uav.run;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.mnl.prediction.DefaultTripPrediction;
import ch.ethz.matsim.mode_choice.mnl.prediction.DistancePredictor;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPrediction;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPredictor;

public class UAVPredictor implements TripPredictor {
	final private double speed;
	final private double liftofftime;
	final private DistancePredictor distancePredictor;
	public UAVPredictor(double speed, double liftofftime, 
			CrowflyDistancePredictor crowflyDistancePredictor) {
		this.speed = speed;
		this.liftofftime = liftofftime;
		this.distancePredictor = crowflyDistancePredictor;
	}

	@Override
	public TripPrediction predictTrip(ModeChoiceTrip trip) {
		
		double travelDistance = distancePredictor.predictDistance(trip);
		double travelTime = travelDistance / this.speed + 2 * this.liftofftime;
		
		return new DefaultTripPrediction(travelTime, travelDistance);
	}

}
