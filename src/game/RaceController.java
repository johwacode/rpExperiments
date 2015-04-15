package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rpEngine.vehicle.Vehicle;
import utils.math.Vector3f;


public class RaceController {
	SceneGraph scene;
	List<Vehicle> vehicles;
	
	public RaceController(String[] player, SceneGraph sceneGraph, Serializable args){
		scene = sceneGraph;
		vehicles= new ArrayList<>();
		for(String driverName:player){
			vehicles.add(Vehicle.load(driverName, args));
		}
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
