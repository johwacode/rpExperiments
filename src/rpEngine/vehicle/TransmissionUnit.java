package rpEngine.vehicle;


/**
 * 
 * @author joh
 *	Getriebe, Differential, ...
 */
public class TransmissionUnit extends VehicleComponent{
	private static final long serialVersionUID = -7028220949640427976L;

	private static final int R=21, N=22; //reservierte Gänge 
	
	private short currentGear;
	private float[] transmission; //effectiveTransmission = AxisTransmission(=[0]) * transmission[gear] * Wheels.radius

	/**
	 * @param weight in kg
	 * @param transmission float values for transmissions {AxisTransmission, 1.Gear, 2nd Gear, 3. Gear, ...}
	 */
	public TransmissionUnit(String id, int weight, float... transmission) {
		super(id);
		this.currentGear = N;
		this.transmission = transmission;
	}
	
	/**
	 * schaltet hoch: R->N->D
	 * @return war schalten möglich
	 */
	public boolean increaseGear(){
		if(currentGear==R){
			currentGear = N;
			return true;
		}
		if(currentGear==N){
			if(transmission.length>1){
				currentGear = 1;
				return true;
			}
			return false;
		}
		if(transmission.length>currentGear+1){
			currentGear += 1;
			return true;
		}
		return false;
	}
	
	public void releaseGear(){
		currentGear = N;
	}
	
	/**
	 * Antreiben
	 * @param rpm wie schnell
	 * @param force mit welcher Kraft
	 * @return Widerstand
	 */
	public float move(float force){
		if(currentGear!=N) return 1;
		return 0;
	}
	
	public String getCurrentGearAsString() {
		if(currentGear==R)return "R";
		if(currentGear==N)return "N";
		return ""+currentGear;
	}
}