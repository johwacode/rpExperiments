package rpEngine.graphical.objects;

import rpEngine.graphical.model.Loader;
import rpEngine.graphical.model.Material;
import rpEngine.graphical.model.Model;
import rpEngine.graphical.model.Texture;
import rpEngine.graphical.structs.TrackAnchor;
import utils.math.Vector3f;

public class Curve extends Entity{
	private static Texture asphalt = new Texture(Loader.loadTexture(Material.ASPHALT, "asphalt", true));
	private static final float height = 0.4f;
	
	private TrackAnchor anchor, aim;

	public Curve(TrackAnchor anchor, float angle, float distance) {
		super(createModel(anchor, angle, distance), anchor.getPosition(), 0, 0, 0, 1);
		this.anchor = anchor;
	}
	
	private static Model createModel(TrackAnchor anchorStart, float angleXZ, float height, float distance){
		//cut into min 10 steps:
		int stepCount = (distance>10)? (int)Math.ceil(distance) : 10;
		float stepwidth = distance/stepCount; // ~=1, if distance>10
		float stepwidthY = height/stepCount;
		
		//init Vertex-Variables
		int ROWS = 5; //has to be odd
		float[] vertexArray = new float[stepCount*3*ROWS];
		Vector3f lastPos = anchorStart.getPosition();
		Vector3f centerPos;
		
		//init direction
		Vector3f direction = anchorStart.getDirection().duplicate();
		direction.normalise();
		direction.scale(stepwidth);
		
		//loop over all steps
		for(int i=0; i<stepCount; i++){
			//build main-point (center)
			direction.rotateXZ(angleXZ);
			direction.y += stepwidthY;
			Vector3f.add(lastPos, direction, centerPos);
			vertexArray[0] = centerPos.x;
			vertexArray[1] = centerPos.y;
			vertexArray[2] = centerPos.z;
			
			//add Vertices (from centerPos +- pitch)
			//TODO: add Vertices (from centerPos +- pitch) to Array
			
			
			lastPos = centerPos;
		}
		
		return new Model(Loader.loadEntityToVAO(vertexArray, textureCoords, normals, indices), asphalt);
	}
}