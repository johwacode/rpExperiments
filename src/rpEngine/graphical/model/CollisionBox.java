package rpEngine.graphical.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import utils.math.ListPrinter;
import utils.math.Maths;
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
	public static enum State{FACE, BLOCK, CONTAINER}; 
	
	public static final int BLOCK_SIZE = 15;
	
	private List<CollisionBox> components;
	protected Vector3f center;
	/**squared max-size from center*/
	protected float radiusSq;
	public State state;
	
	private CollisionBox(){
		components = new LinkedList<>();
	}
	
	public static CollisionBox create(Vector3f[] corners) {
		CollisionBox box = new Face(corners);
		if(box.getRadiusSq()>BLOCK_SIZE) return box;
		else return new BlockBox(corners);
	}

	public CollisionBox(CollisionBox childOne, CollisionBox childTwo, float radiusSq, Vector3f center) {
		this();
		components.add(childOne);
		components.add(childTwo);
		this.radiusSq= radiusSq;
		this.center = center;
		state = State.CONTAINER;
	}

	public CollisionEvent collidesWith(CollisionBox otherBox){
		return null;
	}
	
	
	public float getRadiusSq(){
		return radiusSq;
	}
	
	public Vector3f getCenter(){
		return center;
	}
	
	
	public void printSizes(){
		List<Integer> sizes = new LinkedList<>();
		for(CollisionBox c: components){
			sizes.add((int)c.getRadiusSq());
		}
		ListPrinter.plot(sizes);
	}
	
	
	public void addChild(CollisionBox smallerBox) {
		components.add(smallerBox);
	}
	
	public void replaceChild(CollisionBox toReplace, CollisionBox newChild) {
		components.remove(toReplace);
		addChild(newChild);
	}
	
	/**
	 * count of it's children and their children +1 (itself)
	 */
	public int getWeight(){
		int weight = 1;
		for(CollisionBox child: components) weight += child.getWeight();
		return weight;
	}
	
	@Override
	public boolean equals(Object object){
		if(!(object instanceof CollisionBox)) return false;
		return super.equals(object) || (
				this.state==((CollisionBox) object).state &&
				Maths.floatEquals(((CollisionBox) object).getRadiusSq(), this.getRadiusSq()) &&
				((CollisionBox) object).getCenter().equals(this.getCenter()));
	}
	
	
	/**
	 * An area, defined by usually 3 points. (at least in TRIANGLE-mode)
	 * -> Vectorbased structure to store areas above BlockSize.
	 */
	public static class Face extends CollisionBox{
		private List<Vector3f> corners;
		
		private Face(Vector3f...vector3fs){
			if(vector3fs.length==0) throw new java.lang.ExceptionInInitializerError("no empty Faces allowed. Please insert at least one parameter");
			corners= new ArrayList<>();
			addPoints(vector3fs);
			state = State.FACE;
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
			center.scale(1.0f/corners.size());
			//set Face-size
			radiusSq = Integer.MAX_VALUE;
			for(int i=0; i<corners.size(); i++){
				radiusSq = Math.min(radiusSq, (int) (Vector3f.sub(center, corners.get(i)).length2()*100));
			}
		}
	}
	
	public static class BlockBox extends CollisionBox{
		private BlockBox(Vector3f...vector3fs){
			float x = vector3fs[0].x,
				y = vector3fs[0].y,
				z = vector3fs[0].z;
			//TODO: test, whether /2 is close enough to not ignore points on the edge.
			//else: use all of the points and arithmetically calulate center.
			x -= x%(BLOCK_SIZE/2);
			y -= y%(BLOCK_SIZE/2);
			z -= z%(BLOCK_SIZE/2);
			center = new Vector3f(x,y,z);
			
			state = State.BLOCK;
		}
		
		@Override
		public float getRadiusSq(){
			return BLOCK_SIZE;
		}
	}

	public boolean hasChildren() {
		return !components.isEmpty();
	}
	
	public List<CollisionBox> getChildren(){
		return components;
	}
	
	@Override
	public String toString(){
		return "CollisionBox "+state+" c:"+center+" r²:"+getRadiusSq()+" ~"+components.size()+" children";
	}
}