package ch.ethz.matsim.playgrounds.balac.uav.run;

public class UAVModeChoiceParameters {
	final private double betaDistance;
	final private double betaTravelTime;
	final private double betaWaiting;
	final private double constant;
	
	final private boolean isChainBased;
	
	public UAVModeChoiceParameters(double constant, double betaDistance, double betaTravelTime, double betaWaiting, boolean isChainBased) {
		this.betaDistance = betaDistance;
		this.betaTravelTime = betaTravelTime;
		this.betaWaiting = betaWaiting;
		this.constant = constant;
		this.isChainBased = isChainBased;
	}

	public double getBetaDistance() {
		return betaDistance;
	}

	public double getBetaTravelTime() {
		return betaTravelTime;
	}
	
	public double getBetaWaiting() {
		return betaWaiting;
	}

	public double getConstant() {
		return constant;
	}
	
	public boolean isChainBased() {
		return isChainBased;
	}
}
