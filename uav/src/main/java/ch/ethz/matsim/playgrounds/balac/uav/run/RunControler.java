package ch.ethz.matsim.playgrounds.balac.uav.run;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.router.MainModeIdentifier;
import org.matsim.core.router.MainModeIdentifierImpl;
import org.matsim.core.router.costcalculators.OnlyTimeDependentTravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.ScenarioUtils;

import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import ch.ethz.matsim.baseline_scenario.analysis.simulation.ModeShareListenerModule;
import ch.ethz.matsim.mode_choice.ModeChoiceModel;
import ch.ethz.matsim.mode_choice.alternatives.ChainAlternatives;
import ch.ethz.matsim.mode_choice.alternatives.TripChainAlternatives;
import ch.ethz.matsim.mode_choice.mnl.BasicModeChoiceAlternative;
import ch.ethz.matsim.mode_choice.mnl.BasicModeChoiceParameters;
import ch.ethz.matsim.mode_choice.mnl.ModeChoiceMNL;
import ch.ethz.matsim.mode_choice.mnl.prediction.FixedSpeedPredictor;
import ch.ethz.matsim.mode_choice.mnl.prediction.HashPredictionCache;
import ch.ethz.matsim.mode_choice.mnl.prediction.PredictionCache;
import ch.ethz.matsim.mode_choice.mnl.prediction.PredictionCacheCleaner;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPredictor;
import ch.ethz.matsim.mode_choice.replanning.ModeChoiceStrategy;
import ch.ethz.matsim.mode_choice.run.MNLConfigGroup;
import ch.ethz.matsim.mode_choice.run.RemoveLongPlans;
import ch.ethz.matsim.mode_choice.run.MNLConfigGroup.MNLCarUtility;
import ch.ethz.matsim.mode_choice.selectors.OldPlanForRemovalSelector;
import ch.ethz.matsim.mode_choice.utils.QueueBasedThreadSafeDijkstra;
import ch.ethz.matsim.playgrounds.balac.uav.config.UAVConfigGroup;
import ch.ethz.matsim.playgrounds.balac.uav.router.UAVMainModeIdentifier;
import ch.ethz.matsim.playgrounds.balac.uav.router.UAVRoutingModule;
import ch.ethz.matsim.playgrounds.balac.uav.router.UAVRoutingModuleProvider;
import ch.ethz.matsim.r5.R5TeleportationRoutingModule;
import ch.ethz.matsim.r5.matsim.R5ConfigGroup;
import ch.ethz.matsim.r5.matsim.R5Module;



public class RunControler {

	public static void main(String[] args) {
		R5ConfigGroup r5Config = new R5ConfigGroup();
		
		MNLConfigGroup mnlConfig = new MNLConfigGroup();
		mnlConfig.setMode(ModeChoiceMNL.Mode.BEST_RESPONSE);
		mnlConfig.setCarUtility(MNLCarUtility.NETWORK);
		mnlConfig.setNumberOfThreads(80);
		
		Config config = ConfigUtils.loadConfig(args[0], mnlConfig, r5Config,  new UAVConfigGroup());
		config.strategy().setMaxAgentPlanMemorySize(1);

		double cruisingSpeed = Double.parseDouble(args[1]);
		double liftoffTime = Double.parseDouble(args[2]);
		double waitingTime = Double.parseDouble(args[3]);
	   
		config.controler().setOutputDirectory(config.controler().getOutputDirectory() + Double.toString(cruisingSpeed) + "_" +
		Double.toString(liftoffTime) + "_" + Double.toString(waitingTime) + "_0.5price_sens5");
		config.controler().setRunId(Double.toString(cruisingSpeed) + "_" +
				Double.toString(liftoffTime) + "_" + Double.toString(waitingTime) + "_0.5price_sens5");
		
		((UAVConfigGroup)config.getModules().get("uav")).setCruisingSpeed(cruisingSpeed);
		((UAVConfigGroup)config.getModules().get("uav")).setLiftoffTime(liftoffTime);
		((UAVConfigGroup)config.getModules().get("uav")).setWaitingTime(waitingTime);
		
		Scenario scenario = ScenarioUtils.loadScenario(config);
		Controler controler = new Controler(scenario);

		new RemoveLongPlans(10).run(scenario.getPopulation());

		// Set up MNL

		controler.addOverridingModule(new R5Module());
		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				//addControlerListenerBinding().to(PredictionCacheCleaner.class);
				addControlerListenerBinding().to(Key.get(PredictionCacheCleaner.class, Names.named("car")));
				addControlerListenerBinding().to(Key.get(PredictionCacheCleaner.class, Names.named("pt")));
			}
			
			@Singleton @Provides @Named("car")
			public PredictionCacheCleaner provideCarPredictionCacheCleaner(@Named("car") PredictionCache cache) {
				return new PredictionCacheCleaner(cache);
			}
			
			@Singleton @Provides @Named("pt")
			public PredictionCacheCleaner providePtPredictionCacheCleaner(@Named("pt") PredictionCache cache) {
				return new PredictionCacheCleaner(cache);
			}
			
			@Singleton @Provides @Named("car")
			public PredictionCache provideCarPredictionCache() {
				return new HashPredictionCache();
			}
			
			@Singleton @Provides @Named("pt")
			public PredictionCache providePtPredictionCache() {
				return new HashPredictionCache();
			}

