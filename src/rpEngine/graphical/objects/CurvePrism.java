package rpEngine.graphical.objects;

import game.ChunkMap;

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
		int verticesPerRow = vertices.size()/rowCount;
		
		float[] vertexArray = new float[3*vertices.size()/* für zus. unterseite mal 2 */];
		float[] normals = new float[vertexArray.length];
		float[] textureCoords = new float[2*vertices.size()];
		
		for(int i=0; i<vertices.size(); i++){
			Vector3f v = vertices.get(i);
			vertexArray[3*i] = v.x-position.x;
			vertexArray[3*i+1] = v.y-position.y;
			vertexArray[3*i+2] = v.z-position.z;
			
			//TODO: calc real normals -> x,z determined by direction
			normals[3*i] = 0;
			normals[3*i] = 1;
			normals[3*i] = 0;
			
			switch(i%4){
			case 0: textureCoords[2*i] = 0;
				textureCoords[2*i+1] = 1;
				break;
			case 1: textureCoords[2*i] = 1;
				textureCoords[2*i+1] = 1;
				break;
			case 2: textureCoords[2*i] = 1;
				textureCoords[2*i+1] = 0;
				break;
			case 3: textureCoords[2*i] = 0;
				textureCoords[2*i+1] = 0;
				break;
			}
		}
		
		/*
		 * n rows with m vertices each
		 * -> (n-1)(m-1) triangleLines for right-hand-triangles, equally left-hand-ones
		 * --> *2 -> *3points per triangle
		 */
		int[] indices = new int[6*(rowCount-1)*(verticesPerRow-1)];
		int i = 0;
		for(int stripe=0; stripe<rowCount-1; stripe++){
			for(int quad=0; quad<verticesPerRow-1; quad++){
				//indices for 2 triangles (~ one quad)
				indices[i+0] = stripe*verticesPerRow+quad;
				indices[i+1] = stripe*verticesPerRow+quad+1;
				indices[i+2] = (stripe+1)*verticesPerRow+quad;
				
				indices[i+3] = stripe*verticesPerRow+quad+1;
				indices[i+4] = (stripe+1)*verticesPerRow+quad+1;
				indices[i+5] = (stripe+1)*verticesPerRow+quad;
				
				i+=6;
			}
		}
		
		Model model = new Model(Loader.loadEntityToVAO(vertexArray, textureCoords, normals, indices, 500), asphalt);
		return model;
	}
}
