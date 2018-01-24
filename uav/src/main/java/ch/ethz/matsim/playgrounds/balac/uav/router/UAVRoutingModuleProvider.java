package ch.ethz.matsim.playgrounds.balac.uav.router;
import com.google.inject.Provider;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.router.RoutingModule;

public class UAVRoutingModuleProvider implements Provider<RoutingModule> {

	private Scenario scenario;

	public UAVRoutingModuleProvider(Scenario scenario) {
		this.scenario = scenario;
	}

	@Override
	public RoutingModule get() {
		return new UAVRoutingModule(scenario);
	}
}