package rpEngine.vehicle;

import java.io.Serializable;

import utils.math.Vector3f;

public class VehiclePosition implements Serializable{
	private static final long serialVersionUID = -7065408712059899648L;
	
	public Vector3f worldPosition;
	public float pitch, yaw, roll;
	public float impulsePitch, impulseYaw, impulseRoll;
	public Vector3f directionFront, directionRight;
	public Vector3f impulseWholeCar;
	
	public int weight;
	
	public VehiclePosition(Vector3f worldPosition){
		this(worldPosition, -2, 0, 0);
	}
	
	public VehiclePosition(Vector3f worldPosition, float pitch, float yaw, float roll){
		this.worldPosition = worldPosition;
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
		this.directionFront = new Vector3f(0,0,-1);
		directionFront.rotateXZ(yaw);
		//TODO: insert pitch&roll
		impulseWholeCar = new Vector3f(0,0,0);
	}
	
	protected void moveForward(float amount){
		worldPosition.x += amount*directionFront.x;
		worldPosition.y += amount*directionFront.y;
		worldPosition.z += amount*directionFront.z;
		//TODO: trägheitseinfluss -> Impulse
	}
	
	protected void increasePitch(int amount){
		//TODO: refresh pitch, impulsePitch and frontDirection
		/*
		 *--->XZ to yaw
		float angle = (float) Math.acos(vehicle.getCurrentDirection().z / vehicle.getCurrentDirection().length());
		if(vehiclePos.frontDirection.x>0)angle*=-1;
		yaw = (float) Math.toDegrees(angle);
		*/
	}
}
