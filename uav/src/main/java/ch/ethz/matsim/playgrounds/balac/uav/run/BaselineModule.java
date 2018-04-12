package ch.ethz.matsim.playgrounds.balac.uav.run;
import java.util.Arrays;
import java.util.HashSet;

import org.matsim.api.core.v01.population.Population;
import org.matsim.core.controler.AbstractModule;
import org.matsim.pt.PtConstants;

import com.google.inject.Provides;
import com.google.inject.Singleton;

import ch.ethz.matsim.baseline_scenario.scoring.ActivityScoringBuilder;
import ch.ethz.matsim.baseline_scenario.scoring.ActivityScoringByPersonAttributeBuilder;
import ch.ethz.matsim.baseline_scenario.scoring.BaselineScoringFunctionFactory;

public class BaselineModule extends AbstractModule {
	@Override
	public void install() {
		bindScoringFunctionFactory().to(BaselineScoringFunctionFactory.class).asEagerSingleton();
		bind(ActivityScoringBuilder.class).to(ActivityScoringByPersonAttributeBuilder.class);
	}

	@Provides
	@Singleton
	public ActivityScoringByPersonAttributeBuilder provideActivityScoringByPersonAttributeBuilder(
			Population population) {
		return new ActivityScoringByPersonAttributeBuilder(population.getPersonAttributes(),
				new HashSet<String>(Arrays.asList(PtConstants.TRANSIT_ACTIVITY_TYPE, "uav_interaction")));
	}
}