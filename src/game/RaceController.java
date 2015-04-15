package game;

import rpEngine.vehicle.Vehicle;
import utils.math.Vector3f;


public class RaceController {
	SceneGraph scene;
	
	public RaceController(SceneGraph sceneGraph){
		scene = sceneGraph;
		initVehicles();
	}
	
	public Vehicle getOwnCar(){
		//Todo: implement
		return null;
	}
	
	public int getPositionOf(String playername){
		//Todo: implement
		return 1;
	}
	
	private void setStartPositions(Vector3f[] startPositions){
		//Todo: implement
	}
	
	private void initVehicles() {
		//Todo: implement
	}
	
	private void startRace(int countdown){
		//Todo: implement
	}
}
