package rpEngine.vehicle;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import rpEngine.graphical.objects.Entity;
import rpEngine.graphical.structs.HUDfriendly;
import utils.math.Vector3f;

public class Vehicle implements HUDfriendly {
	private static DecimalFormat format = new DecimalFormat("###0.#");
	
	private Steering steering;
	private Engine engine;
	private TransmissionUnit transmissionUnit;
	private Chassis chassis;
	private List<Wheel> wheels;
	
	private Entity drawableModel;
	private Vector3f currentDirection;
	private Vector3f currentImpulse;
	
	public Vehicle(Entity drawableModel){
		this.drawableModel = drawableModel;
		currentDirection = new Vector3f(0, 0, 0.2f);
		currentImpulse = new Vector3f();
		
		steering = new Steering();
	}
	
	@Override
	public String getHUDmessage(String name) {
		switch(name){
		case "GearContent": return getTransmissionUnit().getCurrentGearAsString();
		case "RPMContent": format.format(getEngine().getCurrentRpm());
		default: return "";
		}
		
	}
		
	public Vector3f getPosition() {
		return drawableModel.getPosition();
	}

	public void setPosition(Vector3f position) {
		drawableModel.setPosition(position);
	}

	public Vector3f getCurrentDirection(){
		return currentDirection;
	}
	
	public void setCurrentDirection(Vector3f newDirection){
		currentDirection = newDirection;
	}
	
	public Entity getDrawableModel(){
		return drawableModel;
	}
	
	public Vector3f getCurrentImpulse() {
		return currentImpulse;
	}

	public void setCurrentImpulse(Vector3f currentImpulse) {
		this.currentImpulse = currentImpulse;
	}

	public void bindComponent(Engine component){
		engine = component;
		engine.setVehicle(this);
	}
	
	public void bindComponent(Chassis component){
		chassis = component;
		chassis.setVehicle(this);
	}
	
	public void bindComponent(TransmissionUnit component){
		transmissionUnit = component;
		transmissionUnit.setVehicle(this);
	}
	
	public void bindComponent(Wheel component){
		if(wheels==null) wheels = new ArrayList<>();
		wheels.add(component);
		component.setVehicle(this);
	}
	
	public void bindComponent(Steering component){
		steering = component;
		steering.setVehicle(this);
	}
		
	public List<Wheel> getWheels(){
		return wheels;
	}
	
	public Engine getEngine(){
		return engine;
	}
	public TransmissionUnit getTransmissionUnit(){
		return transmissionUnit;
	}
	public Steering getSteering(){
		return steering;
	}
	public Chassis getChassis(){
		return chassis;
	}
	
	public void startOrStop(){
		if(engine==null) return;
		if(!engine.running) engine.turnOn();
		else engine.turnOff();
	}
	
	public void move(){
		//drehen
		double angle = Math.toRadians(-steering.getValue());
		setCurrentDirection( new Vector3f(
				(float) (currentDirection.x*Math.cos(angle)
						+currentDirection.z*Math.sin(angle)),
				currentDirection.y,
				(float) (-currentDirection.x*Math.sin(angle)
						+currentDirection.z*Math.cos(angle))
				));
		this.drawableModel.increaseRotation(0, -steering.getValue(), 0);
		steering.turnBack();
		
		//fahren
		Vector3f newPos = new Vector3f();
		currentDirection.normalise();
		currentDirection.scale(0.2f);
		Vector3f.sub(drawableModel.getPosition(), currentDirection, newPos);
		setPosition(newPos);
		drawableModel.setPosition(newPos);
	}
}
