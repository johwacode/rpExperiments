package rpEngine.vehicle;

import java.io.Serializable;

import utils.math.Vector3f;

public class VehiclePosition implements Serializable{
	private static final long serialVersionUID = -7065408712059899648L;
	Vector3f front, top, pitch, impulse;
}
