package rpEngine.graphical.model;

import java.util.LinkedList;
import java.util.List;

import utils.math.Vector3f;

public class CollisionEvent {
	private List<CollisionBox> collidingBoxes;
	private List<Vector3f> collisionSpots;
	
	public CollisionEvent(){
		collidingBoxes = new LinkedList<>();
		collisionSpots = new LinkedList<>();
	}
	
	public void addData(CollisionBox collisionBox){
		collidingBoxes.add(collisionBox);
	}
	public void addData(Vector3f collisionSpot){
		collisionSpots.add(collisionSpot);
	}
	
	//....
}
