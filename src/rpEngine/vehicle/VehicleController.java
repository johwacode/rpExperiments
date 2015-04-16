package rpEngine.vehicle;

import java.io.Serializable;

import rpEngine.graphical.objects.Entity;
import utils.math.Vector3f;


public abstract class VehicleController{
	private Vehicle vehicle;
	private String playerName; 
	private VehiclePosition vehiclePosition;
	
	
	public VehicleController(String driverName, VehiclePosition vehiclePosition, Vehicle vehicle){
		playerName = driverName;
		this.vehicle = vehicle;
		this.vehiclePosition = vehiclePosition;
		vehicle.setController(this);
		for(Entity entity: vehicle.getModel()){
			entity.setPosition(vehiclePosition.worldPosition); //TODO: Correction for Wheels (pos = worldPos + x)
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

	public void fuel(float f) {
		// TODO Auto-generated method stub
		
	}

	public void useBreak(float f) {
		// TODO Auto-generated method stub
		
	}

	public void turnRight(float f) {
		// TODO Auto-generated method stub
		
	}

	public void turnLeft(float f) {
		// TODO Auto-generated method stub
		
	}
}
