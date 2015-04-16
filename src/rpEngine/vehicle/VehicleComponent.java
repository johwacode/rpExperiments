package rpEngine.vehicle;

import java.io.Serializable;

public abstract class VehicleComponent implements Serializable{
	private static final long serialVersionUID = -5894017944130559505L;
	public String id;
	VehicleComponent(String id){
		this.id = id;
	}
	
	public String toString(){
		return this.getClass().getSimpleName()+id;
	}
}