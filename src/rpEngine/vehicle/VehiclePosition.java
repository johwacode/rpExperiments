package rpEngine.vehicle;

import java.io.Serializable;

import rpEngine.graphical.objects.Camera;
import utils.math.Vector3f;

public class VehiclePosition implements Serializable{
	private static final long serialVersionUID = -7065408712059899648L;
	
	public Vector3f worldPosition;
	public float pitch, yaw, roll;
	public float impulsePitch, impulseYaw, impulseRoll;
	public Vector3f directionFront, directionTop;
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
		this.directionTop = new Vector3f(0,1,0);
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
	
	
	protected Vector3f rotateRight(float angle){
		//preassumed: length of normal is always 1
		//float sinAxisX = directionTop.x; and so on.
		
		Vector3f rotXYZ = new Vector3f();
		rotXYZ.x = -angle*directionTop.x;
		rotXYZ.y = -angle*directionTop.y;
		rotXYZ.z = -angle*directionTop.z;
		yaw -= rotXYZ.y;
		pitch -= rotXYZ.z;
		roll -= rotXYZ.x;
		
		double radians = -Math.toRadians(yaw);
		directionFront = new Vector3f(
				(float) (-Math.sin(radians)),
							-0,
				(float) (-Math.cos(radians))
				);
		directionFront.normalise(); 
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
