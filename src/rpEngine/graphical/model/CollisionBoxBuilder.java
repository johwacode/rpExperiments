package rpEngine.graphical.model;

import java.util.Iterator;
import java.util.List;

import rpEngine.graphical.model.CollisionBox.State;
import utils.math.Vector3f;

public class CollisionBoxBuilder {
	private CollisionBox root;
	private int maxDepth = 0;
	private int curDepth;
	
	
	public void addPart(Vector3f... corners){
		curDepth = 1;
		
		if(root==null) root = CollisionBox.create(corners);
		else if(insertInTree(CollisionBox.create(corners), root, null) &&curDepth>maxDepth) maxDepth=curDepth;
	}
	
	private boolean insertInTree(CollisionBox toInsert, CollisionBox comparing, CollisionBox parent){
		if(toInsert.equals(comparing)) return false; 
		curDepth++;
				
		//case: comparing contains toInsert
		if(contains(comparing, toInsert)) return insertSubNode(toInsert, comparing);
		
		//case: toInsert contains comparing
		else if(toInsert.state!=State.BLOCK && contains(toInsert, comparing)){
			toInsert.addChild(comparing);
			if(parent==null) root = toInsert;
			else parent.replaceChild(comparing, toInsert);
			return true;
		}
		
		//case: both exist next to each other (eventually colliding)
		else{
			//create container for both Elements
			CollisionBox container = createContainer(toInsert, comparing);
			//store result
			if(parent==null) root = container;
			else parent.replaceChild(comparing, container);
			return true;
		}
	}
	
	
	/**
	 * creates a container, that contains the given CollisionBoxes a and b.
	 * @return Container
	 */
	private CollisionBox createContainer(CollisionBox a, CollisionBox b){
		/* 
		 * calc size
		 * 			r  =  (r1 + r2 + distanceC1C2) /2
		 *  <=>		r =  ( sqrt(r1) + sqrt(r2) + length(c1-c2) )*0.5
		 *  <=>		r =  ( (sqrt(r1) + sqrt(r2) + length(c1-c2))*0.5 )²
		 */
		float centerDistance = Vector3f.sub(a.getCenter(), b.getCenter()).length();
		float radius = (float) ((Math.sqrt(a.getRadiusSq()) + Math.sqrt(b.getRadiusSq()) + centerDistance)*0.5);
		//create and return container
		return new CollisionBox(a, b, radius*radius +0.005f , calcCenter(a, b, radius));
	}
	
	/**
	 * checks, whether an outer CollisionBox contains an inner one.
	 * @returns true, if 				  outerR  >= centerDistance + innerR									<br/>
	 * 								<=>   outerR² >=  centerDistance² + 2*centerDistance*innerR + innerR²		<br/>
	 * 								<=>   outerR² >= (centerDistance² + innerR²) + 2*centerDistance*innerR		<br/>
	 * 	<=> outerR² - (centerDistance² + innerR²) >=  2*centerDistance*innerR									<br/>
	 */
	private boolean contains(CollisionBox outer, CollisionBox inner){
		float innerRSq = inner.getRadiusSq(),
			  outerRSq = outer.getRadiusSq();
		if(innerRSq > outerRSq) return false;
		float centerDistanceSq = Vector3f.sub(inner.getCenter(), outer.getCenter()).length2();
		outerRSq -= (innerRSq+centerDistanceSq);
		if(outerRSq < 0) return false;
		float delta = (float) (2* Math.sqrt(innerRSq) * Math.sqrt(centerDistanceSq));
		return outerRSq >= delta;
	}

	/**
	 * calculates the Center of a Containerbox
	 * @param boxA - one of the contained boxes
	 * @param boxB - the other container box
	 * @param radius - the calculated radius (not squared!) of the new container.
	 */
	private Vector3f calcCenter(CollisionBox boxA, CollisionBox boxB, float radius) {
		//decide, which box is the bigger one
		CollisionBox biggerBox, smallerBox;
		if(boxA.getRadiusSq() > boxB.getRadiusSq()){
			biggerBox = boxA; smallerBox = boxB;
		} else{
			biggerBox = boxB; smallerBox = boxA;
		}
		//get the direction from the biggerBox to the smaller one
		Vector3f direction = Vector3f.sub(smallerBox.getCenter(), biggerBox.getCenter());
		//scale the direction to the length of the containerRadius minus the biggerBox's one.
		direction.normalise();
		direction.scale((float) (radius-Math.sqrt(biggerBox.getRadiusSq())));
		//add the scaled result to the bigerBox's center.
		return Vector3f.add(biggerBox.getCenter(), direction);
	}
	
	
	
	private CollisionBox combineLowestXZ(List<CollisionBox> elements){
		CollisionBox[] lowestXZ = new CollisionBox[2];
		for(CollisionBox box: elements){
			if(lowestXZ[0]==null) lowestXZ[0] = box;
			else if(lowestXZ[0].getCenter().x>box.getCenter().x ||
				   (lowestXZ[0].getCenter().x==box.getCenter().x && 
					lowestXZ[0].getCenter().z>box.getCenter().z)){
							lowestXZ[1] = lowestXZ[0];
							lowestXZ[0] = box;
			}
			else {
				if(lowestXZ[1]==null) lowestXZ[1] = box;
				else if(lowestXZ[1].getCenter().x>box.getCenter().x ||
					   (lowestXZ[1].getCenter().x==box.getCenter().x && 
						lowestXZ[1].getCenter().z>box.getCenter().z)){
								lowestXZ[1] = box;
				}
			}
		}
		return createContainer(lowestXZ[0], lowestXZ[1]);
	}
	
	private boolean insertSubNode(CollisionBox toInsert, CollisionBox containing){
		if(!containing.hasChildren()){
			containing.addChild(toInsert);
			return true;
		}
		else{
			Iterator<CollisionBox> itr;
			if(toInsert.state!=State.BLOCK){
				//Does toInsert contain any of comparings children?
				itr = containing.getChildren().iterator();
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
					containing.addChild(toInsert);
					return true;
				}
			}
			//does a child of comparing contain toInsert?
			itr = containing.getChildren().iterator();
			while(itr.hasNext()){
				CollisionBox current = itr.next();
				if(contains(current, toInsert)){
					if(toInsert.equals(containing)) return false;
					else{
						curDepth++;
						return insertSubNode(toInsert, current);
					}
				}
			}
			//if no child-interaction: toInsert becomes a new child.
			containing.addChild(toInsert);
			
			//but: if to many children: split them up.
			if(containing.getChildren().size() > 3){
				splitNode(containing);
			}	
			return true;
		}
	}
	

	/** TODO!!!!!
	 * should split up a Node so that there a not to many children in the same one.
	 * sincerely it doesn't work well
	 * -> often removes all of the children and so creates only cons. 
	 */
	private void splitNode(CollisionBox comparing){
		CollisionBox container = combineLowestXZ(comparing.getChildren());
		// move each box that is contained in container into it.
		Iterator<CollisionBox> itr = comparing.getChildren().iterator();
		while(itr.hasNext()){
			CollisionBox current = itr.next();
			
			if(contains(container, current)){
				container.addChild(current);
				itr.remove();
			}
		}
		if(comparing.hasChildren()) comparing.addChild(container);
		else System.out.println("gnaaaaaarrrrrffff!");
	}
	

	public CollisionBox finalizeBox() {
		CollisionBox result = root;
		root = null;
		//debugStuff
		System.out.println(System.lineSeparator() + "Tree ready. size: " + result.getWeight() + ", depth: " + maxDepth);
		System.out.println("======TREE:========");
		System.out.println(result.printTree(0));
		return result;
	}
}