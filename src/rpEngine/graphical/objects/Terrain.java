package rpEngine.graphical.objects;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import rpEngine.graphical.model.Loader;
import rpEngine.graphical.model.Texture;
import rpEngine.graphical.model.VAObject;
import utils.math.Maths;
import utils.math.Vector2f;
import utils.math.Vector3f;

public class Terrain {
	
	private static final int SIZE = 800; //size in worldCoords
	private static final int VERTEX_COUNT = 128; //size in Map-Coords
	
	private float x, z;
	private VAObject model;
	private Texture[] texturePack;
	private float[][] heightMap;
	private Texture blendMap;
	
	
	
	public Terrain(int gridX, int gridZ,
					Texture[] texturePack,
					Texture blendMap){
		this.texturePack = texturePack;
		this.blendMap = blendMap;
		//texture.setReflectivity(0);
		//texture.setShineDamper(300);
		this.x = gridX*SIZE;
		this.z = gridZ * SIZE;
		heightMap = new float[VERTEX_COUNT][VERTEX_COUNT];
		testHeightMap();
		this.model = generateTerrain();
	}
	
	public void testHeightMap(){
		BufferedImage image = null;
		try {
			InputStream in = Loader.class.getResourceAsStream("/res/heightMap.png");
			if(in==null) throw new FileNotFoundException(".png not found.");

			image = ImageIO.read(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		float MaxHeight = 30;
		float factor = MaxHeight/(256*256*256);
		for(int x=0; x<VERTEX_COUNT;x++){
			for(int z=0; z<VERTEX_COUNT;z++){
				heightMap[x][z] = factor*image.getRGB(x, z)+8.5f;
			}
		}
	}
	
	
	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}
	
	public float getTerrainHeight(float worldX, float worldZ){
		float terrainX = worldX-this.x;
		float terrainZ = worldZ-this.z;
		float gridSquareSize = SIZE / ((float) VERTEX_COUNT-1);
		int gridX = (int) Math.floor((terrainX)/gridSquareSize);
		int gridZ = (int) Math.floor((terrainZ)/gridSquareSize);
		if(gridX<0 || gridZ<0 ||
			gridX >= VERTEX_COUNT-1 ||
			gridZ >= VERTEX_COUNT-1)
			return 0;
		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
		float answer = (xCoord <= (1-zCoord))?
				Maths.getBarycentricHeight(new Vector3f(0, heightMap[gridX][gridZ], 0),
											new Vector3f(1, heightMap[gridX + 1][gridZ], 0),
											new Vector3f(0, heightMap[gridX][gridZ + 1], 1),
											new Vector2f(xCoord, zCoord)) :
								
				Maths.getBarycentricHeight(new Vector3f(1, heightMap[gridX + 1][gridZ], 0),
											new Vector3f(1, heightMap[gridX + 1][gridZ + 1], 1),
											new Vector3f(0, heightMap[gridX][gridZ + 1], 1),
											new Vector2f(xCoord, zCoord));
		return answer;
	}

	private float getHeight(int x, int z){
		if(x<0||z<0||x>=VERTEX_COUNT||z>=VERTEX_COUNT)return 0;
		return heightMap[x][z];
	}

	public VAObject getModel() {
		return model;
	}

	public Texture[] getTexturePack() {
		return texturePack;
	}

	public Texture getBlendMap() {
		return blendMap;
	}
	
	private Vector3f calculateNormal(int x, int z){
		Vector3f normal = new Vector3f(
				getHeight(x-1, z)-getHeight(x+1, z),
				2f,
				getHeight(x, z-1)-getHeight(x, z+1)
				);
		normal.normalise();
		return normal;
	}

	private VAObject generateTerrain(){
		
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		float[] terrainType = new float[count];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT*1)];
		
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
				vertices[vertexPointer*3+1] = getHeight(j, i);
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
				Vector3f normal = calculateNormal(j, i);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
				terrainType[vertexPointer] = 0; //TODO: Terraintyp übermitteln.
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return Loader.loadTerrainToVAO(vertices, textureCoords, normals, indices, terrainType);
	}

}
