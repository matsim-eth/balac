package ch.ethz.matsim.playgrounds.balac.uav.run;

import org.matsim.utils.objectattributes.ObjectAttributes;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.mnl.ModeChoiceAlternative;
import ch.ethz.matsim.mode_choice.mnl.prediction.EmptyPredictionCache;
import ch.ethz.matsim.mode_choice.mnl.prediction.PredictionCache;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPrediction;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPredictor;

public class SeasonTicketPublicTransitModeChoiceAlternative implements ModeChoiceAlternative {
	final private SeasonTicketPublicTransitModeChoiceParameters params;
	final private TripPredictor tripPredictor;
	final private PredictionCache cache;
	final private ObjectAttributes personAttributes;

	public SeasonTicketPublicTransitModeChoiceAlternative(SeasonTicketPublicTransitModeChoiceParameters params, TripPredictor tripPredictor,
			PredictionCache cache, ObjectAttributes personAttributes) {
		this.params = params;
		this.tripPredictor = tripPredictor;
		this.cache = cache;
		this.personAttributes = personAttributes;
	}

	public SeasonTicketPublicTransitModeChoiceAlternative(SeasonTicketPublicTransitModeChoiceParameters params, TripPredictor tripPredictor,
			ObjectAttributes personAttributes) {
		this(params, tripPredictor, new EmptyPredictionCache(), personAttributes);
	}

	@Override
	public double estimateUtility(ModeChoiceTrip trip) {
		TripPrediction prediction = cache.get(trip);

		if (prediction == null) {
			prediction = tripPredictor.predictTrip(trip);
			cache.put(trip, prediction);
		}

		if (personAttributes.getAttribute(trip.getPerson().getId().toString(), "season_ticket") == null)
			return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelTime()
					+ params.getBetaDistance() * prediction.getPredictedTravelDistance();
		else {
			if (prediction.getPredictedTravelDistance() < 5000.0) {
				if (((String)personAttributes.getAttribute(trip.getPerson().getId().toString(), "season_ticket")).contains("Verbund"))		
					return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelTime()
							+ params.getBetaDistanceGA() * prediction.getPredictedTravelDistance();
				if (((String)personAttributes.getAttribute(trip.getPerson().getId().toString(), "season_ticket")).contains("Generalabo"))		
					return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelTime()
							+ params.getBetaDistanceGA() * prediction.getPredictedTravelDistance();
				
				return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelTime()
						+ params.getBetaDistanceHT() * prediction.getPredictedTravelDistance();
			}
			else {
				if (((String)personAttributes.getAttribute(trip.getPerson().getId().toString(), "season_ticket")).contains("Generalabo"))		
					return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelTime()
							+ params.getBetaDistanceGA() * prediction.getPredictedTravelDistance();
				
				if (((String)personAttributes.getAttribute(trip.getPerson().getId().toString(), "season_ticket")).contains("Halbtax"))			
					return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelTime()
							+ params.getBetaDistanceHT() * prediction.getPredictedTravelDistance();
				
				return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelTime()
						+ params.getBetaDistance() * prediction.getPredictedTravelDistance();
			}
		}
		
	
	}

	@Override
	public boolean isChainMode() {
		return params.isChainBased();
	}
}
