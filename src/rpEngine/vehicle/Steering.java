package rpEngine.vehicle;


public class Steering extends VehicleComponent{
	private static final long serialVersionUID = -1620266358997169936L;
	float transmission;
	public Steering(float transmission) {
		super("steering"+transmission);
		this.transmission = transmission;
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
