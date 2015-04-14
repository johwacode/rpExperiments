package rpEngine.graphical.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rpEngine.graphical.model.Loader;
import rpEngine.graphical.model.Material;
import rpEngine.graphical.model.Model;
import rpEngine.graphical.model.Texture;
import rpEngine.graphical.structs.TrackAnchor;
import utils.math.Vector2f;
import utils.math.Vector3f;

/**
 * serializable Entity out of which Tracks can be built. 
 * defined by a start-anchor, XY-angle, distance, pitch, height, ... 
 * @author joh
 *
 */
public class Curve extends Entity{
	private static Texture asphalt = new Texture(Loader.loadTexture(Material.ASPHALT, "asphalt", true));
	private static Texture previewTexture = new Texture(Loader.loadTexture(Material.PREVIEW, "surface_water3", true));
	private static Texture trackborder = new Texture(Loader.loadTexture(Material.TRACKBORDER, "trackborder", true));
	private static final float HEIGHT = 0.4f;
	public static final int ROWS = 11; //has to be odd (Or even(but odd) prim?!)
	
	private static TrackAnchor lastAim;
	
	protected SerializableCurveData data;
	
	/**
	 * internal Constructor
	 */
	private Curve(SerializableCurveData data, Texture texture){
		super(createModel(data, texture), data.getWorldPosition(), 0, 0, 0, 1);
		this.data = data;
	}
	
	/**
	 * shorthand-Constructor for restoring Curves without defining texture etc
	 * @param data
	 */
	public Curve(SerializableCurveData data){
		this(data, asphalt);
		lastAim = data.getLastAnchor();
		initBarycentricTesting();
	}
	
	/**
	 * Fabric for building new Curves
	 * @param anchorStart
	 * @param angleXZ
	 * @param height
	 * @param distance
	 * @param pitch
	 */
	public static Curve create(TrackAnchor anchorStart, float angleXZ,
			float height, float distance, float pitch){
		return new Curve(new SerializableCurveData(anchorStart, angleXZ, height, distance, pitch));
	}
	
	/**
	 * Fabric for building new Curves. Additional possibility of setting as Preview-Element 
	 * @param anchorStart
	 * @param angleXZ
	 * @param height
	 * @param distance
	 * @param pitch
	 * @param preview
	 */
	public static Curve create(TrackAnchor anchorStart, float angleXZ,
			float height, float distance, float pitch, boolean preview){ 
		Texture texture = (preview)? previewTexture : asphalt; 
		SerializableCurveData sd = new SerializableCurveData(anchorStart, angleXZ, height, distance, pitch);
		return (preview)? new Curve(sd, previewTexture) : new Curve(sd);
	}

	/**
	 * build Curve from preview. Has no effect on normal curves.
	 * (also resets the "Curve.lastAim to current Curves Aim)
	 */
	public Curve buildCurve(){
		this.getModel().setTexture(asphalt);
		Curve.lastAim = this.data.getLastAnchor();
		initBarycentricTesting();
		return this;
	}
	
	public SerializableCurveData getData() {
		return data;
	}

	public static TrackAnchor getLastAnchor(){
		return lastAim;
	}

	/**
	 * calculates Constructor-stuff.
	 * @param data
	 * @param texture
	 * @return
	 */
	private static Model createModel(SerializableCurveData data, Texture texture){
		if(data==null || data.getFirstAnchor()==null) throw new IllegalArgumentException("No CurveData!");
		
		TrackAnchor anchorStart = data.getFirstAnchor();
		
		//cut into min 10 steps:
		int stepCount = (data.distance>10)? (int)Math.ceil(data.distance) : 10;
		float stepwidth = data.distance/stepCount; // ~=1, if distance>10
		float stepwidthY = data.height/stepCount;
		float stepwidthPitch = data.pitch/stepCount;
		float stepAngle = data.angleXZ/stepCount;
		
		//init Vertex-Variables
		int rowsPerSide = ROWS/2;
		float[] vertexArray = new float[(stepCount+1)*ROWS*3];
		Vector3f lastPos = anchorStart.getPosition().duplicate();
		Vector3f centerPos= new Vector3f();
		
		float[] normals = new float[vertexArray.length];
		float[] textureCoords = new float[(stepCount+1)*2*ROWS];
		
		
		//init direction
		Vector3f direction = anchorStart.getDirection().duplicate();
		direction.normalise();
		direction.scale(stepwidth);
		
		Vector3f curPitch = anchorStart.getPitch().duplicate();
		
		int vertexNumber = 0;
		int texNumber = 0;
		
		//loop over all steps (plus first Step at i=0 -> connect to AnchorPoint)
		for(int i=0; i<=stepCount; i++){
			if(i>1){
				//build main-point (center)
				direction.rotateXZ(stepAngle);
				direction.y += stepwidthY;
				Vector3f.add(lastPos, direction, centerPos);
				
				//calc pitch
				curPitch.rotateXZ(stepAngle);
				curPitch.y += stepwidthPitch;
			}
			
			//store the Anchor.
			Vector3f anchorPosition = Vector3f.add(anchorStart.getPosition(), centerPos);
			TrackAnchor currentAnchor = new TrackAnchor(anchorPosition, direction.duplicate(), curPitch.duplicate());
			data.addAnchor(currentAnchor);
			
			//add Vertices (from centerPos +- pitch)
			for(int row=-rowsPerSide; row<=rowsPerSide; row++){
				vertexArray[vertexNumber] = centerPos.x + row*curPitch.x;
				vertexArray[vertexNumber+1] = centerPos.y + row*curPitch.y;
				vertexArray[vertexNumber+2] = centerPos.z + row*curPitch.z;
				
				//TODO: calc real normals -> x,z determined by direction
				Vector3f normal = currentAnchor.getNormal();
				normals[vertexNumber] = normal.x;
				normals[vertexNumber+1] = normal.y;
				normals[vertexNumber+2] = normal.z;
				
				vertexNumber += 3;
				
				texNumber = setTextureCoords(textureCoords, texNumber);
			}
						
			lastPos = centerPos;
		}
		
		return new Model(Loader.loadEntityToVAO(vertexArray,
				textureCoords,
				normals,
				generateIndices(stepCount, ROWS),
				furthestDistance(data)),
				texture);
	}
	