			@Singleton
			@Provides
			public ModeChoiceModel provideModeChoiceModel(Network network, @Named("car") TravelTime travelTime,
					MNLConfigGroup mnlConfig, @Named("car") PredictionCache carCache, @Named("pt") PredictionCache ptCache, R5TeleportationRoutingModule routingModule) {
				ChainAlternatives chainAlternatives = new TripChainAlternatives(false);
				ModeChoiceMNL model = new ModeChoiceMNL(MatsimRandom.getRandom(), chainAlternatives,
						scenario.getNetwork(), mnlConfig.getMode());

			//	BasicModeChoiceParameters carParameters = new BasicModeChoiceParameters(0.0, -0.176 / 1000.0 * 0.1108,
			//			-0.0403 /60.0, true);
				//BasicModeChoiceParameters ptParameters = new BasicModeChoiceParameters(-0.7753, -0.25 / 1000.0 * 0.1108,
				//		-0.0365 / 60.0, false);
				//BasicModeChoiceParameters walkParameters = new BasicModeChoiceParameters(1.1684, 0.0, -0.07 / 60.0,
				//		false);				
				//BasicModeChoiceParameters bikeParameters = new BasicModeChoiceParameters(-0.9, 0.0, -0.0642 / 60.0,
			//	SeasonTicketPublicTransitModeChoiceParameters ptParameters = new
			//			SeasonTicketPublicTransitModeChoiceParameters(-0.7753, -0.1 / 1000.0 * 0.1108, -0.36 / 1000.0 * 0.1108, -0.18 /1000 * 0.1108,
			//			-0.0365 / 60.0, false);
				
				
				BasicModeChoiceParameters carParameters = new BasicModeChoiceParameters(0.0, -0.176 / 1000.0 * 0.1045,
						-0.0709 /60.0, true);
				AdvancedSeasonTicketPublicTransitModeChoiceParameters ptParameters = new
						AdvancedSeasonTicketPublicTransitModeChoiceParameters(-0.3471, -0.1 / 1000.0 * 0.1045, -0.36 / 1000.0 * 0.1045, -0.18 /1000 * 0.1045,
						-0.0347 / 60.0, -0.0, -0.02079 / 60.0, false);
				
				BasicModeChoiceParameters walkParameters = new BasicModeChoiceParameters(0.9, 0.0, -0.0670 / 60.0,
						false);				
				BasicModeChoiceParameters bikeParameters = new BasicModeChoiceParameters(-0.2, 0.0, -0.0617 / 60.0,
						true);
				
				UAVModeChoiceParameters uavParameters = new UAVModeChoiceParameters(-0.3471, -0.1045, -0.0393 / 60.0, 
						-0.02079 / 60.0, false);				
				
				TripPredictor carPredictor = null;

				switch (mnlConfig.getCarUtility()) {
				case NETWORK:
					carPredictor = new CarPredictor(
							new QueueBasedThreadSafeDijkstra(mnlConfig.getNumberOfThreads(), network,
									new OnlyTimeDependentTravelDisutility(travelTime), travelTime));
					break;
				case CROWFLY:
					carPredictor = new FixedSpeedPredictor(30.0 * 1000.0 / 3600.0, new CrowflyDistancePredictor());
					break;
				default:
					throw new IllegalStateException();
				}
				
				R5PTPredictor publicTransitPredictor =  
						new R5PTPredictor(routingModule);
				
				model.addModeAlternative("car", new CarModeChoiceAlternative(carParameters, carPredictor, carCache));

			//	model.addModeAlternative("pt", new SeasonTicketPublicTransitModeChoiceAlternative(ptParameters,
			//			publicTransitPredictor, cache, scenario.getPopulation().getPersonAttributes()));
				
				model.addModeAlternative("pt", new AdvancedSeasonTicketPublicTransitModeChoiceAlternative(ptParameters,
						publicTransitPredictor, ptCache, scenario.getPopulation().getPersonAttributes()));
				
				model.addModeAlternative("walk", new BasicModeChoiceAlternative(walkParameters,
						new FixedSpeedPredictor(1.3888888888888888, new CrowflyDistancePredictor(1.5))));
				model.addModeAlternative("bike", new BasicModeChoiceAlternative(bikeParameters,
						new FixedSpeedPredictor(3.611111111111111, new CrowflyDistancePredictor(1.3))));
				model.addModeAlternative("uav", new UAVModeChoiceAlternative(uavParameters,
						new UAVPredictor(cruisingSpeed, liftoffTime, new CrowflyDistancePredictor(1.0)), waitingTime));

				return model;
			}
		});
		
		
		//MNLModel.setUpModelWithRoutedPT(controler);

		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				this.bindPlanSelectorForRemoval().to(OldPlanForRemovalSelector.class);
				this.addPlanStrategyBinding("ModeChoiceStrategy").toProvider(ModeChoiceStrategy.class);
			}
		});

		controler.addOverridingModule(new BaselineModule());
		
		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				addRoutingModuleBinding(UAVRoutingModule.TELEPORTATION_LEG_MODE).toProvider(
						new UAVRoutingModuleProvider(
								// the module uses the trip router for the PT part.
								// This allows to automatically adapt to user settings,
								// including if they are specified at a later stage
								// in the initialisation process.
								
								scenario
								));
				// we still need to provide a way to identify our trips
				// as being teleportation trips.
				// This is for instance used at re-routing.
				bind(MainModeIdentifier.class).toInstance(new UAVMainModeIdentifier(new MainModeIdentifierImpl()));
			}
		});
		
		controler.addOverridingModule(new ModeShareListenerModule()); // Writes correct mode shares in every iteration

		controler.run();
	}

}
