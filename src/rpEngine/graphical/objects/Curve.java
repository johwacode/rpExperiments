package rpEngine.graphical.objects;

import game.SceneGraph;
import rpEngine.graphical.model.Loader;
import rpEngine.graphical.model.Material;
import rpEngine.graphical.model.Model;
import rpEngine.graphical.model.Texture;
import rpEngine.graphical.structs.TrackAnchor;
import utils.math.Vector3f;

/**
 * serializable Entity out of which Tracks can be built. 
 * defined by a start-anchor, XY-angle, distance, pitch, height, ... 
 * @author joh
 *
 */
public class Curve extends Entity{
	private static Texture asphalt = new Texture(Loader.loadTexture(Material.ASPHALT, "asphalt", true));
	private static final float height = 0.4f;
	private static int ROWS = 11; //has to be odd (Or even(but odd) prim?!)
	
	private static TrackAnchor lastAim;
	
	protected TrackAnchor anchor, aim;

	private Curve(Model model, Vector3f position){
		super(model, position, 0, 0, 0, 1);
	}
	
	public static Preview createPreview(TrackAnchor anchor, float angleXZ, float height,
			float distance, float pitch){
		return new Preview(anchor, angleXZ, height, distance, pitch);
	}
	
	public static TrackAnchor getLastAnchor(){
		return lastAim;
	}
	
	public TrackAnchor getAim(){
		return aim;
	}
	
	
	/**
	 * The Preview-Class of Curves
	 * Curve with a different Texture and additional Attribs for easier Converting into a real curve.
	 * There should always be max 1 Instance at a time (~singleton)
	 * @author joh
	 *
	 */
	public static class Preview extends Curve{
		private static Texture texture = new Texture(Loader.loadTexture(Material.WATER, "surface_water3", true));
		
		private static TrackAnchor previewAim;
		
		private Preview(TrackAnchor anchor, float angleXZ, float height,
				float distance, float pitch) {
			super(createModel(anchor, angleXZ, height, distance, pitch, texture), anchor.getPosition());	
			this.anchor = anchor;
			this.aim = previewAim;
		}
		
		public Curve buildCurve(){
			//calc length(only correct for very small angles)
			Vector3f distVec = new Vector3f();
			Vector3f.sub(anchor.getPosition(), aim.getPosition(), distVec);
			float distance = distVec.length();
			//get model
			Model model = this.getModel();
			model.setTexture(asphalt);
			lastAim = previewAim;
			//get curve
			return new Curve(model, getPosition());
		}
		
		private static Model createModel(TrackAnchor anchorStart, float angleXZ, float height, float distance, float pitch, Texture texture){
			if(anchorStart==null) throw new IllegalStateException("No AnchorPoint defined!");
			//cut into min 10 steps:
			int stepCount = (distance>10)? (int)Math.ceil(distance) : 10;
			float stepwidth = distance/stepCount; // ~=1, if distance>10
			float stepwidthY = height/stepCount;
			float stepwidthPitch = pitch/stepCount;
			
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
					direction.rotateXZ(angleXZ);
					direction.y += stepwidthY;
					Vector3f.add(lastPos, direction, centerPos);
					
					//calc pitch
					curPitch.rotateXZ(angleXZ);
					curPitch.y += stepwidthPitch;
				}
				
				
				//add Vertices (from centerPos +- pitch)
				for(int row=-rowsPerSide; row<=rowsPerSide; row++){
					vertexArray[vertexNumber] = centerPos.x + row*curPitch.x;
					vertexArray[vertexNumber+1] = centerPos.y + row*curPitch.y;
					vertexArray[vertexNumber+2] = centerPos.z + row*curPitch.z;
					
					/*addDebugSphere(vertexArray[vertexNumber],
							vertexArray[vertexNumber+1],
							vertexArray[vertexNumber+2]
							, anchorStart.getPosition());
					*/
					
					//TODO: calc real normals -> x,z determined by direction
					normals[vertexNumber] = 0;
					normals[vertexNumber+1] = 1;
					normals[vertexNumber+2] = 0;
					
					switch((texNumber/2)%4){
					case 0: textureCoords[texNumber] = 0;
						textureCoords[texNumber+1] = 0;
						break;
					case 1: textureCoords[texNumber] = 0;
						textureCoords[texNumber+1] = 1;
						break;
					case 2: textureCoords[texNumber] = 1;
						textureCoords[texNumber+1] = 1;
						break;
					case 3: textureCoords[texNumber] = 1;
						textureCoords[texNumber+1] = 0;
						break;
					}
					
					vertexNumber += 3;
					texNumber += 2;
				}
				
				
				lastPos = centerPos;
			}
			
			/*
			 * n rows with m vertices each
			 * -> (n-1)(m-1) triangleLines for right-hand-triangles, equally left-hand-ones
			 * --> *2 -> *3points per triangle
			 */
			int[] indices = new int[6*(stepCount)*(ROWS-1)];
			int i = 0;
			int currentVertex;
			for(int stripe=0; stripe<stepCount; stripe++){
				for(int quad=0; quad<ROWS-1; quad++){
					
					//indices for 2 triangles (~ one quad)
					currentVertex = stripe*ROWS+quad;
					indices[i+0] = currentVertex;
					indices[i+1] = currentVertex+ROWS;
					indices[i+2] = currentVertex+1;
					
					indices[i+3] = currentVertex+ROWS;
					indices[i+4] = currentVertex+ROWS+1;
					indices[i+5] = currentVertex+1;
					
					i+=6;
				}
			}
			
			
			//store the aimed Anchor.
			Vector3f aimPosition = Vector3f.add(anchorStart.getPosition(), centerPos);
			previewAim = new TrackAnchor(aimPosition, direction, curPitch);
			
			return new Model(Loader.loadEntityToVAO(vertexArray, textureCoords, normals, indices, distance), texture);
		}
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