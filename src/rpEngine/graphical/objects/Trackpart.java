package rpEngine.graphical.objects;

import java.io.Serializable;
import java.util.List;

import game.ChunkMap;
import utils.math.Vector3f;

public class Trackpart{
	private static final int ANCHOR1 = 0,
							 ANCHOR2 = 1,
							 DEFINING = 2;

	private Vector3f[] vertices = new Vector3f[3];
	private Entity entity;
	
	public Trackpart(Vector3f anchor1, Vector3f anchor2, Vector3f newVertex, ChunkMap chunkmap){
		vertices[ANCHOR1] = anchor1;
		vertices[ANCHOR2] = anchor2;
		vertices[DEFINING] = newVertex;
		entity = new Prism(anchor1, anchor2, newVertex);
		//registerinChunkMap(chunkmap);
	}
	
	/**
	 * constructor for curved TrackParts
	 * @param vertices
	 * @param countOfRows
	 * @param chunkmap
	 */
	public Trackpart(List<Vector3f> vertexList, int countOfRows, ChunkMap chunkmap){
		entity = new CurvePrism(vertexList, countOfRows, chunkmap);
		//registerinChunkMap(chunkmap);
	}

	public static Trackpart[] generateStart(Vector3f groundPosition, Vector3f direction, ChunkMap chunkmap, Terrain terrain){
		int width = 16,
			length = 25,
			lengthBack = 13;
		Vector3f side = new Vector3f(direction.z*width/2,
									 0,
									 direction.x*length/2),
				 left = new Vector3f(
							groundPosition.x+side.x +direction.x*lengthBack,
							0,
							groundPosition.z+side.z +direction.z*lengthBack),
				 right = new Vector3f(
							groundPosition.x-side.x +direction.x*lengthBack,
							0,
							groundPosition.z-side.z +direction.z*lengthBack),
			   thirdVertex = new Vector3f(
			   							left.x-direction.x*length,
			   							0,
			   							left.z-direction.z*length),
			   fourthVertex = new Vector3f(
										right.x-direction.x*length,
										0,
										right.z-direction.z*length);
			left.y = 1.5f+Math.max(
					terrain.getTerrainHeight(left.x, left.z),
					terrain.getTerrainHeight(right.x, right.z)
					);
			right.y=left.y;
			thirdVertex.y = 1.5f+Math.max(
					terrain.getTerrainHeight(thirdVertex.x, thirdVertex.z),
					terrain.getTerrainHeight(fourthVertex.x, fourthVertex.z)
					);
			fourthVertex.y = thirdVertex.y;
		Trackpart[] prisms = new Trackpart[2];
		prisms[0] = new Trackpart(left, right, thirdVertex, chunkmap);
		prisms[1] = new Trackpart(right, thirdVertex, fourthVertex, chunkmap);
		return prisms;
		
	}

	public Entity getEntity(){
		return entity;
	}
	
	public Vector3f[] getAnchors(){
		//TODO: durchwechselbar machen
		Vector3f[] res = new Vector3f[2];
		res[0] = vertices[ANCHOR2];
		res[1] = vertices[DEFINING];
		return res;
	}
	
	public Vector3f[] getVertices(){
		return vertices;
	}


/*
	private void registerinChunkMap(ChunkMap chunkmap) {
		if(vertices[0]==null){
			//just4testing -> curved-Part
			int x = (int) Math.floor(entity.getPosition().x/ChunkMap.RASTERSIZE);
			int z = (int) Math.floor(entity.getPosition().z/ChunkMap.RASTERSIZE);
			chunkmap.registerModel(x, z, this);
		}
		else{
			//Eckpunkte für Schleife finden
			float lowestX = vertices[ANCHOR1].x,
				lowestZ = vertices[ANCHOR1].z,
				highestX = vertices[ANCHOR1].x,
				highestZ = vertices[ANCHOR1].z;
			for(Vector3f pos:vertices){
				if(pos.x<lowestX) lowestX = pos.x;
				if(pos.z<lowestZ) lowestZ = pos.z;
				if(pos.x>highestX) highestX = pos.x;
				if(pos.z>highestZ) highestZ = pos.z;
			}
			for(int x = (int) Math.floor(lowestX/ChunkMap.RASTERSIZE); x <= highestX/ChunkMap.RASTERSIZE; x+=1){
				for(int z = (int) Math.floor(lowestZ/ChunkMap.RASTERSIZE); z <= highestZ/ChunkMap.RASTERSIZE; z+=1){
					//TODO: testen, ob wirklich innerhalb der Zelle vorhanden
					chunkmap.registerModel(x, z, this);
				}
			}
		}
		
	}
*/

	public Vector3f getCenter() {
		Vector3f center = new Vector3f();
		Vector3f.add(vertices[0], vertices[1], center);
		center.scale(0.5f);
		Vector3f.add(center, vertices[2], center);
		center.scale(0.3f);
		return center;
	}
	
	public ReconstructablePart getReconstructablePart(){
		return new ReconstructablePart(vertices);
	}
	
	public static class ReconstructablePart implements Serializable{
		private static final long serialVersionUID = 1L;
		protected Vector3f[] vertices;
		
		public ReconstructablePart(Vector3f[] vertices){
			this.vertices = vertices;
		}
		
		public Trackpart recreate(ChunkMap chunkmap){
			return new Trackpart(vertices[0], vertices[1], vertices[2], chunkmap);
		}
		
		public boolean equals(ReconstructablePart obj){
			Vector3f[] ref = obj.vertices;
			return (vertices[0].equals(ref[0]) || vertices[0].equals(ref[1]) || vertices[0].equals(ref[2]))
					&& (vertices[1].equals(ref[0]) || vertices[1].equals(ref[1]) || vertices[1].equals(ref[2]))
					&& (vertices[2].equals(ref[0]) || vertices[2].equals(ref[1]) || vertices[2].equals(ref[2]));
		}
	}
	
	public String toString(){
		return "Trackpart{"+vertices[0]+ vertices[1]+vertices[2]+"}";
	}
}
