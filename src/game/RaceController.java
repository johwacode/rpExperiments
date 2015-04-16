package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rpEngine.vehicle.UserInteractionController;
import rpEngine.vehicle.Vehicle;
import rpEngine.vehicle.VehicleController;
import rpEngine.vehicle.VehiclePosition;
import utils.math.Vector3f;


public class RaceController {
	SceneGraph scene;
	List<VehicleController> vehicles;
	
	public RaceController(String[] player, SceneGraph sceneGraph, Serializable args){
		scene = sceneGraph;
		Vector3f[] positions = createStartPositions(player.length, sceneGraph);
		vehicles = new ArrayList<>();
		for(int i=0; i<player.length; i++){
			VehicleController vc = new UserInteractionController(player[i], new VehiclePosition(positions[i]), new Vehicle());
			scene.addEntities(vc.getVehicle().getModel());
		}
	}
	
	private static Vector3f[] createStartPositions(int countOfVehicles, SceneGraph scene){
		Vector3f[] positions = new Vector3f[countOfVehicles];
		for(int i=0; i<countOfVehicles; i++){
			positions[i] = new Vector3f(370+2*i, 7, -10);
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