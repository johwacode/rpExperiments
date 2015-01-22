package rpEngine.vehicle;

import utils.math.Vector3f;

public class Chassis extends VehicleComponent{	
	private float width, height, length;

	public Chassis(float width, float height, float length,
			int weight, Vector3f aeroDragFront, Vector3f aeroDragSide) {
		super(null, weight, aeroDragFront, aeroDragSide);
		this.width = width;
		this.height = height;
		this.length = length;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public float getLength() {
		return length;
	}}
