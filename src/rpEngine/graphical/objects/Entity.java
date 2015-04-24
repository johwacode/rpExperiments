package rpEngine.graphical.objects;

import rpEngine.graphical.model.BoundingBox;
import rpEngine.graphical.model.Model;
import utils.math.Matrix4f;
import utils.math.Vector3f;

public class Entity {
	private Model model;
	private BoundingBox boundingBox;
	private Vector3f position;
	private float rotX, rotY, rotZ;
	private float scale;
	
	private Matrix4f transformationMatrix;
	private boolean matrixUpToDate = false;
	
	/**
	 * @param model
	 * @param position
	 * @param rotX
	 * @param rotY
	 * @param rotZ
	 * @param scale
	 */
	public Entity(Model model, Vector3f position, float rotX,
			float rotY, float rotZ, float scale) {
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		BoundingBox.generateBox(model.getVao().getVertices());
	}
	
	public Matrix4f getTransformationMatrix(){
		if(!matrixUpToDate){
			transformationMatrix = Matrix4f.createTransformationMatrix(position,
				rotX, rotY, rotZ,
				scale);
			matrixUpToDate = true;
		}
		return transformationMatrix;
	}
	
	public void increasePosition(Vector3f direction){
		increasePosition(direction.x, direction.y, direction.z);
	}
	
	public void increasePosition(float dx, float dy, float dz){
		this.position.x+=dx;
		this.position.y+=dy;
		this.position.z+=dz;
		matrixUpToDate = false;
	}
	
	public void increaseRotation(float dx, float dy, float dz){
		this.rotX+=dx;
		this.rotY+=dy;
		this.rotZ+=dz;
		matrixUpToDate = false;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
		matrixUpToDate = false;
	}

	public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
		matrixUpToDate = false;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
		matrixUpToDate = false;
	}

	public float getRotZ() {
		return rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
		matrixUpToDate = false;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
		matrixUpToDate = false;
	}

	public void setMatrixOutdatedFlag() {
		matrixUpToDate = false;
	}
	
}