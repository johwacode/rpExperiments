package rpEngine.vehicle;

import java.util.Random;

public class Engine extends VehicleComponent{
	private static final long serialVersionUID = 6856882182745023485L;
	private int maxPower;
	private int size;
	private int cylinders;
	private int torque; //(maxValue)
	private int weight;
	private int maxRpm; //Drehzahl
	private float currentRpm;
	private int maxTemperature;
	private float currentTemperature; //in °C
	private float smoothness = 1;
	public boolean running = false;
	
	private float fuelUp_tmp; 
	
	private Random rndGenerator = new Random();
	
	/**
	 * @param maxPower in kW
	 * @param size in cm³ (ccm)
	 * @param cylinders count (2,3,4,...12)
	 * @param torque - turningMoment in Nm (maxValue)
	 * @param maxRpm  in 1000/min
	 * @param maxTemperature in °C
	 * @param weight in kg
	 * @param smoothness [0.2(really rough); 1.0(perfect >RollsRoyce)]
	 */
	public Engine(String id, int maxPower, int size, int cylinders, int torque, int maxRpm,
			int maxTemperature, int weight, float smoothness) {
		super(id);
		this.maxPower = maxPower;
		this.size = size;
		this.cylinders = cylinders;
		this.torque = torque;
		this.maxRpm = maxRpm;
		this.currentRpm = 0;
		this.maxTemperature = maxTemperature;
		this.currentTemperature = 20;
		this.weight = weight;
		this.smoothness = smoothness;
	}
	
	public void turnOn(){
		if(running) return;
		running = true;
	}
	public void turnOff(){
		running=false;
	}
	
	/**
	 * "Gas geben"
	 * @param amount [0..1]
	 */
	public void fuel(float amount){
		if(!running) return;
		fuelUp_tmp += amount;
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
}
