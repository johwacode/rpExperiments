package rpEngine.vehicle;


public class Steering extends VehicleComponent{
	
	public Steering() {
		super(null, 0, null, null);
	}

	private float value=0;
	
	public float getValue(){
		return value;
	}
	
	public void turnLeft(float amount){
		value-=amount;
	}
	
	public void turnRight(float amount){
		value+=amount;
	}
	
	public void turnBack(){
		value = (Math.abs(value)>0.01)? 0.8f*value : 0;
	}
}
