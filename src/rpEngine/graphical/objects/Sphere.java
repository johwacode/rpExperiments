package rpEngine.graphical.objects;

import rpEngine.graphical.model.Model;
import rpEngine.graphical.model.Texture;
import rpEngine.graphical.model.VAObject;
import utils.fileLoader.OBJLoader;
import utils.math.Vector3f;

public class Sphere extends Entity {
	private static VAObject vao = OBJLoader.loadOBJ("sphere");
	private float radius;

	public Sphere(Vector3f position, float size, Texture texture) {
		super(new Model(vao, texture), position, 0, 0, 0, size);
		//TODO: check, whether size is Radius or diameter (=2r)
		radius = size;
	}

	public boolean intersects(Vector3f point) {
		Vector3f test = Vector3f.add(point, getPosition());
		return (test.length()<=radius);
	}

	
}
