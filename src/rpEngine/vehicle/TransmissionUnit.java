package rpEngine.vehicle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import utils.math.Vector3f;

/**
 * 
 * @author joh
 *	Getriebe, Differential, ...
 */
public class TransmissionUnit extends VehicleComponent{
	private static final int R=0, N=20; //reservierte Gänge 
	
	private short currentGear;
	private float[] transmission;
	
	private List<Wheel> wheels;

	public TransmissionUnit(Vector3f position, int weight,
			float[] transmission) {
		super(position, weight, null, null);
		this.currentGear = N;
		this.transmission = transmission;
		wheels = new ArrayList<>();
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
		if(currentGear!=N)
			try{
				float resistanceFactor = transmission[currentGear];
				float resultingForce = force * transmission[currentGear];
				float resistanceOutput = 0;
				
				//Widerstände der einzelnen Räder berechnen
				List<Float> resistances = new LinkedList<>();
				for(Wheel wheel: wheels){
					resistances.add(wheel.getResistance());
				}
				float sumR = 0;
				for(float r : resistances){
					sumR += r;
				}
				float splittedForce = resultingForce/wheels.size();
				//Räder antreiben
				for(int i=0; i<wheels.size(); i++){
					//anteilForce = 1/R; anteilR = (resistances.get(i)/sumR);
					float rResult = wheels.get(i).move(sumR/resistances.get(i) * splittedForce);
					resistanceOutput += rResult;
				}
				resistanceOutput*=resistanceFactor;
				
				return resistanceOutput;
			}catch(ArithmeticException e){
			}
		return 0;
	}
	
	public void addWheel(Wheel wheel){
		wheels.add(wheel);
	}

	public String getCurrentGearAsString() {
		if(currentGear==R)return "R";
		if(currentGear==N)return "N";
		return ""+currentGear;
	}
}