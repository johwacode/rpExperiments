package rpEngine.vehicle;

import java.io.Serializable;

import rpEngine.graphical.objects.Camera;
import utils.math.Vector3f;

public class VehiclePosition implements Serializable{
	private static final long serialVersionUID = -7065408712059899648L;
	
	public Vector3f worldPosition;
	public float pitch, yaw, roll;
	public float impulsePitch, impulseYaw, impulseRoll;
	public Vector3f directionFront, directionTop, directionRight;
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
		this.directionRight = new Vector3f(1,0,0);
		rotateRight(yaw);
		impulseWholeCar = new Vector3f(0,0,0);
	}
	
	protected void moveForward(float amount){
		worldPosition.x += amount*directionFront.x;
		worldPosition.y += amount*directionFront.y;
		worldPosition.z += amount*directionFront.z;
		//TODO: trägheitseinfluss -> Impulse
	}
	
	protected void rotateRight(float angleRAD){
		/* rotation around directionTop:
		 * -directionFront&directionRight are rotated around top-Axis
		 * -yaw, roll & pitch get updated
		 */
		directionFront.rotateAroundAxis(angleRAD*-0.0174f, directionTop);
		directionFront.normalise();
		directionRight.rotateAroundAxis(angleRAD*-0.0174f, directionTop);
		directionRight.normalise();	
		
		updatePitchYawRoll();
	}
	
	
	protected void rotateFrontUp(float angleRAD){
		directionFront.rotateAroundAxis(angleRAD*-0.0174f, directionRight);
		directionFront.normalise();
		directionTop.rotateAroundAxis(angleRAD*-0.0174f, directionRight);
		directionTop.normalise();
		
		updatePitchYawRoll();
	}
	
	protected void rotateRollRight(float angleRAD){
		directionRight.rotateAroundAxis(angleRAD*-0.0174f, directionFront);
		directionRight.normalise();
		directionTop.rotateAroundAxis(angleRAD*-0.0174f, directionFront);
		directionTop.normalise();
		
		updatePitchYawRoll();
	}

	private void updatePitchYawRoll(){
		float angle = (float) Math.acos(-directionFront.y);
		//if(directionFront.y<=0)angle*=-1;
		pitch = (float) Math.toDegrees(angle)-90;

		angle = (float) Math.acos(directionTop.y);
		if(directionTop.z<=0)angle*=-1;
		roll = (float) Math.toDegrees(angle);
		
		angle = (float) Math.acos(-directionFront.z);
		if(directionFront.x<=0)angle*=-1;
		yaw = (float) Math.toDegrees(angle);
	}
}
