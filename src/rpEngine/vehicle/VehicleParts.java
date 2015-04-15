package rpEngine.vehicle;

import java.io.Serializable;
import java.util.List;

import rpEngine.graphical.objects.Entity;

public class VehicleParts implements Serializable{
	private static final long serialVersionUID = -8117181536048511797L;
	public Steering steering;
	public Engine engine;
	public TransmissionUnit transmissionUnit;
	public Chassis chassis;
	public List<Wheel> wheels;
	
	public List<Entity> getDrawables(){
		return null;
	}
}
