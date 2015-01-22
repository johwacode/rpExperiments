package rpEngine.graphical.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.TextureLoader;

import rpEngine.graphical.objects.ParticleStream;


public class Loader {
	
	private static List<Integer> vaos = new ArrayList<>(),
								vbos = new ArrayList<>();
	private static Map<Material, Integer> materials = new HashMap<>();
	private static Map<String, Integer> namelessMaterials = new HashMap<>();
	

	public static VAObject loadEntityToVAO(float[] positions,
							  float[] textureCoords,
							  float[] normals,
							  int[] indices,
							  float furthestDistance){
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDateInAttributeList(0, 3, positions);
		storeDateInAttributeList(1, 2, textureCoords);
		storeDateInAttributeList(2, 3, normals);
		unbindVAO();
		return new VAObject(vaoID, indices.length, furthestDistance);
	}
	
	public static VAObject loadBillboardToVAO(
			float[] vertices,
			float[] textureCoords,
			float[] positions){
		int vaoID = createVAO();
		storeDateInAttributeList(0, 3, vertices);
		storeDateInAttributeList(1, 2, textureCoords);
		unbindVAO();
		return new VAObject(vaoID, 4, 0.1f);
	}
	
	
	public static VAObject loadTerrainToVAO(float[] positions,
			  float[] textureCoords,
			  float[] normals,
			  int[] indices,
			  float[] terrainType){
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDateInAttributeList(0, 3, positions);
		storeDateInAttributeList(1, 2, textureCoords);
		storeDateInAttributeList(2, 3, normals);
		storeDateInAttributeList(3, 1, terrainType);
		unbindVAO();
		return new VAObject(vaoID, indices.length);
		}
	
	public static Model2D load2DToVAO(float[] positions,
							  float[] textureCoords,
							  int[] indices,
							  Texture textur){
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDateInAttributeList(0, 2, positions);
		storeDateInAttributeList(1, 2, textureCoords);
		unbindVAO();
		VAObject vao = new VAObject(vaoID, indices.length);
		return new Model2D(vao, textur);
	}
	

	
	public static VAObject loadParticleStreamToVAO(
					float[] vertices,
					float[] textureCoords,
					int buffersize,
					ParticleStream stream){
		int vaoID = createVAO();
		storeDateInAttributeList(0, 3, vertices);
		storeDateInAttributeList(1, 2, textureCoords);
		int vbo2 = createEmptyBuffer(2, 3, buffersize, GL15.GL_STREAM_DRAW);
		stream.setVBOPositionsID(vbo2);
		unbindVAO();
		return new VAObject(vaoID, 4, 0.1f);
	}
	
	public static void storeBufferSubData(int bufferLocation, float[] data, int particleCount){
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferLocation);
		FloatBuffer buffer = storeDataInFloatBuffer(data, particleCount);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data.length*Float.BYTES, null, GL15.GL_STREAM_DRAW);
		//GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, particleCount, buffer);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
	}
	
	
	
	/**
	 * liefert die ID einer Textur. Falls die Textur noch nicht initialiserit wurde geschieht dies hier.
	 * bevorzugte (weil effizienter suchende) Methode wäre loadTexture(Material material, String fileName, boolean mipmapping),
	 * da ohne Materialangabe anhand des fileName-Strings gesucht wird.
	 */
	public static int loadTexture(String fileName, boolean mipmapping){
		if(namelessMaterials.containsKey(fileName)) return namelessMaterials.get(fileName);
		int textureID = initTexure(fileName, mipmapping);
		namelessMaterials.put(fileName, textureID);
		return textureID;
	}
	
	/**
	 * liefert die Id des entsprechenden Materials. Initialisiert dieses neu, falls noch nicht geschehen.
	 */
	public static int loadTexture(Material material, String fileName, boolean mipmapping){
		if(materials.containsKey(material)) return materials.get(material);
		int textureID = initTexure(fileName, mipmapping);
		materials.put(material, textureID);
		return textureID;
	}
	
	private static int initTexure(String fileName, boolean mipmapping){
		org.newdawn.slick.opengl.Texture texture = null;
		try {
			InputStream in = Loader.class.getResourceAsStream("/res/"+fileName+".png");
			if(in==null) throw new FileNotFoundException(fileName+".png not found.");
			texture = TextureLoader.getTexture("PNG", in);
			if(mipmapping){
				GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -1.2f); //level of detail
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return texture.getTextureID();
	}
	
	public static void cleanUp(){
		for(int vao:vaos){
			GL30.glDeleteVertexArrays(vao);
		}
		for(int vbo:vbos){
			GL15.glDeleteBuffers(vbo);
		}
		for(int texture:materials.values()){
			GL11.glDeleteTextures(texture);
		}
		for(int texture:namelessMaterials.values()){
			GL11.glDeleteTextures(texture);
		}
	}
	
	private static int createVAO(){
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}
	
	public static void deleteVAO(int id){
		vaos.remove(vaos.indexOf(id));
		GL30.glDeleteVertexArrays(id);
	}
	
	/**
	 * stores Data in an Array-Buffer
	 * standard-mode: GL_STATIC_DRAW
	 */
	private static void storeDateInAttributeList(int attributeNumber, int coordinateSize, float[] data){
		storeDateInAttributeList(attributeNumber, coordinateSize, data, GL15.GL_STATIC_DRAW);
	}
		
	private static void storeDateInAttributeList(int attributeNumber, int coordinateSize, float[] data, int mode){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, mode);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, true, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private static int createEmptyBuffer(int attributeNumber, int coordinateSize, int datasize, int mode){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, datasize, null, mode);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vboID;
	}
	
	private static void unbindVAO(){
		GL30.glBindVertexArray(0);
	}
	
	private static void bindIndicesBuffer(int[] indices){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	private static IntBuffer storeDataInIntBuffer(int[] data){
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private static FloatBuffer storeDataInFloatBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length); 
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private static FloatBuffer storeDataInFloatBuffer(float[] data, int endOfData){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(endOfData); 
		buffer.put(data, 0, endOfData);
		buffer.flip();
		return buffer;
	}
	
}
