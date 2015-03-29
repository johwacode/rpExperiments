package rpEngine.graphical.structs;

import java.io.Serializable;

import utils.math.Vector3f;

public class TrackAnchor implements Serializable {
	private static final long serialVersionUID = 1L;
	private Vector3f position;
	private Vector3f direction;
	private Vector3f pitch; //="to-left-Vector"
	private Vector3f normal;
	private float nDotPos;
	
	public TrackAnchor(Vector3f position, Vector3f direction, Vector3f pitch) {
		this.position = position;
		this.direction = direction;
		this.pitch = pitch;
		this.normal = Vector3f.cross(direction, pitch);
		normal.normalise();
		nDotPos = Vector3f.dot(normal, position);
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getDirection() {
		return direction;
	}

	public Vector3f getPitch() {
		return pitch;
	}
		
	public Vector3f getNormal() {
		return normal;
	}

	public float getnDotPos() {
		return nDotPos;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("TrackAnchor(");
		sb.append("pos:"+position+";");
		sb.append("dir:"+direction+";");
		sb.append("pitch:"+pitch+")");
		return sb.toString();
	}
}
