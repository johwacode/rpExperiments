package rpEngine.vehicle;

import java.io.Serializable;
import java.text.DecimalFormat;

import rpEngine.graphical.structs.HUDfriendly;
import utils.math.Vector3f;

public class Vehicle implements HUDfriendly {
	private static DecimalFormat format = new DecimalFormat("###0.#");
	
	private VehiclePosition currentPosition;
	private VehicleParts parts;
	private VehicleController driver;
	
	public Vehicle(VehicleController driver, VehicleParts parts, VehiclePosition position){
		this.driver = driver;
		driver.setVehicle(this);
		this.parts = parts;
		this.currentPosition = position;
	}
	
	public static Vehicle load(String drivername, Serializable data){
		//TODO: implement loading from Serializable
		VehicleController driver = new UserInteractionController(drivername);
		VehicleParts parts = new VehicleParts();
		//TODO: implement finding initPositions for every Vehicle in this Race.(raceController)
		Vector3f worldPosition = new Vector3f(370, 7, -20);
		VehiclePosition position = new VehiclePosition(new Vector3f(0,0,-1), new Vector3f(0,1,0), worldPosition);
		return new Vehicle(driver, parts, position);
	}
	
	@Override
	public String getHUDmessage(String name) {
		switch(name){
		case "GearContent": return parts.transmissionUnit.getCurrentGearAsString();
		case "RPMContent": format.format(parts.engine.getCurrentRpm());
		default: return "";
		}
		
	}

	public Vector3f getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	public Vector3f getCurrentDirection() {
		// TODO Auto-generated method stub
		return null;
	}
}