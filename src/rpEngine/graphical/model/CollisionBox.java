package rpEngine.graphical.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import rpEngine.graphical.objects.Entity;
import rpEngine.graphical.objects.Sphere;
import utils.math.ListPrinter;
import utils.math.Maths;
import utils.math.Matrix4f;
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
	
	public static final float BLOCK_SIZE = 0.05f;
	
	private List<CollisionBox> components;
	protected Vector3f center;
	/**squared max-size from center*/
	protected float radiusSq;
	public State state;
	
	private static int lastID = 0;
	public int id;
	
	private CollisionBox(){
		components = new LinkedList<>();
		id = lastID++;
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

	/**
	 * detects Collisions with other CollisionBoxes in detail
	 * 
	 * @param otherBox //TODO: add flags for degree of detail
	 * @return CollisionEvent
	 */
	public CollisionEvent collidesWith(CollisionBox otherBox, Matrix4f transformationOfThis, Matrix4f transformationOfOther){
		CollisionEvent e = new CollisionEvent();
		return collidesWith(otherBox, transformationOfThis, transformationOfOther, e);
	}
	
	/**
	 * loops the CollisionDetection through the CollisionTree		</br>
	 * ------------------------------------------------------		</br>
	 * 																</br>
	 * !! to extend/override by subclasses !!						</br>
	 * [-> if(realCollision) add Data to CollisionEvent e;]			</br>
	 */
	private CollisionEvent collidesWith(CollisionBox otherBox, Matrix4f transformationOfThis, Matrix4f transformationOfOther, CollisionEvent e){
		if(intersectsNoChild(otherBox, transformationOfThis, transformationOfOther)){
			for(CollisionBox child: components){
				child.collidesWith(otherBox, transformationOfThis, transformationOfOther, e);
			}
		}
		return e;
	}
	
	/**
	 * returns intersection without caring for children. -> no details
	 * @param other CollisionBox
	 * @returns true, if 				  centerDistance  <  r1 + r2										<br/>
	 * 					<=>			   	  centerDistance² <  r1² + r2² + 2*r1*r2							<br/>
	 * 					<=> centerDistance² - (r1² + r2²) <  2*r1*r2										<br/>
	 * 	 				<=> (centerDistance² - r1²) - r2² <  2*r1*r2										<br/>
	 */
	public boolean intersectsNoChild(CollisionBox other, Matrix4f transformationOfThis, Matrix4f transformationOfOtherMatrix4f){
		//transform CenterPoints
		Vector3f c = this.getCenter();
		Vector3f centerThis = transformationOfThis.multiplyWithVector3fAndOne(c.x, c.y, c.z);
		c = other.getCenter();
		Vector3f centerOther = transformationOfThis.multiplyWithVector3fAndOne(c.x, c.y, c.z);
		
		//check (centerDistance² - r1²) - r2² <  2*r1*r2 stepwise
		float centerDistance = Vector3f.sub(centerThis, centerOther).length2();
		centerDistance -= this.getRadiusSq();
		if(centerDistance<0) return true;
		centerDistance -= other.getRadiusSq();
		if(centerDistance<0) return true;
		return (centerDistance < Math.sqrt(this.getRadiusSq()) * Math.sqrt(other.getRadiusSq()) * 2);
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
	
	public List<Entity> getRenderStuff(){
		List<CollisionBox> blocks = getRenderStuff(new LinkedList<CollisionBox>());
		List<Entity> entities = new LinkedList<>();
		for(CollisionBox box : blocks) entities.add(new Sphere(box.getCenter(), (float) Math.sqrt(BLOCK_SIZE)));
		return entities;
	}
	protected List<CollisionBox> getRenderStuff(List<CollisionBox> list){
		for(CollisionBox child: components) getRenderStuff(list);
		return list;
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
			radiusSq = Float.MAX_VALUE;
			for(int i=0; i<corners.size(); i++){
				radiusSq = Math.min(radiusSq, Vector3f.sub(center, corners.get(i)).length2());
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
		
		@Override
		public void addChild(CollisionBox smallerBox) {
			super.components.add(smallerBox);
		}
		
		@Override
		public void replaceChild(CollisionBox toReplace, CollisionBox newChild) {
			super.components.remove(toReplace);
			addChild(newChild);
		}
		
		@Override
		protected List<CollisionBox> getRenderStuff(List<CollisionBox> list){
			list.add(this);
			return list;
		}
		
	}

	public boolean hasChildren() {
		return !components.isEmpty();
	}
	
	public List<CollisionBox> getChildren(){
		return components;
	}
	
	public String printTree(int level){
		String tree = "";
		for(int i=0; i<level; i++){
			tree+=" -";
		}
		tree += "> "+ this.toString() + " (#" + id + ")" + System.lineSeparator();
		int blocks = 0;
		for(CollisionBox child : components){
			if(child.state==State.BLOCK) blocks++;
			else tree += child.printTree(level+1);
		}
		if(blocks>0){
			for(int i=0; i<=level; i++){
				tree+=" -";
			}
			tree += "> " + blocks + " Blocks (r=" + +Math.sqrt(BLOCK_SIZE) +")" + System.lineSeparator();
		}
		return tree;
	}
	
	@Override
	public String toString(){
		return "CollisionBox "+state+" c:"+center+" r:"+Math.sqrt(getRadiusSq())+" ~"+components.size()+" children";
	}
}