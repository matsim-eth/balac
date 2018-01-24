package ch.ethz.matsim.playgrounds.balac.uav.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.NetworkReaderMatsimV2;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.transformations.CH1903LV03PlustoWGS84;
import org.matsim.core.utils.io.IOUtils;

public class UAVUsers {

	public static void main(String[] args) throws IOException {

		
		String[] speed = {"20.0","25.0","30.0"};
		String[] liftoff = {"60.0","120.0","180.0"};
		String[] waiting = {"120.0","300.0","600.0"};

		for (String s1 : speed) {
			for (String s2 : liftoff) {
				for (String s3 : waiting) {
					String popLoc = args[0] +"/out" + s1 + "_" + s2 +"_" + s3 + "_0.5price/ITERS/it.100/" + s1 + "_" + s2 +"_" + s3 + "_0.5price.100.plans.xml.gz";
					
					File f = new File(popLoc);
					if(f.exists() ) {				
						Config config = ConfigUtils.createConfig();
						Scenario scenario = ScenarioUtils.createScenario(config);
						PopulationReader popReader = new PopulationReader(scenario);
						popReader.readFile(popLoc);
						NetworkReaderMatsimV2 networkReader = new NetworkReaderMatsimV2(scenario.getNetwork());
						networkReader.readFile(args[1]);
						String outLoc = args[2] + s1 + "_" + s2 +"_" + s3 + "_0.5price.csv";
						BufferedWriter writer = IOUtils.getBufferedWriter(outLoc);
						writer.write("personId,distance,traveltime,start_x,start_y,end_x,end_y,start_x_wgs,start_y_wgs,end_x_wgs,end_y_wgs");
						writer.newLine();
						
						Network network = scenario.getNetwork();
						CH1903LV03PlustoWGS84 transformation = new CH1903LV03PlustoWGS84();
						for (Person person : scenario.getPopulation().getPersons().values()) {
							
							for (PlanElement pe : person.getSelectedPlan().getPlanElements()) {
								
								if (pe instanceof Leg) {
									
									if (((Leg) pe).getMode().equals("uav")) {
										
										writer.write(person.getId() + "," + 
										((Leg) pe).getRoute().getDistance() + "," + ((Leg) pe).getRoute().getTravelTime());
										
										Id<Link> startLinkId = ((Leg) pe).getRoute().getStartLinkId();
										Coord startCoord = network.getLinks().get(startLinkId).getCoord();
										Coord startCoordT = transformation.transform(startCoord);
										
										Id<Link> endLinkId = ((Leg) pe).getRoute().getEndLinkId();
										Coord endCoord = network.getLinks().get(endLinkId).getCoord();
										Coord endCoordT = transformation.transform(endCoord);
										
										writer.write("," + startCoord.getX() + "," + startCoord.getY());
										writer.write("," + endCoord.getX() + "," + endCoord.getY());
										
										writer.write("," + startCoordT.getX() + "," + startCoordT.getY());
										writer.write("," + endCoordT.getX() + "," + endCoordT.getY());
	
										writer.newLine();										
									}					
								}
							}
						}
						writer.flush();
						writer.close();
					}					
				}			
			}
		}	
	}
}
