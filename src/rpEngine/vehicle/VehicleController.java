package rpEngine.vehicle;

import java.io.Serializable;

import rpEngine.graphical.objects.Entity;
import utils.math.Vector3f;


public abstract class VehicleController{
	private Vehicle vehicle;
	private String playerName; 
	private VehiclePosition vehiclePosition;
	
	public static VehicleController load(String drivername, Serializable data){
		//TODO: implement loading from Serializable
		VehicleParts parts = new VehicleParts();
		//TODO: implement finding initPositions for every Vehicle in this Race.(raceController)
		Vector3f worldPosition = new Vector3f(370, 7, -20);
		VehiclePosition position = new VehiclePosition(new Vector3f(0,0,-1), new Vector3f(0,1,0), worldPosition);
		return new UserInteractionController(drivername, position, new Vehicle(parts));
	}
	
	public VehicleController(String driverName, VehiclePosition vehiclePosition, Vehicle vehicle){
		playerName = driverName;
		this.vehicle = vehicle;
		for(Entity entity: vehicle.getModel()){
			entity.setPosition(vehiclePosition.worldPosition);
		}
	}
	
	public Vehicle getVehicle() {
		return vehicle;
	}

	public String getPlayerName() {
		return playerName;
	}

	public VehiclePosition getVehiclePosition() {
		return vehiclePosition;
	}

	protected void setPosition(Vector3f newPosition){
		vehiclePosition.worldPosition = newPosition;
	}
	
	protected void rotateRight(int angle){
		//TODO ...
	}
}
