package rpEngine.vehicle;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

import rpEngine.graphical.objects.Entity;
import rpEngine.graphical.structs.HUDfriendly;
import utils.math.Vector3f;

public class Vehicle implements HUDfriendly {
	private static DecimalFormat format = new DecimalFormat("###0.#");
	
	private VehicleParts parts;
	private VehicleController driver;
	
	/**
	 * creates a vehicle with default parts.
	 */
	public Vehicle(){
		parts = new VehicleParts();
	}
	
	/**
	 * creates a vehicle using specifies parts
	 * @param parts a VehicleParts-Object.
	 */
	public Vehicle(VehicleParts parts){
		this.parts = parts;
	}
	
	public void setController(VehicleController driver){
		this.driver = driver;
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
	
	public List<Entity> getModel(){
		return parts.getDrawables();
	}
}