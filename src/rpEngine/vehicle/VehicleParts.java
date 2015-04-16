package rpEngine.vehicle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rpEngine.graphical.objects.Entity;

public class VehicleParts implements Serializable{
	private static final long serialVersionUID = -8117181536048511797L;
	public Steering steering;
	public Engine engine;
	public TransmissionUnit transmissionUnit;
	public Chassis chassis;
	public Wheels wheels;
	
	private transient List<Entity> models;
	
	public VehicleParts(){
		engine = new Engine("r170slk200kompressorEngine", 120, 1998, 4, 230, 5300, 120, 120, 0.8f);
		transmissionUnit = new TransmissionUnit("porsche91503Transmission", 15, 4.4285f, 3.181f, 1.833f, 1.261f, 0.9615f, 0.7586f);
		wheels = new Wheels(235, 55, 18, 0.7f);
		chassis = new Chassis("blockChassis", 1, 1, 1, 700, 1, 1, 0.3f, "cube2", "metal");
		steering = new Steering(0.5f);
	}
	
	public VehicleParts(Engine engine, TransmissionUnit transmissionUnit, Chassis chassis, Wheels wheels, Steering steering){
		this.steering = steering;
		this.engine = engine;
		this.transmissionUnit = transmissionUnit;
		this.chassis = chassis;
		this.wheels = wheels;
	}

	public List<Entity> getDrawables(){
		if(models==null)refreshModel();
		return models;
	}
	public void refreshModel(){
		models = new ArrayList<>();
		models.add(chassis.getModel());
		models.addAll(wheels.getModels());
	}
}
