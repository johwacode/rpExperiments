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
	
	protected Vector3f rotateRight(int angle){
		float angleRad = (float) Math.toRadians(angle);
		float sinYaw = (float) Math.sin(yaw+90);
		float sinPitch = (float) Math.sin(pitch+90);
		float sinRoll = (float) Math.sin(roll+90);
		Vector3f rotXYZ = new Vector3f();
		rotXYZ.x = angleRad*sinPitch*sinYaw;
		rotXYZ.y = angleRad*sinYaw*sinRoll;
		rotXYZ.z = angleRad*sinPitch*sinRoll;
		yaw += rotXYZ.y;
		pitch += rotXYZ.z;
		roll += rotXYZ.x;
		return rotXYZ;
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
