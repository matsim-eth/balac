package ch.ethz.matsim.playgrounds.balac.uav.run;

public class SeasonTicketPublicTransitModeChoiceParameters {
	final private double betaDistance;
	final private double betaDistanceGA;
	final private double betaDistanceHT;

	final private double betaTravelTime;
	final private double constant;
	
	final private boolean isChainBased;
	
	public SeasonTicketPublicTransitModeChoiceParameters(double constant, double betaDistanceGA,
			double betaDistance, double betaDistanceHT, double betaTravelTime, boolean isChainBased) {
		this.betaDistance = betaDistance;
		this.betaDistanceGA = betaDistanceGA;
		this.betaDistanceHT = betaDistanceHT;
		this.betaTravelTime = betaTravelTime;
		this.constant = constant;
		this.isChainBased = isChainBased;
	}

	public double getBetaDistance() {
		return betaDistance;
	}
	
	public double getBetaDistanceGA() {
		return betaDistanceGA;
	}
	
	public double getBetaDistanceHT() {
		return betaDistanceGA;
	}

	public double getBetaTravelTime() {
		return betaTravelTime;
	}

	public double getConstant() {
		return constant;
	}
	
	public boolean isChainBased() {
		return isChainBased;
	}
}
