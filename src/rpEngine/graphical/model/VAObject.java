package rpEngine.graphical.model;

public class VAObject {
	private int id, count;
	private CollisionBox collisionBox;
	
	public VAObject(int id, int count){
		this.id = id;
		this.count = count;
	}
	
	public VAObject(int id, int count, CollisionBox collisionBox){
		this(id, count);
		this.collisionBox = collisionBox;
	}

	public int getId() {
		return id;
	}

	public int getVertexCount() {
		return count;
	}
	
	public boolean hasCollisionBox(){
		return collisionBox==null;
	}
}
