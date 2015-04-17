package rpEngine.vehicle;

import rpEngine.graphical.objects.Entity;
import utils.math.Vector3f;


public abstract class VehicleController{
	private Vehicle vehicle;
	private String playerName; 
	private VehiclePosition vehiclePosition;
	private VehicleParts parts;
	
	
	public VehicleController(String driverName, VehiclePosition vehiclePosition, Vehicle vehicle){
		playerName = driverName;
		this.vehicle = vehicle;
		this.parts = vehicle.getParts();
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
		updateEntities();
	}
	
	protected void rotateRight(int angle){
		//TODO ...
	}

	public void fuel(float amount) {
		/*
		//TODO: resistance.
		float kw = parts.engine.fuel(amount);
		float rotation = parts.transmissionUnit.translate(kw);
		float speed = parts.wheels.rotate(rotation);
		*/
		float speed = amount;
		vehiclePosition.moveForward(speed);
		updateEntities();
	}
	
	private void updateEntities(){
		for(Entity e:vehicle.getModel()) e.setMatrixOutdatedFlag();
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
