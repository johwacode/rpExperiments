package rpEngine.graphical.structs;

import game.SceneGraph;

import java.io.Serializable;

import utils.math.Vector3f;

public class TrackAnchor implements Serializable {
	private static final long serialVersionUID = 1L;
	private Vector3f position;
	private Vector3f direction;
	private Vector3f pitch; //="to-left-Vector"
	private Vector3f normal;
	private float nDotPos;
	
	//stuff for barycentricCoords
	private Vector3f baryPointA, baryPointD,
			baryVecCA, baryVecBA, baryVecDA;
	private float baryDotCACA, baryDotCABA, 
			baryDotBABA, baryDotCADA, baryDotDADA,
			baryInvDenomABC, baryInvDenomACD;
	
	
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
	
	protected void initPointsAnD(int rowCount){
		int dist = (rowCount-1)/2;
		baryPointA = new Vector3f(
				position.x+dist*pitch.x,
				position.y+dist*pitch.y,
				position.z+dist*pitch.z);
		baryPointD = new Vector3f(
				position.x-dist*pitch.x,
				position.y-dist*pitch.y,
				position.z-dist*pitch.z);
	}
	
	public void initBarycentricSystem(int rowCount, TrackAnchor followingAnchor){
		if(baryPointD==null) initPointsAnD(rowCount);
		if(followingAnchor.baryPointD==null) followingAnchor.initPointsAnD(rowCount);
		baryVecCA = Vector3f.sub(followingAnchor.baryPointD, baryPointA);
		baryVecBA = Vector3f.sub(followingAnchor.baryPointA, baryPointA);
		baryVecDA = Vector3f.sub(baryPointD, baryPointA);
		baryDotCACA = Vector3f.dot(baryVecCA, baryVecCA);
		baryDotCABA = Vector3f.dot(baryVecCA, baryVecBA);
		baryDotBABA = Vector3f.dot(baryVecBA, baryVecBA);
		baryDotCADA = Vector3f.dot(baryVecCA, baryVecDA);
		baryDotDADA = Vector3f.dot(baryVecDA, baryVecDA);
		baryInvDenomABC = 1/(baryDotCACA*baryDotBABA-baryDotCABA*baryDotCABA);
		baryInvDenomACD = 1/(baryDotCACA*baryDotDADA-baryDotCADA*baryDotCADA);
	}
	
	public boolean isPointInside(Vector3f point){
		Vector3f vPA = Vector3f.sub(point, baryPointA);
		float dotCAPA = Vector3f.dot(baryVecCA, vPA);
		float dotBAPA = Vector3f.dot(baryVecBA, vPA);
		float dotDAPA = Vector3f.dot(baryVecDA, vPA);
		float uABC = (baryDotBABA*dotCAPA-baryDotCABA*dotBAPA) * baryInvDenomABC;
		float vABC = (baryDotCACA*dotBAPA-baryDotCABA*dotCAPA) * baryInvDenomABC;
		float uACD = (baryDotDADA*dotCAPA-baryDotCADA*dotDAPA) * baryInvDenomACD;
		float vACD = (baryDotCACA*dotDAPA-baryDotCADA*dotCAPA) * baryInvDenomACD;
		return ((uABC >= 0) && (vABC >= 0) && (uABC + vABC < 1)) 
				|| ((uACD >= 0) && (vACD >= 0) && (uACD + vACD < 1));
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("TrackAnchor(");
		sb.append("pos:"+position+";");
		sb.append("dir:"+direction+";");
		sb.append("pitch:"+pitch+")");
		return sb.toString();
	}
	
	
	/**
	 * DEBUGSTUFF just adds a sphere at a given position
	 */
	private static void addDebugSphere(float x, float y, float z, Vector3f worldPos){
		SceneGraph.addDebugSphere(new Vector3f(
				x+worldPos.x,
				y+worldPos.y,
				z+worldPos.z
				));
	}
	
}
