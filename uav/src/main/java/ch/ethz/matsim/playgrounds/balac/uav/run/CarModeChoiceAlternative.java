package ch.ethz.matsim.playgrounds.balac.uav.run;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.mnl.BasicModeChoiceParameters;
import ch.ethz.matsim.mode_choice.mnl.ModeChoiceAlternative;
import ch.ethz.matsim.mode_choice.mnl.prediction.EmptyPredictionCache;
import ch.ethz.matsim.mode_choice.mnl.prediction.PredictionCache;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPrediction;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPredictor;

public class CarModeChoiceAlternative implements ModeChoiceAlternative {
	final private BasicModeChoiceParameters params;
	final private TripPredictor tripPredictor;
	final private PredictionCache cache;

	public CarModeChoiceAlternative(BasicModeChoiceParameters params, TripPredictor tripPredictor,
			PredictionCache cache) {
		this.params = params;
		this.tripPredictor = tripPredictor;
		this.cache = cache;
	}

	public CarModeChoiceAlternative(BasicModeChoiceParameters params, TripPredictor tripPredictor) {
		this(params, tripPredictor, new EmptyPredictionCache());
	}

	@Override
	public double estimateUtility(ModeChoiceTrip trip) {
		TripPrediction prediction = cache.get(trip);

		if (prediction == null) {
			prediction = tripPredictor.predictTrip(trip);
			cache.put(trip, prediction);
		}

		double cost = prediction.getPredictedTravelDistance() /1000.0 * 0.176 < 1.0 ? 1.0 : prediction.getPredictedTravelDistance() / 1000.0 * 0.176;
		
		return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelTime()
				- cost * 0.1045;
	}

	@Override
	public boolean isChainMode() {
		return params.isChainBased();
	}
}
