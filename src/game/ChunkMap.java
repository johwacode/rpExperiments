package game;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import rpEngine.graphical.objects.Curve;
import rpEngine.graphical.objects.Curve.SerializableCurveData;
import rpEngine.graphical.structs.TrackAnchor;

public class ChunkMap {
	public static final short RASTERSIZE = 200;
	
	private List<Curve>[][] map;
	private int centerX, centerZ; //Nullpunkt, damit auch negative Bereiche abdeckbar
	
	public int currentMinX, currentMinZ,currentMaxX, currentMaxZ; 
	private boolean isEmpty = true;
	
	
	@SuppressWarnings("unchecked") //List[][] not checked for <Curve>-generic
	public ChunkMap(int minX, int maxX, int minZ, int maxZ){
		map = new List[(maxX-minX)/RASTERSIZE][(maxZ-minZ)/RASTERSIZE];
		this.centerX = -minX/RASTERSIZE; this.centerZ = -minZ/RASTERSIZE;
	}
	
	public boolean isEmpty(){
		return isEmpty;
	}
	
	
	public int CoordToChunkMapValue(float coord){
		return (int) Math.floor(coord/RASTERSIZE);
	}
	
	
	public void registerModel(Curve curve){
		int x, z, tmpX=0, tmpZ=0;
		boolean first = true;
		//System.out.println("===new curveRegistration===");
		for(TrackAnchor anchor: curve.getData().anchors){
			x = CoordToChunkMapValue(anchor.getPosition().x);
			z = CoordToChunkMapValue(anchor.getPosition().z);
			if(first){
				registerModel(x,z, curve);
				first = false;
				tmpX = x; tmpZ = z;
				//System.out.println("entry in "+x+", "+z);
			}
			else if(x!=tmpX || z!=tmpZ){
					registerModel(x,z, curve);
					tmpX = x; tmpZ = z;
					//System.out.println("entry in "+x+", "+z);
				}
		}
	}
	
	/**
	 * @param xz in ChunkMap-Coordinates. 
	 * @param curve Model.
	 */
	public void registerModel(int x, int z, Curve curve){
		try{
			getModels(x, z).add(curve);
			setMinAndMax(x,z);
			//System.out.println("ChunkmapDaten: x="+x+", z="+z+".");
		}
		catch(IndexOutOfBoundsException e){
			System.err.println("Chunkmap zu klein.\n"+
								"problematische Daten: x="+x+", z="+z+".");
		}
	}
	
	private void setMinAndMax(int x, int z) {
		//on first call
		if(isEmpty){
			currentMinX = x;
			currentMaxX = x;
			currentMinZ = z;
			currentMaxZ = z;
			isEmpty = false;
		}
		//anytime else
		else{
			if(x<currentMinX) currentMinX=x;
			else if(x>currentMaxX) currentMaxX=x;
			if(z<currentMinZ) currentMinZ=z;
			else if(z>currentMaxZ) currentMaxZ=z;
		}
	}

	/**
	 * get Models in specified Area.
	 * @param xMin
	 * @param xMax
	 * @param zMin
	 * @param zMax
	 */
	public List<Curve> getModels(int xMin, int xMax, int zMin, int zMax){
		//cut out-of-Bounds-Values
		int minX = Math.max(currentMinX, Math.max(xMin, -centerX));
		int minZ = Math.max(currentMinZ, Math.max(zMin, -centerZ));
		int maxX = Math.min(currentMaxX, Math.min(xMax, (map.length-centerX)-1));
		int maxZ = Math.min(currentMaxZ, Math.min(zMax, (map[0].length-centerZ)-1));
		
		List<Curve> result = new LinkedList<>();
		for(int x=minX; x<=maxX; x++){
			for(int z=minZ; z<=maxZ; z++){
				result.addAll(getModels(x,z));
			}
		}
		return result;
	}
	
	/**
	 * @return all Curves in x-z-containing Chunk
	 * @param xz given in world-Coordinates
	 */
	public List<Curve> getModels(float x, float z){
		//System.out.println("requested: "+(int)Math.floor(x/RASTERSIZE)+", "+(int)Math.floor(z/RASTERSIZE));
		return getModels((int)Math.floor(x/RASTERSIZE), (int)Math.floor(z/RASTERSIZE));
	}
	
	private List<Curve> getModels(int x, int z){
		//printMap(); //debugStuff
		
		if(map[x+centerX][z+centerZ]==null)map[x+centerX][z+centerZ] = new LinkedList<Curve>();
		return map[x+centerX][z+centerZ];
	}
	
	public void deleteModel(int x, int z, Curve curve){
		getModels(x+centerX, z+centerZ).remove(curve);
	}
	
	/**
	 * creates a List<Serializable> with serializable data of every contained Item (Curves for now)
	 */
	public Serializable getContent(){
		LinkedList<Serializable> parts = new LinkedList<>();
		for(Curve c : getModels(currentMinX, currentMaxX, currentMinZ, currentMaxZ)){
			SerializableCurveData data = c.getData();
			if(!parts.contains(data)) parts.add(data);
		}
		if(!isEmpty)parts.add(Curve.getLastAnchor());
		return parts;
	}
	
	public void printMap(){
		System.out.println("=========\n\n ChunkMap:\n");
		for(int x=0; x<map.length; x++){
			for(int z=0; z<map[0].length; z++){
				if(map[x][z]==null) System.out.print("-");
				else {
					if(map[x][z].isEmpty()) System.out.print("o");
					 else System.out.print("x");
				} 
			}
			System.out.println(";");
		}
	}
}
