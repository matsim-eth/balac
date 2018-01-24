package ch.ethz.matsim.playgrounds.balac.uav.router;

import java.util.ArrayList;
import java.util.List;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.api.core.v01.population.Route;
import org.matsim.core.population.routes.RouteFactories;
import org.matsim.core.router.CompositeStageActivityTypes;
import org.matsim.core.router.RoutingModule;
import org.matsim.core.router.StageActivityTypes;
import org.matsim.core.router.StageActivityTypesImpl;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.facilities.Facility;

import ch.ethz.matsim.playgrounds.balac.uav.config.UAVConfigGroup;



public class UAVRoutingModule implements RoutingModule{
	public static final String TELEPORTATION_LEG_MODE = "uav";
	public static final String WAITING_MODE = "waiting_uav";
	public static final String UAV_INTERACTION = "uav_interaction";
	private final Scenario scenario;

	public UAVRoutingModule(final Scenario scenario) {
		
		this.scenario = scenario;
	}
	
	
	@Override
	public List<? extends PlanElement> calcRoute(Facility<?> fromFacility, Facility<?> toFacility, double departureTime,
			Person person) {
		final List<PlanElement> trip = new ArrayList<PlanElement>();

		UAVConfigGroup uavConfigGroup = (UAVConfigGroup) scenario.getConfig().getModules().get("uav");
		
		double cruisingSpeed = uavConfigGroup.getCruisingSpeed();
		double waitingTime = uavConfigGroup.getWaitingTime();
		double liftoffTime = uavConfigGroup.getLiftoffTime();
				
		PopulationFactory populationFactory = scenario.getPopulation().getFactory();
		RouteFactories routeFactory = ((PopulationFactory)populationFactory).getRouteFactories();
		
		Route accessWalkRoute = routeFactory.createRoute(Route.class, fromFacility.getLinkId(), fromFacility.getLinkId());
		
		accessWalkRoute.setDistance(0.0);
		accessWalkRoute.setTravelTime(waitingTime);
		
		final Leg uavWaitingLeg = populationFactory.createLeg( WAITING_MODE );
		uavWaitingLeg.setRoute(accessWalkRoute);
			trip.add( uavWaitingLeg );
			
		Activity uav_interaction = populationFactory.createActivityFromLinkId(UAV_INTERACTION, fromFacility.getLinkId());	
		uav_interaction.setMaximumDuration(60.0);
		trip.add(uav_interaction);
		
		Route routeUAV = routeFactory.createRoute( Route.class, fromFacility.getLinkId(), toFacility.getLinkId() ); 
		
		double egressDist = CoordUtils.calcEuclideanDistance(fromFacility.getCoord(), toFacility.getCoord());
		egressDist = egressDist > 0 ? egressDist : 1; 
		routeUAV.setTravelTime( 2.0 * liftoffTime  + (egressDist / cruisingSpeed));
		routeUAV.setDistance(egressDist);	

		final Leg uavLeg = populationFactory.createLeg( TELEPORTATION_LEG_MODE );
		uavLeg.setRoute(routeUAV);
			trip.add( uavLeg );
		
		return trip;
	}

	@Override
	public StageActivityTypes getStageActivityTypes() {
		final CompositeStageActivityTypes stageTypes = new CompositeStageActivityTypes();
		stageTypes.addActivityTypes( new StageActivityTypesImpl( "uav_interaction" ) );
		return stageTypes;
	}

}
