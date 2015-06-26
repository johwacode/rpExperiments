package rpEngine.graphical.model;

import java.util.Iterator;

import rpEngine.graphical.model.CollisionBox.State;
import utils.math.Vector3f;

public class CollisionBoxBuilder {
	private CollisionBox root;
	private int maxDepth = 0;
	private int curDepth;
	
	
	public void addPart(Vector3f... corners){
		curDepth = 0;
		
		if(root==null) root = CollisionBox.create(corners);
		else if(insertInTree(CollisionBox.create(corners), root, null) &&curDepth>maxDepth) maxDepth=curDepth;
	}
	
	private boolean insertInTree(CollisionBox toInsert, CollisionBox comparing, CollisionBox parent){
		if(toInsert.equals(comparing)) return false;
		if(toInsert.state==CollisionBox.State.BLOCK && comparing.state==CollisionBox.State.BLOCK)
			System.out.println("watch here");//TODO: doesn't happen, but still some identically Blocks contain each other. WHY?! 
		curDepth++;
		//case: toInsert contains other
		if(toInsert.state!=State.BLOCK && contains(toInsert, comparing)){
			toInsert.addChild(comparing);
			if(parent==null) root = toInsert;
			else parent.replaceChild(comparing, toInsert);
			return true;
		}
		
		//case: other contains toInsert
		else if(contains(comparing, toInsert)){
			if(!comparing.hasChildren()){
				comparing.addChild(toInsert);
				return true;
			}
			else{
				Iterator<CollisionBox> itr;
				if(toInsert.state!=State.BLOCK){
					//Does toInsert contain any of comparings children?
					itr = comparing.getChildren().iterator();
					boolean foundContained = false;
					while(itr.hasNext()){
						CollisionBox current = itr.next();
						
						if(contains(toInsert, current)){
							foundContained = true;
							toInsert.addChild(current);
							itr.remove();
						}
					}
					if(foundContained){
						comparing.addChild(toInsert);
						return true;
					}
				}
				//does a child of comparing contain toInsert?
				itr = comparing.getChildren().iterator();
				while(itr.hasNext()){
					CollisionBox current = itr.next();
					if(contains(current, toInsert))
						return insertInTree(toInsert, current, comparing);
				}
				//if no child-interaction: toInsert becomes a new child.
				comparing.addChild(toInsert);
				return true;
			}
		}
		
		//case: both exist next to each other (eventually colliding)
		else{
			//calc size
			float centerDistance = Vector3f.sub(toInsert.getCenter(), comparing.getCenter()).length();
			float radius = (float) ((Math.sqrt(toInsert.getRadiusSq()) + centerDistance + Math.sqrt(comparing.getRadiusSq()))*0.5);
			//create container
			CollisionBox container = new CollisionBox(toInsert, comparing, radius*radius, calcCenter(toInsert, comparing, radius));
			//store result
			if(parent==null) root = container;
			else parent.replaceChild(comparing, container);
			
			return true;
		}
	}
	
	/**
	 * checks, whether an outer CollisionBox contains an inner one.
	 */
	private boolean contains(CollisionBox outer, CollisionBox inner){
		float innerR = inner.getRadiusSq(),
			  outerR = outer.getRadiusSq();
		if(innerR > outerR) return false;
		float centerDistance = Vector3f.sub(inner.getCenter(), outer.getCenter()).length2();
		outerR -= (innerR+centerDistance);
		if(outerR < 0) return false;
		float delta = (float) (2* Math.sqrt(innerR) * Math.sqrt(centerDistance));
		return outerR >= delta;
	}

	private Vector3f calcCenter(CollisionBox boxA, CollisionBox boxB, float radius) {
		//decide, which box is the bigger one
		CollisionBox biggerBox, smallerBox;
		if(boxA.getRadiusSq() > boxB.getRadiusSq()){
			biggerBox = boxA; smallerBox = boxB;
		} else{
			biggerBox = boxB; smallerBox = boxA;
		}
		Vector3f direction = Vector3f.sub(smallerBox.getCenter(), biggerBox.getCenter());
		direction.normalise();
		direction.scale((float) (radius-Math.sqrt(biggerBox.getRadiusSq())));
		return Vector3f.add(biggerBox.getCenter(), direction);
	}

	public CollisionBox finalizeBox() {
		CollisionBox result = root;
		root = null;
		//debugStuff
		System.out.println("Part inserted. new tree-size: " + result.getWeight() + ", depth: " + maxDepth);
		System.out.println("======TREE:========");
		System.out.println(result.printKey(0));
		return result;
	}
}