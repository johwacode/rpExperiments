package rpEngine.vehicle;

import utils.math.Vector3f;

public abstract class VehicleComponent {
	private Vector3f position;
	private int weight;
	private Vector3f aeroDragFront;
	private Vector3f aeroDragSide;
	private Vehicle vehicle;
	
	public VehicleComponent(Vector3f position, int weight,
			Vector3f aeroDragFront, Vector3f aeroDragSide) {
		this.position = position;
		this.weight = weight;
		this.aeroDragFront = aeroDragFront;
		this.aeroDragSide = aeroDragSide;
	}
	
	public void setVehicle(Vehicle vehicle){
		this.vehicle = vehicle;
	}
	
	protected Vehicle getVehicle(){
		return vehicle;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	public int getWeight() {
		return weight;
	}
	public Vector3f getAeroDragFront() {
		return aeroDragFront;
	}
	public Vector3f getAeroDragSide() {
		return aeroDragSide;
	}
}
