package rpEngine.vehicle;

import java.io.Serializable;
import java.text.DecimalFormat;

import rpEngine.graphical.structs.HUDfriendly;
import rpEngine.graphical.structs.InputHandler;
import utils.math.Vector3f;

public class Vehicle implements HUDfriendly {
	private static DecimalFormat format = new DecimalFormat("###0.#");
	
	private VehiclePosition currentPosition;
	private VehicleParts parts;
	private VehicleController driver;
	
	public Vehicle(){
	}
	
	public static Vehicle load(String drivername, Serializable data){
		//TODO: implement
		return new Vehicle();
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