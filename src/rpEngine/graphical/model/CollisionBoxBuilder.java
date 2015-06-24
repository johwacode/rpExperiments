package rpEngine.graphical.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;
import utils.math.Maths;
import utils.math.Vector3f;

public class CollisionBoxBuilder {
	/**
	 * each known box represents a key. for each key there is a List, in which 
	 * the size is stored, that both Boxes would have, if they were combined with eachother.
	 */
	private Map<CollisionBox, List<Pair<CollisionBox, Float>>> combiningMap;
	private List<Float> valueList;
	
	public CollisionBoxBuilder(){
		combiningMap = new HashMap<>();
		valueList = new LinkedList<>();
	}
	
	public void addPart(Vector3f... corners){
		CollisionBox newBox = new CollisionBox(corners);
		
		for(CollisionBox key: combiningMap.keySet()){
			//TODO: check, whether Box equals already known block
		}
		 
		List<Pair<CollisionBox, Float>> ownList = new LinkedList<>();
		//update every old key
		for(CollisionBox key: combiningMap.keySet()){
			float size = getCombinedSize(key, newBox);
			combiningMap.get(key).add(new Pair<>(newBox, size));
			ownList.add(new Pair<>(key, size));
		}
		//add new Key
		combiningMap.put(newBox, ownList);
	}
	
	private float getCombinedSize(CollisionBox a, CollisionBox b){
		//get size of the bigger Box
		float biggerOne = Math.max(a.getRadiusSq(), b.getRadiusSq());
		//calc size between both centerPoints
		float centerDistance = Vector3f.sub(a.getCenter(), b.getCenter()).length2();
		//return either the biggerone - containing the other - or add the distance to the size of the smaller one.
		return Math.max(biggerOne , (Math.min(a.getRadiusSq(), b.getRadiusSq())+centerDistance));
	}
	
	
	public CollisionBox finalizeBox(){
		if(combiningMap.isEmpty()) throw new IllegalStateException("Box must not be empty!");
		while(combiningMap.size()>1){
			//search best pair a.k.a. smallest pair of Boxes.
			Collections.sort(valueList);
			float size = valueList.remove(0);
			//find min-sized-Pair
			CollisionBox key = null;
			Pair<CollisionBox, Float> partner = null;
			for(CollisionBox k: combiningMap.keySet()){
				for(Pair<CollisionBox, Float> pair : combiningMap.get(k)){
					if(Maths.floatEquals(pair.getValue(), size)){
						key = k;
						partner = pair;
						break;
					}
				}
				if(key!=null) break;
			}
			
			//combine both
			float combinedSize = partner.getValue();
			
			boolean isKeyBiggerThanPartner = key.getRadiusSq() > partner.getKey().getRadiusSq();
			CollisionBox biggerSphere = (isKeyBiggerThanPartner)? key : partner.getKey(),
						 smallerSphere = (isKeyBiggerThanPartner)? partner.getKey() : key;
			
			  //if no sphere contains the other one -> new bigger Box around both selected ones. 
			if(biggerSphere.getRadiusSq() + smallerSphere.getRadiusSq() > combinedSize){
				
				CollisionBox result = new CollisionBox(key, partner.getKey(), combinedSize, calcCenter(biggerSphere, smallerSphere, combinedSize));
				
				//remove old keys
				for(Pair<CollisionBox, Float> p : combiningMap.remove(biggerSphere)){
					valueList.remove(p.getValue());
				}
				for(Pair<CollisionBox, Float> p : combiningMap.remove(smallerSphere)){
					valueList.remove(p.getValue());
				}
				
				LinkedList<Pair<CollisionBox, Float>> resultsSizeList = new LinkedList<>();
				
				//remove old values, add new ones.
				for(CollisionBox k : combiningMap.keySet()){
					Iterator<Pair<CollisionBox, Float>> listIterator = combiningMap.get(k).iterator();
					int counter = 0;
				    while(counter<2 && listIterator.hasNext()){
				    	Pair<CollisionBox, Float> p = listIterator.next();
				    	if(p.getKey()==smallerSphere || p.getKey()==biggerSphere){
				    		valueList.remove(p.getValue());
				    		counter++;
				    		listIterator.remove();
				    	}
				    }
				    
				    //add combinedSize(key, result) into key's List and into result's one.
				    float keyPlusResultSize = getCombinedSize(k, result);
				    resultsSizeList.add(new Pair<CollisionBox, Float>(k, keyPlusResultSize));
				    combiningMap.get(k).add(new Pair<CollisionBox, Float>(result, keyPlusResultSize));
				    //add value twice, because of possibility of same-sized-object
				    valueList.add(keyPlusResultSize);
				    valueList.add(keyPlusResultSize);
				}
				
				combiningMap.put(result, resultsSizeList);
			}
			
			
			  //bigger sphere contains the smaller one -> add smaller box as child of the bigger one
			else{
				biggerSphere.addChild(smallerSphere);
				
				//remove deprecated key
				for(Pair<CollisionBox, Float> p : combiningMap.remove(smallerSphere)){
					valueList.remove(p.getValue());
				}
				
				//remove old values, add new ones.
				for(CollisionBox k : combiningMap.keySet()){
					Iterator<Pair<CollisionBox, Float>> listIterator = combiningMap.get(k).iterator();
				    while(listIterator.hasNext()){
				    	Pair<CollisionBox, Float> p = listIterator.next();
				    	if(p.getKey()==smallerSphere){
				    		valueList.remove(p.getValue());
				    		listIterator.remove();
				    		break;
				    	}
				    }
				}
			}
			
		}
		CollisionBox maxBox = (CollisionBox) combiningMap.keySet().toArray()[0];
		combiningMap = new HashMap<>();
		return maxBox;
	}

	private Vector3f calcCenter(CollisionBox biggerBox, CollisionBox smallerBox, float radiusSq) {
		Vector3f direction = Vector3f.sub(smallerBox.getCenter(), biggerBox.getCenter());
		direction.normalise();
		direction.scale((float) (Math.sqrt(radiusSq)-Math.sqrt(biggerBox.getRadiusSq())));
		return Vector3f.add(biggerBox.getCenter(), direction);
	}
}