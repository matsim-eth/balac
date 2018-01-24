package ch.ethz.matsim.playgrounds.balac.uav.run;

import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;
import org.matsim.core.utils.geometry.CoordUtils;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.mnl.prediction.DefaultTripPrediction;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPrediction;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPredictor;

public class CarPredictor implements TripPredictor {
	final private LeastCostPathCalculator router;

	public CarPredictor(LeastCostPathCalculator router) {
		this.router = router;
	}

	@Override
	public TripPrediction predictTrip(ModeChoiceTrip trip) {
		Path path = router.calcLeastCostPath(trip.getOriginLink().getToNode(), trip.getDestinationLink().getFromNode(),
				trip.getDepartureTime(), trip.getPerson(), null);

		if (path == null) {
			throw new IllegalStateException();
		}

		double distance = CoordUtils.calcEuclideanDistance(trip.getOriginLink().getCoord(), trip.getDestinationLink().getCoord());
		return new DefaultTripPrediction(path.travelTime, distance);
	}
}
