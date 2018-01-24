package ch.ethz.matsim.playgrounds.balac.uav.router;

/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */


import java.util.List;

import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.router.MainModeIdentifier;

/**
 * @author thibautd
 */
public class UAVMainModeIdentifier implements MainModeIdentifier {
	private final MainModeIdentifier defaultModeIdentifier;

	public UAVMainModeIdentifier(final MainModeIdentifier defaultModeIdentifier) {
		this.defaultModeIdentifier = defaultModeIdentifier;
	}

	@Override
	public String identifyMainMode(List<? extends PlanElement> tripElements) {
		for ( PlanElement pe : tripElements ) {
			if ( pe instanceof Leg && (((Leg) pe).getMode().equals( UAVRoutingModule.TELEPORTATION_LEG_MODE ) 
					|| ((Leg)pe).getMode().equals(UAVRoutingModule.WAITING_MODE)))  {
				return UAVRoutingModule.TELEPORTATION_LEG_MODE;
			}
		}
		// if the trip doesn't contain a teleportation leg,
		// fall back to the default identification method.
		return defaultModeIdentifier.identifyMainMode( tripElements );
	}
}