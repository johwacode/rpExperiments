package rpEngine.graphical.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;
import utils.math.Vector3f;

public class CollisionBoxBuilder {
	/**
	 * each known box represents a key. for each key there is a List, in which 
	 * the size is stored, that both Boxes would have, if they were combined with eachother.
	 */
	private Map<CollisionBox, List<Pair<CollisionBox, Float>>> combiningMap;
	
	public CollisionBoxBuilder(){
		combiningMap = new HashMap<>();
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
		float biggerOne = Math.max(a.getSizeSq(), b.getSizeSq());
		//calc size between both centerPoints
		float centerDistance = Vector3f.sub(a.getCenter(), b.getCenter()).length2();
		//return either the biggerone - containing the other - or add the distance to the size of the smaller one.
		return Math.max(biggerOne , (Math.min(a.getSizeSq(), b.getSizeSq())+centerDistance));
	}
	
	public CollisionBox finalizeBox(){
		
		
		
		
		
		return null;
	}
}
