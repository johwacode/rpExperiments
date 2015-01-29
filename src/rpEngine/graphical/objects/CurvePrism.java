package rpEngine.graphical.objects;

import game.ChunkMap;

import java.util.ArrayList;
import java.util.List;

import rpEngine.graphical.model.Loader;
import rpEngine.graphical.model.Material;
import rpEngine.graphical.model.Model;
import rpEngine.graphical.model.Texture;
import utils.math.Vector3f;

public class CurvePrism extends Entity{
	private static Texture asphalt = new Texture(Loader.loadTexture(Material.ASPHALT, "asphalt", true));
	private static final float height = 0.4f;

	public CurvePrism(List<Vector3f> vertices, int countOfRows, ChunkMap chunkmap) {
		super(calcModel(vertices, countOfRows), vertices.get(0), 0, 0, 0, 1);
	}
	
	private static Model calcModel(List<Vector3f> vertices, int rowCount){
		Vector3f position = vertices.get(0).duplicate();
		
		float[] vertexArray = new float[3*vertices.size()/* für zus. unterseite mal 2 */];
		for(int i=0; i<vertices.size(); i++){
			Vector3f v = vertices.get(i);
			vertexArray[3*i] = v.x-position.x;
			vertexArray[3*i+1] = v.y-position.y;
			vertexArray[3*i+2] = v.z-position.z;
		}
		
		/*
		 * n rows with m vertices each
		 * -> (n-1)(m-1) triangleLines for right-hand-triangles, equally left-hand-ones
		 * --> *2
		 */
		int verticesPerRow = vertices.size()/rowCount;
		int[] indices = new int[2*(rowCount-1)*(verticesPerRow-1)];
		for(int stripe=0; stripe<rowCount-1; stripe++){
			for(int quad=0; quad<verticesPerRow-1; quad++){
				indices[0] = stripe*verticesPerRow+quad;
				indices[1] = stripe*verticesPerRow+quad+1;
				indices[2] = (stripe+1)*verticesPerRow+quad;
				
				indices[3] = stripe*verticesPerRow+quad+1;
				indices[4] = (stripe+1)*verticesPerRow+quad;
				indices[5] = (stripe+1)*verticesPerRow+quad+1;
			}
		}
		
		//just2try
		float[] textureCoords =
			{ 0, 1,
			  1, 1,
			  0, 0,
			  0, 1,
			  1, 0,
			  0, 0,
			  1, 0,
			  1, 1
			};
		float[] normals =
			{ 0.57735f, -0.57735f, -0.57735f,
			  0.57735f, -0.57735f,  0.57735f,
			 -0.57735f, -0.57735f, -0.57735f,
			 -0.57735f,  0.57735f, -0.57735f,
			  0.57735f,  0.57735f,  0.57735f,
			  0.57735f,  0.57735f, -0.57735f,
			  0.57735f, -0.57735f, -0.57735f,
			  0.57735f,  0.57735f, -0.57735f};
		
		
		Model model = new Model(Loader.loadEntityToVAO(vertexArray, textureCoords, normals, indices, 500), asphalt);
		return model;
	}
}
