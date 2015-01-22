package rpEngine.graphical.objects;

import rpEngine.graphical.model.Loader;
import rpEngine.graphical.model.Material;
import rpEngine.graphical.model.Model;
import rpEngine.graphical.model.Texture;
import utils.math.Vector2f;


public class Billboard {
	 private static final float[] vertices = {
										-0.5f, -0.5f, 0,
										 0.5f, -0.5f, 0,
										-0.5f,  0.5f, 0,
										 0.5f,  0.5f, 0
									 	};
					
	private static final float[] texCoords = {
				0, 1,	//0
				1, 1,	//1
				0, 0,	//2
				1, 0  	//3
				};
	
	private Model model;
	Vector2f scale;
	
	public Billboard(){
		Texture texture = new Texture(Loader.loadTexture(Material.ASPHALT, "asphalt", true));
		float[] positions = {10,0,-50,
							 10,0,-50,
							 10,0,-50,
							 10,0,-50};
		model = new Model(Loader.loadBillboardToVAO(vertices, texCoords, positions), texture);
		scale = new Vector2f(1,1);
	}
	
	public Model getModel(){
		return model;
	}
	
	public Vector2f getScale(){
		return scale;
	}
}
