package rpEngine.graphical.model;

import java.util.Iterator;

import utils.math.Vector3f;

public class CollisionBoxBuilder {
	private CollisionBox root;
	private int weight = 0;
	private int maxDepth = 0;
	private int curDepth;
	
	
	public void addPart(Vector3f... corners){
		curDepth = 0;
		
		if(root==null) root = CollisionBox.create(corners);
		else if(insertInTree(CollisionBox.create(corners), root, null) &&curDepth>maxDepth) maxDepth=curDepth;
		
		weight = root.getWeight();
		System.out.println("Part inserted. new tree-size: " + weight + ", depth: " + maxDepth);
		if(weight>8000)
			System.out.println("stop");
	}
	
	private boolean insertInTree(CollisionBox toInsert, CollisionBox comparing, CollisionBox parent){
		if(toInsert.equals(comparing)) return false;
		if(toInsert.state==CollisionBox.State.BLOCK && comparing.state==CollisionBox.State.BLOCK)
			System.out.println("watch here");//TODO: doesn't happen, but still some identically Blocks contain each other. WHY?! 
		curDepth++;
		//case: toInsert contains other
		if(contains(toInsert, comparing)){
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
				//Does toInsert contain any of comparings children?
				Iterator<CollisionBox> itr = comparing.getChildren().iterator();
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
			//decide, which box is the bigger one
			CollisionBox bigger, smaller;
			if(toInsert.getRadiusSq() > comparing.getRadiusSq()){
				bigger = toInsert; smaller = comparing;
			} else{
				bigger = comparing; smaller = toInsert;
			}
			//calc size
			float centerDistance = Vector3f.sub(bigger.getCenter(), smaller.getCenter()).length2();
			if(centerDistance < bigger.getRadiusSq()) centerDistance -= (bigger.getRadiusSq()-centerDistance); //case of overlapping
			float radiusSq = bigger.getRadiusSq() + centerDistance + smaller.radiusSq;
			//create container
			CollisionBox container = new CollisionBox(toInsert, comparing, radiusSq, calcCenter(bigger, smaller, radiusSq));
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

	private Vector3f calcCenter(CollisionBox biggerBox, CollisionBox smallerBox, float radiusSq) {
		Vector3f direction = Vector3f.sub(smallerBox.getCenter(), biggerBox.getCenter());
		direction.normalise();
		direction.scale((float) (Math.sqrt(radiusSq)-Math.sqrt(biggerBox.getRadiusSq())));
		return Vector3f.add(biggerBox.getCenter(), direction);
	}

	public CollisionBox finalizeBox() {
		CollisionBox result = root;
		root = null;
		return result;
	}
}