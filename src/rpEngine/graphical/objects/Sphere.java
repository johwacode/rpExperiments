package rpEngine.graphical.objects;

import rpEngine.graphical.model.Model;
import rpEngine.graphical.model.Texture;
import rpEngine.graphical.model.VAObject;
import utils.fileLoader.OBJLoader;
import utils.math.Vector3f;

public class Sphere extends Entity {
	private static VAObject vao = OBJLoader.loadOBJ("sphere");

	public Sphere(Vector3f position, float size, Texture texture) {
		super(new Model(vao, texture), position, 0, 0, 0, size);
	}
}
