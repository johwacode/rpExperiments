package rpEngine.vehicle;

import java.io.Serializable;

import utils.math.Vector3f;

public class VehiclePosition implements Serializable{
	private static final long serialVersionUID = -7065408712059899648L;
	
	public Vector3f worldPosition;
	public Vector3f directionFront, directionTop;
	public Vector3f impulseWholeCar;
	/**
	 * coords store InnerImpulses of the Car. <br/>
	 * x: rotation around front-back-axis; positive means right side of the car goes down, left side lifts itself up. <br/>
	 * y: rotation around y-Axis; positive means car rotates to right side. <br/>
	 * z: rotation around left-right-axis: positive->front sinks downwards, back goes up.
	 */
	public Vector3f impulseInner;
	
	/**
	 * front and top are set to (0,0,-1) and (0,1,0) per default.
	 * @param worldPosition
	 */
	public VehiclePosition(Vector3f worldPosition){
		this(new Vector3f(0,0,-1), new Vector3f(0,1,0), worldPosition);
	}
	
	/**
	 * @param frontDiretion
	 * @param topDirection
	 * @param worldPosition
	 */
	public VehiclePosition(Vector3f frontDiretion, Vector3f topDirection, Vector3f worldPosition){
		this.worldPosition = worldPosition;
		this.directionFront = frontDiretion;
		this.directionTop = topDirection;
		impulseWholeCar = new Vector3f();
		impulseInner = new Vector3f();
	}
}
