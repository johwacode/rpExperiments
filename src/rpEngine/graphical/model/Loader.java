package rpEngine.graphical.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.PNGDecoder;
import org.newdawn.slick.opengl.TextureLoader;

import rpEngine.graphical.objects.ParticleStream;
import rpEngine.graphical.structs.TextureData;


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
	
	public static VAObject loadPositionOnlyVAO(float[] positions){
		int vaoID = createVAO();
		storeDateInAttributeList(0, 3, positions);
		unbindVAO();
		return new VAObject(vaoID, positions.length/3);
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
	
	
	public static int loadCubeMapTexture(String[] textureFiles, String textureName){
		if(namelessMaterials.containsKey(textureName)) return namelessMaterials.get(textureName);
		
		int texID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);

		for (int i=0; i<6; i++){
			TextureData data = decodeTexureFile(textureFiles[i]);
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X +i,
					0, GL11.GL_RGBA,
					data.getWidth(), data.getHeight(), 0,
					GL11.GL_RGBA,
					GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		namelessMaterials.put(textureName, texID);
		return texID;
	}
	
	/**
	 * creates a TextureData-Object from a PNG-file. Used for the Skybox-Cubemap-Texture
	 */
	private static TextureData decodeTexureFile(String fileName){
		int width = 0, height = 0;
		ByteBuffer buffer = null;
		try {
			InputStream in = Loader.class.getResourceAsStream("/res/"+fileName+".png");
			PNGDecoder decoder = new PNGDecoder(in);
			width = decoder.getWidth();
			height = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4*width*height);
			decoder.decode(buffer, width *4, PNGDecoder.RGBA);
			buffer.flip();
			in.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new TextureData(buffer, width, height);
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
