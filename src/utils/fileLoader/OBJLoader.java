package utils.fileLoader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import rpEngine.graphical.model.CollisionBoxBuilder;
import rpEngine.graphical.model.Loader;
import rpEngine.graphical.model.VAObject;
import rpEngine.graphical.structs.Vertex;
import utils.math.Vector2f;
import utils.math.Vector3f;

public class OBJLoader {
	
	private static final String RES_LOC = "/res/models/";
	
	private static CollisionBoxBuilder collisionBoxBuilder = new CollisionBoxBuilder();

	public static VAObject loadOBJ(String objFileName) {
		InputStream in = null;
		try {
			in = Loader.class.getResourceAsStream(RES_LOC + objFileName + ".obj");
			if(in==null) throw new FileNotFoundException(objFileName + ".obj not found.");
		} catch (FileNotFoundException e) {
			System.err.println("File not found in res folder!");
			System.exit(-1);
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line;
		List<Vertex> vertices = new ArrayList<Vertex>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		List<String> faces = new LinkedList<>();
		try {
			while (true) {
				line = reader.readLine();
				if(line == null) break;
				else if (line.startsWith("v ")) {
					String[] currentLine = line.split(" ");
					Vector3f vertex = new Vector3f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]),
							(float) Float.valueOf(currentLine[3]));
					Vertex newVertex = new Vertex(vertices.size(), vertex);
					vertices.add(newVertex);

				} else if (line.startsWith("vt ")) {
					String[] currentLine = line.split(" ");
					Vector2f texture = new Vector2f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]));
					textures.add(texture);
				} else if (line.startsWith("vn ")) {
					String[] currentLine = line.split(" ");
					Vector3f normal = new Vector3f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]),
							(float) Float.valueOf(currentLine[3]));
					normals.add(normal);
				} else if (line.startsWith("f ")) faces.add(line);
			}
			
			if(faces.get(0).contains("//")) processFacesNormals(faces, vertices, indices);
			else if(faces.get(0).split(" ")[1].split("/").length==2) processFacesTextured(faces, vertices, indices);
			else processFacesNormalsAndTextures(faces, vertices, indices);
			
			reader.close();
		} catch (Exception e) {
			System.err.println("Error reading the obj-file \""+objFileName+"\"");
			e.printStackTrace();
			System.exit(-1);
		}

		float[] verticesArray = new float[vertices.size() * 3];
		float[] texturesArray = new float[vertices.size() * 2];
		float[] normalsArray = new float[vertices.size() * 3];
		float furthest = convertDataToArrays(vertices, textures, normals, verticesArray,
				texturesArray, normalsArray);
		int[] indicesArray = convertIndicesListToArray(indices);
		
		
		collisionBoxBuilder.finalizeBox();
		return Loader.loadEntityToVAO(verticesArray, texturesArray, normalsArray, indicesArray, furthest);
	}

	private static void processFacesNormalsAndTextures(List<String> faces,
			List<Vertex> vertices, List<Integer> indices) {
		for(String face: faces){
			String[] currentLine = face.split(" ");
			String[] vertex1 = currentLine[1].split("/");
			String[] vertex2 = currentLine[2].split("/");
			String[] vertex3 = currentLine[3].split("/");
			collisionBoxBuilder.addPart(
				processVertex(vertex1, vertices, indices),
				processVertex(vertex2, vertices, indices),
				processVertex(vertex3, vertices, indices)
				);
		}
		
	}

	/**
	 * doesn't work! don't use!
	 */
	private static void processFacesTextured(List<String> faces,
			List<Vertex> vertices, List<Integer> indices) {
		//as: "f 2/1 3/1 4/1"
		for(String face: faces){
			String[] currentLine = face.split(" ");
			String[][] vertex = new String[3][3];
			for(int i=0; i<3; i++){
				String tmp[] = currentLine[i+1].split("/");
				vertex[i][0] = tmp[0];
				vertex[i][1] = tmp[1];
				vertex[i][2] = "1";
				processVertex(vertex[i], vertices, indices);
			}
		}
	}

	
	private static void processFacesNormals(List<String> faces,
			List<Vertex> vertices, List<Integer> indices) {
		//as "f 2//1 3//1 4//1"
		for(String face: faces){
			String[] currentLine = face.split(" ");
			String[][] vertex = new String[3][3];
			for(int i=0; i<3; i++){
				String tmp[] = currentLine[i+1].split("/");
				vertex[i][0] = tmp[0];
				vertex[i][1] = "1";
				vertex[i][2] = tmp[2];
				processVertex(vertex[i], vertices, indices);
			}
		}
	}

	/**
	 * translates data into a Vertex. Additionally verifies, whether a vertex is already set. 
	 * @return vertex.getPosition().
	 */
	private static Vector3f processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices) {
		int index = Integer.parseInt(vertex[0]) - 1;
		Vertex currentVertex = vertices.get(index);
		int textureIndex = Integer.parseInt(vertex[1]) - 1;
		int normalIndex = Integer.parseInt(vertex[2]) - 1;
		if (!currentVertex.isSet()) {
			currentVertex.setTextureIndex(textureIndex);
			currentVertex.setNormalIndex(normalIndex);
			indices.add(index);
		} else {
			dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices,
					vertices);
		}
		return currentVertex.getPosition();
	}

	private static int[] convertIndicesListToArray(List<Integer> indices) {
		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indicesArray.length; i++) {
			indicesArray[i] = indices.get(i);
		}
		return indicesArray;
	}

	private static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures,
			List<Vector3f> normals, float[] verticesArray, float[] texturesArray,
			float[] normalsArray) {
		float furthestPoint = 0;
		for (int i = 0; i < vertices.size(); i++) {
			Vertex currentVertex = vertices.get(i);
			if (currentVertex.getLength() > furthestPoint) {
				furthestPoint = currentVertex.getLength();
			}
			Vector3f position = currentVertex.getPosition();
			Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
			Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;
			texturesArray[i * 2] = textureCoord.x;
			texturesArray[i * 2 + 1] = 1 - textureCoord.y;
			normalsArray[i * 3] = normalVector.x;
			normalsArray[i * 3 + 1] = normalVector.y;
			normalsArray[i * 3 + 2] = normalVector.z;
		}
		return furthestPoint;
	}

	private static void dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex,
			int newNormalIndex, List<Integer> indices, List<Vertex> vertices) {
		if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
			indices.add(previousVertex.getIndex());
		} else {
			Vertex anotherVertex = previousVertex.getDuplicateVertex();
			if (anotherVertex != null) {
				dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex,
						indices, vertices);
			} else {
				Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
				duplicateVertex.setTextureIndex(newTextureIndex);
				duplicateVertex.setNormalIndex(newNormalIndex);
				previousVertex.setDuplicateVertex(duplicateVertex);
				vertices.add(duplicateVertex);
				indices.add(duplicateVertex.getIndex());
			}

		}
	}

}
