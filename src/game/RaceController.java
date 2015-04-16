package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rpEngine.vehicle.UserInteractionController;
import rpEngine.vehicle.Vehicle;
import rpEngine.vehicle.VehicleController;
import rpEngine.vehicle.VehicleControllerMultiplayer;
import rpEngine.vehicle.VehiclePosition;
import utils.math.Vector3f;


public class RaceController {
	private static enum controllerTypes {USER, BOT, MULTIPLAYER};
	SceneGraph scene;
	List<VehicleController> vehicles;
	
	public RaceController(String[] player, SceneGraph sceneGraph, Serializable args){
		if(player.length==0) return;
		
		//TODO: replace by reading from menuParams.
		controllerTypes[] controlledBy = new controllerTypes[player.length];
		controlledBy[0]=controllerTypes.USER;
		for(int i=1; i<player.length; i++) controlledBy[i]=controllerTypes.MULTIPLAYER;
		
		scene = sceneGraph;
		Vector3f[] positions = createStartPositions(player.length, sceneGraph);
		vehicles = new ArrayList<>();
		VehicleController vc = null;
		for(int i=0; i<player.length; i++){
			switch(controlledBy[i]){
			case BOT:
				break;
			case MULTIPLAYER: vc = new VehicleControllerMultiplayer(player[i], new VehiclePosition(positions[i]), new Vehicle());
				break;
			case USER: vc = new UserInteractionController(player[i], new VehiclePosition(positions[i]), new Vehicle());
				break;
			}
			scene.addEntities(vc.getVehicle().getModel());
			vehicles.add(vc);
		}
		sceneGraph.getCamera().setVehicle(vehicles.get(0).getVehicle());
	}
	
	private static Vector3f[] createStartPositions(int countOfVehicles, SceneGraph scene){
		Vector3f[] positions = new Vector3f[countOfVehicles];
		for(int i=0; i<countOfVehicles; i++){
			positions[i] = new Vector3f(371+4*i, 7.5f, -10);
			//TODO: determine position via trackInformation
		}
		return positions; 
	}
	
	/**
	 * @param playername
	 * @return position in Race -> first, second etc.
	 */
	public int getPositionOf(String playername){
		//Todo: implement
		return 1;
	}
	
	private void startRace(int countdown){
		//Todo: implement
	}
}