package game;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import rpEngine.graphical.objects.Trackpart;
import rpEngine.graphical.objects.Trackpart.ReconstructablePart;

public class ChunkMap {
	public static final short RASTERSIZE = 50;
	
	private List<Trackpart>[][] map;
	private int centerX, centerZ; //Nullpunkt, damit auch negative Bereiche abdeckbar
	
	public int currentMinX, currentMinZ,currentMaxX, currentMaxZ; 
	private boolean isEmpty = true;
	
	@SuppressWarnings("unchecked")
	public ChunkMap(int minX, int maxX, int minZ, int maxZ){
		map = new List[(maxX-minX)/RASTERSIZE][(maxZ-minZ)/RASTERSIZE];
		this.centerX = -minX/RASTERSIZE; this.centerZ = -minZ/RASTERSIZE;
	}
	
	public void registerModel(int x, int z, Trackpart prism){
		try{
			getModels(x, z).add(prism);
			setMinAndMax(x,z);
			//System.out.println("ChunkmapDaten: x="+x+", z="+z+".");
		}
		catch(IndexOutOfBoundsException e){
			System.err.println("Chunkmap zu klein.\n"+
								"problematische Daten: x="+x+", z="+z+".");
		}
	}
	
	private void setMinAndMax(int x, int z) {
		if(isEmpty){
			currentMinX = x;
			currentMaxX = x;
			currentMinZ = z;
			currentMaxZ = z;
			isEmpty = false;
		}
		else{
			if(x<currentMinX) currentMinX=x;
			else if(x>currentMaxX) currentMaxX=x;
			if(z<currentMinZ) currentMinZ=z;
			else if(z>currentMaxZ) currentMaxZ=z;
		}
	}

	public List<Trackpart> getModels(int xMin, int xMax, int zMin, int zMax){
		int minX = Math.max(currentMinX, Math.max(xMin, -centerX));
		int minZ = Math.max(currentMinZ, Math.max(zMin, -centerZ));
		int maxX = Math.min(currentMaxX, Math.min(xMax, map.length-centerX));
		int maxZ = Math.min(currentMaxZ, Math.min(zMax, map[0].length-centerZ));
		
		List<Trackpart> result = new LinkedList<>();
		for(int x=minX; x<=maxX; x++){
			for(int z=minZ; z<=maxZ; z++){
				result.addAll(getModels(x,z));
			}
		}
		return result;
	}
	
	public List<Trackpart> getModels(float x, float z){
		//System.out.println("requested: "+(int)Math.floor(x/RASTERSIZE)+", "+(int)Math.floor(z/RASTERSIZE));
		return getModels((int)Math.floor(x/RASTERSIZE), (int)Math.floor(z/RASTERSIZE));
	}
	
	private List<Trackpart> getModels(int x, int z){
		if(map[x+centerX][z+centerZ]==null)map[x+centerX][z+centerZ] = new LinkedList<Trackpart>();
		return map[x+centerX][z+centerZ];
	}
	
	public void deleteModel(int x, int z, Trackpart prism){
		getModels(x+centerX, z+centerZ).remove(prism);
	}
	
	public Serializable getContent(){
		LinkedList<ReconstructablePart> parts = new LinkedList<>();
		for(Trackpart t : getModels(currentMinX, currentMaxX, currentMinZ, currentMaxZ)){
			ReconstructablePart part = t.getReconstructablePart();
			if(!parts.contains(part)) parts.add(part); //TODO: testen ob contains funktioniert
		}
		return parts;
	}
}
