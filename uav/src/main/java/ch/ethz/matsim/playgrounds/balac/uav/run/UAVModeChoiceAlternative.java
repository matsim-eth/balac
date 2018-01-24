package ch.ethz.matsim.playgrounds.balac.uav.run;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.mnl.ModeChoiceAlternative;
import ch.ethz.matsim.mode_choice.mnl.prediction.EmptyPredictionCache;
import ch.ethz.matsim.mode_choice.mnl.prediction.PredictionCache;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPrediction;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPredictor;

public class UAVModeChoiceAlternative implements ModeChoiceAlternative {
	final private UAVModeChoiceParameters params;
	final private TripPredictor tripPredictor;
	final private PredictionCache cache;
	final private double waitingTime;

	public UAVModeChoiceAlternative(UAVModeChoiceParameters params, TripPredictor tripPredictor,
			PredictionCache cache, double waitingTime) {
		this.params = params;
		this.tripPredictor = tripPredictor;
		this.cache = cache;
		this.waitingTime = waitingTime;
	}

	public UAVModeChoiceAlternative(UAVModeChoiceParameters params, TripPredictor tripPredictor, double waitingTime) {
		this(params, tripPredictor, new EmptyPredictionCache(), waitingTime);
	}

	@Override
	public double estimateUtility(ModeChoiceTrip trip) {
		TripPrediction prediction = cache.get(trip);

		if (prediction == null) {
			prediction = tripPredictor.predictTrip(trip);
			cache.put(trip, prediction);
		}

		return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelTime()
				+ params.getBetaDistance() * prediction.getPredictedTravelDistance() + params.getBetaWaiting() * waitingTime;
	}

	@Override
	public boolean isChainMode() {
		return params.isChainBased();
	}
}