	/**
	 * calculates a new Model which is cut off at a specified line.
	 * e.g. everything beyond x=400.
	 * @param direction x-z-vector to specify the outgoing direction of the Model.
	 * legal values:
	 * 		(-1,0) - cut of everything below the given value in x-Direction
	 * 		 (1,0) - cut above x
	 * 		(0,-1) - cut below z
	 * 		 (0,1) - cut above z.
	 * 
	 * @param value sets the value in the given direction, at which to cut
	 * @return a new  Curve-Model.
	 */
	private Model createCutOffModel(Vector2f direction, int value){
		//TODO: implement
		return null;
	}
	
	
	/**
	 * 
	 * @param texCoordArray
	 * @param counter
	 * @return counter
	 */
	private static int setTextureCoords(float[] texCoordArray, int counter){
		switch((counter/2)%4){
		case 0: texCoordArray[counter] = 0;
			texCoordArray[counter+1] = 0;
			break;
		case 1: texCoordArray[counter] = 0;
			texCoordArray[counter+1] = 1;
			break;
		case 2: texCoordArray[counter] = 1;
			texCoordArray[counter+1] = 1;
			break;
		case 3: texCoordArray[counter] = 1;
			texCoordArray[counter+1] = 0;
			break;
		}
		return counter += 2;
	}
	
	private static int[] generateIndices(int stepCount, int rowCount){
		/*
		 * n rows with m vertices each
		 * -> (n-1)(m-1) triangleLines for right-hand-triangles, equally left-hand-ones
		 * --> *2 -> *3points per triangle
		 */
		int[] indices = new int[6*(stepCount)*(rowCount-1)];
		int i = 0;
		int currentVertex;
		for(int stripe=0; stripe<stepCount; stripe++){
			for(int quad=0; quad<ROWS-1; quad++){
				
				//indices for 2 triangles (~ one quad)
				currentVertex = stripe*rowCount+quad;
				indices[i+0] = currentVertex;
				indices[i+1] = currentVertex+rowCount;
				indices[i+2] = currentVertex+1;
				
				indices[i+3] = currentVertex+rowCount;
				indices[i+4] = currentVertex+rowCount+1;
				indices[i+5] = currentVertex+1;
				
				i+=6;
			}
		}
		return indices;
	}
	
	private static float furthestDistance(SerializableCurveData data){
		//TODO
		return 0;
	}

	public static class SerializableCurveData implements Serializable{
		private static final long serialVersionUID = 1L;
		public List<TrackAnchor> anchors;
		public float angleXZ, height, distance, pitch;
		
		public SerializableCurveData(TrackAnchor anchorStart, float angleXZ,
				float height, float distance, float pitch) {
			anchors = new ArrayList<>();
			addAnchor(anchorStart);
			this.angleXZ = angleXZ;
			this.height = height;
			this.distance = distance;
			this.pitch = pitch;
		}
		
		private void addAnchor(TrackAnchor anchor){
			this.anchors.add(anchor);
		}
		
		public Vector3f getWorldPosition(){
			return anchors.get(0).getPosition();
		}
		
		public TrackAnchor getFirstAnchor(){
			return anchors.get(0);
		}
		
		public TrackAnchor getLastAnchor(){
			return this.anchors.get(anchors.size()-1);
		}
	}



	@Override
	public boolean intersects(Vector3f point) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public Vector3f getClosestIntersection(Vector3f point, Vector3f direction){
		Vector3f[] results = new Vector3f[2];
		int n=0;
		for(int i=0; i<data.anchors.size()-1 || n==2; i++){
			//TODO: find OutOfArea-Exception-causing array. (size~33/37)
			results[n] = getClosestIntersectionWitharea(data.anchors.get(i), data.anchors.get(i+1), point, direction);
			if(results[n]!=null) n++;
		}
		if(n==0) return null;
		
		float distSQ0 = Vector3f.sub(point, results[0]).length2();
		if(n==2 && Vector3f.sub(point, results[1]).length2()<distSQ0)return results[1];
		return results[0];
	}
	
	/**
	 * 
	 * @return null if no intersection, result else.
	 */
	private Vector3f getClosestIntersectionWitharea(TrackAnchor current, TrackAnchor next, Vector3f point, Vector3f direction){
		//intersection with a plane:
		float nDotDir = Vector3f.dot(current.getNormal(), direction);
		if(nDotDir == 0) return null; //TODO: check additionally for collision from side
		
		float lambda = (current.getnDotPos()-Vector3f.dot(current.getNormal(), point))/nDotDir;
		Vector3f pointInPlane = direction.duplicate();
		pointInPlane.scale(lambda);
		Vector3f.add(pointInPlane, point, pointInPlane);
		if(current.isPointInside(pointInPlane))	return pointInPlane;
		else return null;
	}
	
	private void initBarycentricTesting(){
		for(int i=1; i<data.anchors.size(); i++){
			data.anchors.get(i-1).initBaryentricData(data.anchors.get(i));
		}
		//System.out.println("[Curve.initBarycentricCTesting] initialized Curve "+this);
	}

	public static void setLastAnchor(TrackAnchor anchor) {
		lastAim = anchor;
	}
}