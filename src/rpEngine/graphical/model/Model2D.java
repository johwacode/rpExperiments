package rpEngine.graphical.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.math.Vector2f;

public class Model2D extends Model {
	private Map<Long, Vector2f> positions;
	public long nextId = Long.MIN_VALUE;
		
	public Model2D(VAObject vao, Texture texture) {
		super(vao, texture);
		positions = new HashMap<>();
	}
	
	public Model2D(VAObject vao, Texture texture, Vector2f firstPosition) {
		super(vao, texture);
		positions = new HashMap<>();
		addPosition(firstPosition);
	}

	public Collection<Vector2f> getPositions(){
		return positions.values();
	}
	
	public long addPosition(Vector2f newPosition){
		long id = nextId++;
		positions.put(id, newPosition);
		return id;
	}
	
	public long addPosition(float x, float y){
		return addPosition(new Vector2f(x, y));
	}
	
	public void replacePosition(long id, float x, float y){
		positions.replace(id, new Vector2f(x, y));
	}
	
	public void removePosition(long id){
		positions.remove(id);
	}
	
	public void removePositions(List<Long> ids){
		System.out.println("positionCount before: "+positions.size());
		for(long id : ids) removePosition(id);
		System.out.println("deleted "+ids.size()+" items.");
		System.out.println("positionCount after: "+positions.size());
	}
	
	public void clearPositions(){
		positions.clear();
	}
}
