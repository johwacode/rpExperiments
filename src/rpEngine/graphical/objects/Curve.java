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
	
	private static TrackAnchor lastAim;
	
	private TrackAnchor anchor, aim;

	public Curve(TrackAnchor anchor, float angleXZ, float height, float distance, float pitch) {
		super(createModel(anchor, angleXZ, height, distance, pitch), anchor.getPosition(), 0, 0, 0, 1);	
		this.anchor = anchor;
		this.aim = lastAim;
	}
	
	private static Model createModel(TrackAnchor anchorStart, float angleXZ, float height, float distance, float pitch){
		//cut into min 10 steps:
		int stepCount = (distance>10)? (int)Math.ceil(distance) : 10;
		float stepwidth = distance/stepCount; // ~=1, if distance>10
		float stepwidthY = height/stepCount;
		float stepwidthPitch = pitch/stepCount;
		
		//init Vertex-Variables
		int ROWS = 5; //has to be odd
		int rowsPerSide = ROWS/2;
		System.out.println("per line: "+rowsPerSide+" - center - "+rowsPerSide);
		float[] vertexArray = new float[stepCount*3*ROWS];
		Vector3f lastPos = anchorStart.getPosition();
		Vector3f centerPos= new Vector3f();
		
		float[] normals = new float[vertexArray.length];
		float[] textureCoords = new float[stepCount*2*ROWS];
		
		
		//init direction
		Vector3f direction = anchorStart.getDirection().duplicate();
		direction.normalise();
		direction.scale(stepwidth);
		
		Vector3f curPitch = anchorStart.getPitch().duplicate();
		
		int vertexNumber = 0;
		
		//loop over all steps (plus first Step at i=0 -> connect to AnchorPoint)
		for(int i=0; i<=stepCount; i++){
			if(i>1){
				//build main-point (center)
				direction.rotateXZ(angleXZ);
				direction.y += stepwidthY;
				Vector3f.add(lastPos, direction, centerPos);
				
				//calc pitch
				curPitch.rotateXZ(angleXZ);
				curPitch.y += stepwidthPitch;
			}
			
			
			//add Vertices (from centerPos +- pitch)
			for(int row=-rowsPerSide; row<=rowsPerSide; i++){
				vertexNumber = (i+row+rowsPerSide)*3;
				vertexArray[vertexNumber] = centerPos.x + row*curPitch.x;
				vertexArray[vertexNumber+1] = centerPos.y + row*curPitch.y;
				vertexArray[vertexNumber+2] = centerPos.z + row*curPitch.z;
				
				//TODO: calc real normals -> x,z determined by direction
				normals[vertexNumber] = 0;
				normals[vertexNumber+1] = 1;
				normals[vertexNumber+2] = 0;
				
				vertexNumber = (i+row+rowsPerSide)*2;
				switch(i%4){
				case 0: textureCoords[vertexNumber] = 0;
					textureCoords[vertexNumber+1] = 1;
					break;
				case 1: textureCoords[vertexNumber] = 1;
					textureCoords[vertexNumber+1] = 1;
					break;
				case 2: textureCoords[vertexNumber] = 1;
					textureCoords[vertexNumber+1] = 0;
					break;
				case 3: textureCoords[vertexNumber] = 0;
					textureCoords[vertexNumber+1] = 0;
					break;
				}
			}
			
			
			lastPos = centerPos;
		}
		
		/*
		 * n rows with m vertices each
		 * -> (n-1)(m-1) triangleLines for right-hand-triangles, equally left-hand-ones
		 * --> *2 -> *3points per triangle
		 */
		int[] indices = new int[6*(stepCount-1)*(ROWS-1)];
		int i = 0;
		int currentVertex;
		for(int stripe=0; stripe<stepCount-1; stripe++){
			for(int quad=0; quad<ROWS-1; quad++){
				//indices for 2 triangles (~ one quad)
				currentVertex = stripe*ROWS+quad;
				indices[i+0] = currentVertex;
				indices[i+1] = currentVertex+1;
				indices[i+2] = currentVertex+ROWS;
				
				indices[i+3] = currentVertex+1;
				indices[i+4] = currentVertex+ROWS+1;
				indices[i+5] = currentVertex+ROWS;
				
				i+=6;
			}
		}
		
		
		//store the aimed Anchor.
		Vector3f aimPosition = Vector3f.add(anchorStart.getPosition(), centerPos);
		lastAim = new TrackAnchor(aimPosition, direction, curPitch);
		
		return new Model(Loader.loadEntityToVAO(vertexArray, textureCoords, normals, indices, distance), asphalt);
	}
}