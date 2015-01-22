package testPackage;

import rpEngine.graphical.model.Loader;
import rpEngine.graphical.model.Material;
import rpEngine.graphical.model.Model;
import rpEngine.graphical.model.Texture;
import rpEngine.graphical.objects.Entity;
import utils.math.Vector3f;

public class Square extends Entity {
	private static final float[] vertices = {
		 -0.5f, -0.5f, 0,
		  0.5f, -0.5f, 0,
		 -0.5f,  0.5f, 0,
		  0.5f,  0.5f, 0
	 	};
	
	private static final float[] normals ={
		0.5f, 0, 0.5f,	//0
		0.5f, 0, 0.5f,	//1
		0.5f, 0, 0.5f	//2
		};
	
	private static final float[] texCoords ={
		0, 1,	//0
		1, 1,	//1
		0, 0,	//2
		1, 0  	//3
		};
	
	private static final int[] indices ={
		0, 1, 2,	//0
		1, 3, 2		//1
		};
	
	/**
	 * standardm‰ﬂig sichtbar aus Richtung (0,0,0) [an pos(10,5, -25)]
	 */
	public Square(){
		super(
			new Model(Loader.loadEntityToVAO(vertices, texCoords, normals, indices, 0),
				new Texture(Loader.loadTexture(Material.ASPHALT, "asphalt", true))),
			new Vector3f(10, 5, -25),
			0, 0, 0,
			3);
	}
}
