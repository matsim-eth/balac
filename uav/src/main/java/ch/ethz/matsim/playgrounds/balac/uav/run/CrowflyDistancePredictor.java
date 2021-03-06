package ch.ethz.matsim.playgrounds.balac.uav.run;

import org.matsim.core.utils.geometry.CoordUtils;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.mnl.prediction.DistancePredictor;

public class CrowflyDistancePredictor implements DistancePredictor {
	final private double factor;
	
	public CrowflyDistancePredictor(double factor) {
		this.factor = factor;
	}
	
	public CrowflyDistancePredictor() {
		this(1.0);
	}
	
	@Override
	public double predictDistance(ModeChoiceTrip trip) {
		return factor *  CoordUtils.calcEuclideanDistance(trip.getOriginLink().getCoord(), trip.getDestinationLink().getCoord());
	}
}
