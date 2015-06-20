package rpEngine.graphical.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import utils.math.ListPrinter;
import utils.math.Vector3f;

/**
 * Support of Entity-Class for detailed collisiondetection.
 * @author joh
 * 
 * holds information about the objects faces.
 * If faces are smaller than a defined min-(/BLOCK-)Size, then the Data changes into a cube-map
 * (like from vectors to 3d-bitmap).
 * so big Faces are threatened in Detail, while smaller ones hopefully don't overflow the Stack 
 * 
 */
public class CollisionBox {
	public static final int BLOCK_SIZE = 5;
	
	private List<CollisionBox> components;
	private Vector3f center;
	/**squared max-size from center*/
	private float sizeSq;
	
	public CollisionBox(){
		components = new ArrayList();
	}
	
	public CollisionBox(Vector3f[] corners) {
		// TODO Auto-generated constructor stub
	}

	public CollisionEvent collidesWith(CollisionBox otherBox){
		return null;
	}
	
	
	public void add(Vector3f...corners){
		Face f = new Face(corners);
		if(f.size>BLOCK_SIZE){
			components.add(f);
		}
	}
	
	public float getSizeSq(){
		return sizeSq;
	}
	
	public Vector3f getCenter(){
		return center;
	}
	
	public float getDistanceTo(CollisionBox otherBox){
		//TODO
		return 0;
	}
	
	
	
	
	public void printSizes(){
		List<Integer> sizes = new LinkedList<>();
		for(CollisionBox c: components){
			sizes.add((int)c.getSizeSq());
		}
		ListPrinter.plot(sizes);
	}
	
	
	/**
	 * An area, defined by usually 3 points. (at least in TRIANGLE-mode)
	 * -> Vectorbased structure to store areas above BlockSize.
	 */
	public class Face extends CollisionBox{
		private List<Vector3f> corners;
		private Vector3f center;
		/** sq(largest distance from Center) */
		private int size;
		
		private Face(Vector3f...vector3fs){
			if(vector3fs.length==0) throw new java.lang.ExceptionInInitializerError("no empty Faces allowed. Please insert at least one parameter");
			corners= new ArrayList<>();
			addPoints(vector3fs);
		}
		private void addPoints(Vector3f...points){
			for(Vector3f point:points){
				corners.add(point);
			}
			calculateCenter();
		}
		private void calculateCenter(){
			center = new Vector3f(0,0,0);
			for(Vector3f corner: corners){
				center = Vector3f.add(center, corner);
			}
			center.scale(1/corners.size());
			//set Face-size
			size = Integer.MAX_VALUE;
			for(int i=0; i<corners.size(); i++){
				size = Math.min(size, (int) (Vector3f.sub(center, corners.get(i)).length2()*100));
			}
		}
	}
}