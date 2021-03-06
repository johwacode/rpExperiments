package rpEngine.vehicle;

import java.io.Serializable;

import utils.math.Vector3f;

public class VehiclePosition implements Serializable{
	private static final long serialVersionUID = -7065408712059899648L;
	
	public Vector3f worldPosition;
	public float pitch, yaw, roll;
	public float impulsePitch, impulseYaw, impulseRoll;
	public Vector3f directionFront, directionTop, directionRight;
	public Vector3f impulseWholeCar;
	
	public int weight;
	public float width, length, height;
	
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
		rotateRight((float)Math.toRadians(yaw));
		impulseWholeCar = new Vector3f(0,0,0);
		width = 1.4f;
		length = 3;
		height = 0.8f;
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
		//TODO further: insert roll into view-matrix.

		float angle = (float) Math.atan2(directionFront.x, -directionFront.z);
		yaw = (float) Math.toDegrees(angle);

		/*angle = (float) Math.acos(directionTop.y);
		if(directionTop.x<=0)angle*=-1;
		roll = (float) Math.toDegrees(angle);
		*/
		
		if(Math.abs(Math.abs(yaw)-90)>2.8){ //at least a little less better.
			angle = (float) Math.atan2(-directionFront.y, -directionFront.z);
			if(directionFront.z>=-0.00000005) angle+=Math.PI;
			pitch = (float) Math.toDegrees(angle);
			//System.out.println(angle);
		}
		else {
		
		}		
	}
}
