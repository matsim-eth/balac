package ch.ethz.matsim.playgrounds.balac.uav.run;

import org.matsim.utils.objectattributes.ObjectAttributes;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.mnl.ModeChoiceAlternative;
import ch.ethz.matsim.mode_choice.mnl.prediction.EmptyPredictionCache;
import ch.ethz.matsim.mode_choice.mnl.prediction.PredictionCache;
import ch.ethz.matsim.mode_choice.mnl.prediction.PublicTransitTripPrediction;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPrediction;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPredictor;

public class AdvancedSeasonTicketPublicTransitModeChoiceAlternative implements ModeChoiceAlternative {
	final private AdvancedSeasonTicketPublicTransitModeChoiceParameters params;
	final private TripPredictor tripPredictor;
	final private PredictionCache cache;
	final private ObjectAttributes personAttributes;

	public AdvancedSeasonTicketPublicTransitModeChoiceAlternative(AdvancedSeasonTicketPublicTransitModeChoiceParameters params, TripPredictor tripPredictor,
			PredictionCache cache, ObjectAttributes personAttributes) {
		this.params = params;
		this.tripPredictor = tripPredictor;
		this.cache = cache;
		this.personAttributes = personAttributes;
	}

	public AdvancedSeasonTicketPublicTransitModeChoiceAlternative(AdvancedSeasonTicketPublicTransitModeChoiceParameters params, TripPredictor tripPredictor,
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
		
		if (((PublicTransitTripPrediction)prediction).isOnlyTransitWalk())
			return -100;

		if (personAttributes.getAttribute(trip.getPerson().getId().toString(), "season_ticket") == null) {
		
			double cost = 0.36 * prediction.getPredictedTravelDistance() / 1000.0 < 2.6 ? 2.6 : 0.36 * prediction.getPredictedTravelDistance() / 1000.0;
			
			
			return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelTime()
					+ (-0.1045) * cost + params.getBetaTransfers() * ((PublicTransitTripPrediction)prediction).getNumberOfLineSwitches() +
					params.getBetaAccessTime() * (((PublicTransitTripPrediction)prediction).getTransferTime());
		}
		else {
			if (prediction.getPredictedTravelDistance() < 5000.0) {
				if (((String)personAttributes.getAttribute(trip.getPerson().getId().toString(), "season_ticket")).contains("Verbund"))		
					return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelTime()
							+ params.getBetaDistanceGA() * prediction.getPredictedTravelDistance() + params.getBetaTransfers() * ((PublicTransitTripPrediction)prediction).getNumberOfLineSwitches() +
							params.getBetaAccessTime() * (((PublicTransitTripPrediction)prediction).getTransferTime());
				if (((String)personAttributes.getAttribute(trip.getPerson().getId().toString(), "season_ticket")).contains("Generalabo"))		
					return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelTime()
							+ params.getBetaDistanceGA() * prediction.getPredictedTravelDistance() + params.getBetaTransfers() * ((PublicTransitTripPrediction)prediction).getNumberOfLineSwitches() +
							params.getBetaAccessTime() * (((PublicTransitTripPrediction)prediction).getTransferTime());
				
				return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelTime()
						+ params.getBetaDistanceHT() * prediction.getPredictedTravelDistance() + params.getBetaTransfers() * ((PublicTransitTripPrediction)prediction).getNumberOfLineSwitches() +
						params.getBetaAccessTime() * (((PublicTransitTripPrediction)prediction).getTransferTime());
			}
			else {
				if (((String)personAttributes.getAttribute(trip.getPerson().getId().toString(), "season_ticket")).contains("Generalabo"))		
					return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelTime()
							+ params.getBetaDistanceGA() * prediction.getPredictedTravelDistance() + params.getBetaTransfers() * ((PublicTransitTripPrediction)prediction).getNumberOfLineSwitches() +
							params.getBetaAccessTime() * (((PublicTransitTripPrediction)prediction).getTransferTime());
				
				if (((String)personAttributes.getAttribute(trip.getPerson().getId().toString(), "season_ticket")).contains("Halbtax"))			
					return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelTime()
							+ params.getBetaDistanceHT() * prediction.getPredictedTravelDistance() + params.getBetaTransfers() * ((PublicTransitTripPrediction)prediction).getNumberOfLineSwitches() +
							params.getBetaAccessTime() * (((PublicTransitTripPrediction)prediction).getTransferTime());
				
				return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelTime()
						+ params.getBetaDistance() * prediction.getPredictedTravelDistance() + params.getBetaTransfers() * ((PublicTransitTripPrediction)prediction).getNumberOfLineSwitches() +
						params.getBetaAccessTime() * (((PublicTransitTripPrediction)prediction).getTransferTime());
			}
		}
		
	
	}

	@Override
	public boolean isChainMode() {
		return params.isChainBased();
	}
}
