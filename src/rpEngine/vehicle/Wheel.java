package rpEngine.vehicle;

import utils.math.Vector3f;

public class Wheel extends VehicleComponent{
	private int size;
	private float grip;
	private float absThreshold; //wert, ab dem abs anspringt
	private boolean inWheelMotor;
	private Vector3f currentDirection;
	private Vector3f currentImpulse;
	
	public Wheel(Vector3f position, int weight, Vector3f aeroDragFront,
			Vector3f aeroDragSide, int size, float grip, float absThreshold,
			boolean inWheelMotor, Vector3f currentDirection) {
		super(position, weight, aeroDragFront, aeroDragSide);
		this.size = size;
		this.grip = grip;
		this.absThreshold = absThreshold;
		this.inWheelMotor = inWheelMotor;
		this.currentDirection = currentDirection;
		this.currentImpulse = new Vector3f(0,0,0);
	}
	
	public void speedUp(float amount){
		
	}
	public void useBreak(float amount){
		
	}
	public void turnWeel(float amount){
		
	}
	public Vector3f getCurrentImpulse() {
		return currentImpulse;
	}

	public float getResistance() {
		// TODO Auto-generated method stub
		return 1;
	}

	public float move(float f) {
		//this.getVehicle().move();
		return 1;
	}
}
