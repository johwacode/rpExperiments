package rpEngine.vehicle;

import java.util.Random;

import utils.math.Vector3f;

public class Engine extends VehicleComponent implements Runnable{
	private TransmissionUnit transmissionUnit;

	private int maxPower;
	private int size;
	private int maxRpm; //Drehzahl
	private float currentRpm;
	private int maxTemperature;
	private float currentTemperature; //in °C
	private int smoothness = 1; //Laufruhe in Aussetzern je 1000 Umdrehungen
	public boolean running = false;
	private Thread engineThread;
	
	private float fuelUp_tmp; 
	
	private Random rndGenerator = new Random();
	
	public Engine(Vector3f position, int weight, Vector3f aeroDragFront,
			Vector3f aeroDragSide, int maxPower, int size, int maxRpm,
			int maxTemperature,
			int smoothness) {
		super(position, weight, aeroDragFront, aeroDragSide);
		this.maxPower = maxPower;
		this.size = size;
		this.maxRpm = maxRpm;
		this.currentRpm = 0;
		this.maxTemperature = maxTemperature;
		this.currentTemperature = 20;
		this.smoothness = smoothness;
	}
	
	public void turnOn(){
		if(running) return;
		engineThread = new Thread(this);
		running = true;
		engineThread.start();
	}
	public void turnOff(){
		running=false;
	}
	
	public void run(){
		final int REFRESHRATE = 30;
		while(running){
			///////
			//Leistung = M * Omega
			//==2Pi * M * Drehzahl
			//==2Pi * F * r * f
			//==2Pi*r * F * f
			//-> F = P / (2*Pi * FREQUENCY)
			float newMoment = gaussian(maxPower*(5*fuelUp_tmp+1) / 0.63f, 0.2f); //~ (2*2pi*10)
			fuelUp_tmp = 0;
			mixImpulseWithCurrent(newMoment);
			
			try{
				//if(transmissionUnit==null)transmissionUnit = getVehicle().getTransmissionUnit();
				float resistance = transmissionUnit.move(currentRpm);
				
				currentRpm -= resistance;
				
			} catch( NullPointerException e){
				e.printStackTrace();
			}
			
				
				
			try {
				Thread.sleep(REFRESHRATE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//slow down.
		while(currentRpm>20){
			mixImpulseWithCurrent(0);
		}
		currentRpm = 0;
	}
	
	private void mixImpulseWithCurrent(float newMoment){
		float oldMoment = currentRpm/2f;
		currentRpm=(0.3f*newMoment+0.7f*oldMoment)*2f;
	}
	
	/**
	 * "Gas geben"
	 * @param amount [0..1]
	 */
	public void fuel(float amount){
		if(!running) return;
		fuelUp_tmp += amount;
	}

	public int getMaxPower() {
		return maxPower;
	}

	public int getSize() {
		return size;
	}

	public int getMaxRpm() {
		return maxRpm;
	}

	public float getCurrentRpm() {
		return currentRpm;
	}

	public int getMaxTemperature() {
		return maxTemperature;
	}

	public float getCurrentTemperature() {
		return currentTemperature;
	}
	
	private float gaussian(float value, float stretch){
		return (float) rndGenerator.nextGaussian()*stretch+value;
	}
}
