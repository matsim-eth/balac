package ch.ethz.matsim.playgrounds.balac.uav.config;

import org.matsim.core.config.ReflectiveConfigGroup;

public class UAVConfigGroup extends ReflectiveConfigGroup {
	public static final String GROUP_NAME = "uav";

	private double cruisingSpeed; //in m/s
	private double waitingTime; //in seconds
	private double liftoffTime; //in seconds
	public UAVConfigGroup() {
		super(GROUP_NAME);		
	}
	
	@StringGetter( "cruisingSpeed" )
	public double getCruisingSpeed() {
		return this.cruisingSpeed;
	}

	@StringSetter( "cruisingSpeed" )
	public void setCruisingSpeed(double cruisingSpeed) {		
		this.cruisingSpeed = cruisingSpeed;
	}
	
	@StringGetter( "waitingTime" )
	public double getWaitingTime() {
		return this.waitingTime;
	}

	@StringSetter( "waitingTime" )
	public void setWaitingTime(double waitingTime) {		
		this.waitingTime = waitingTime;
	}
	
	@StringGetter( "liftoffTime" )
	public double getLiftoffTime() {
		return this.liftoffTime;
	}

	@StringSetter( "liftoffTime" )
	public void setLiftoffTime(double liftoffTime) {		
		this.liftoffTime = liftoffTime;
	}

}
