package rpEngine.graphical.structs;

import java.io.Serializable;

import utils.math.Vector3f;

public class TrackAnchor implements Serializable {
	private Vector3f position;
	private Vector3f direction;
	private Vector3f pitch; //="to-left-Vector"
	
	public TrackAnchor(Vector3f position, Vector3f direction, Vector3f pitch) {
		this.position = position;
		this.direction = direction;
		this.pitch = pitch;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getDirection() {
		return direction;
	}

	public void setDirection(Vector3f direction) {
		this.direction = direction;
	}

	public Vector3f getPitch() {
		return pitch;
	}

	public void setPitch(Vector3f pitch) {
		this.pitch = pitch;
	}
}
