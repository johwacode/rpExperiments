package rpEngine.vehicle;

import rpEngine.graphical.model.Model;
import rpEngine.graphical.objects.Entity;
import utils.math.Vector3f;

public class Chassis extends VehicleComponent{
	private static final long serialVersionUID = -906262156728325867L;
	private float width, height, length, weight, aeroDragFront, aeroDragSide, centerOfMass;
	private String modelFilename, textureFilename;
	
	/**
	 * @param width  in m
	 * @param height in m
	 * @param length in m
	 * @param weight in kg
	 * @param aeroDragFront [0.01(an insect), 1.0(beton wall)]
	 * @param aeroDragSide [0.01(an insect), 1.0(beton wall)]
	 * @param centerOfMass height of the central MassPoint [0.0(directly On the Street), 1.0(on the very top of the roof*)] <br/>
	 * *maybe a really heavy antenna?
	 * @param model
	 */
	public Chassis(String id, float width, float height, float length, float weight,
			float aeroDragFront, float aeroDragSide, float centerOfMass,
			String modelName, String textureName) {
		super(id);
		this.width = width;
		this.height = height;
		this.length = length;
		this.weight = weight;
		this.aeroDragFront = aeroDragFront;
		this.aeroDragSide = aeroDragSide;
		this.centerOfMass = centerOfMass;
		this.modelFilename = modelName;
		this.textureFilename = textureName;
	}

	public Entity getModel() {
		Model model = new Model(modelFilename, textureFilename, true);
		return new Entity(model, new Vector3f(), 0, 0, 0, 1);
	}
}
