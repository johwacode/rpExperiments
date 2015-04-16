package rpEngine.vehicle;

import java.util.ArrayList;
import java.util.List;

import rpEngine.graphical.objects.Entity;


public class Wheels extends VehicleComponent{
	private static final long serialVersionUID = 256644575824834963L;
	private int radius; //in mm
	private float gripFactor;
	
	/**
	 * @param width in mm
	 * @param aspectRatio side/width in %
	 * @param diameter in inches (as 18")
	 * (e.g. 235/55/R18) <br/>
	 * @param materialGrip [0.1(icy), 1.0(spiked chains)]
	 */
	public Wheels(int width, int aspectRatio, int diameter,
			float materialGrip) {
		super(width+"/"+aspectRatio+"R"+diameter+"\" G"+10*materialGrip);
		this.radius = (int) Math.round(diameter*12.7);
		this.gripFactor = width*materialGrip; //TODO: adjust this. 
	}
	
	public float move(float f) {
		//this.getVehicle().move();
		return 1;
	}

	public List<Entity> getModels() {
		List<Entity> list = new ArrayList<>();
		//TODO: add models
		return list;
	}
}
