package game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import rpEngine.graphical.model.Loader;
import rpEngine.graphical.model.Material;
import rpEngine.graphical.model.Texture;
import rpEngine.graphical.objects.Camera;
import rpEngine.graphical.objects.Curve;
import rpEngine.graphical.objects.Entity;
import rpEngine.graphical.objects.Light;
import rpEngine.graphical.objects.Sphere;
import rpEngine.graphical.objects.Terrain;
import rpEngine.graphical.objects2d.HUDElement;
import utils.math.Vector3f;

public class SceneGraph {
	private Terrain terrain;
	private List<Entity> entities;
	private List<HUDElement> hud;
	private Camera camera;
	private List<Light> lights;
	private ChunkMap chunkMap;
	private BuilderTool builderTool;
	
	private static List<Entity> debugSpheres;
	private static Texture sphereTex;
	
	public SceneGraph(){
		entities = new LinkedList<>();
		debugSpheres = new LinkedList<>();
		hud = new ArrayList<>();
		lights = new ArrayList<>();
	}
	
	public static void addDebugSphere(Vector3f pos){
		if(sphereTex==null) sphereTex = new Texture(Loader.loadTexture(Material.RED, "transparentRed", true));
		debugSpheres.add(new Sphere(pos, 0.1f, sphereTex));
	}
	
	public Terrain getTerrain() {
		return terrain;
	}
	public void setTerrain(Terrain terrain) {
		this.terrain = terrain;
	}
	public static List<Entity> getDebugSpheres() {
		return debugSpheres;
	}
	public List<Entity> getEntities() {
		return entities;
	}
	public void addEntity(Entity entity) {
		this.entities.add(entity);
	}
	public void addEntities(List<Entity> entities) {
		this.entities.addAll(entities);
	}
	public List<HUDElement> getHUDElements() {
		return hud;
	}
	public void addToHUD(HUDElement newElement) {
		this.hud.add(newElement);
	}
	public void addToHUD(List<HUDElement> newElements) {
		this.hud.addAll(newElements);
	}
	public Camera getCamera() {
		return camera;
	}
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	public List<Light> getLights() {
		return lights;
	}
	public void addLight(Light light) {
		this.lights.add(light);
	}
	public ChunkMap getChunkMap() {
		return chunkMap;
	}

	/**
	 * 	
	 * @param radiusFromCam in count of Chunks
	 * @return
	 */
	public List<Curve> getModels(int radiusFromCam) {
		int camPosX =(int) (camera.getPosition().x/ChunkMap.RASTERSIZE);
		int camPosZ =(int) (camera.getPosition().z/ChunkMap.RASTERSIZE);
		return chunkMap.getModels(camPosX-radiusFromCam,
									camPosX+radiusFromCam,
									camPosZ-radiusFromCam,
									camPosZ+radiusFromCam);
	}
	public void setChunkMap(ChunkMap chunkMap) {
		this.chunkMap = chunkMap;
	}
	public BuilderTool getBuilderTool() {
		return builderTool;
	}
	public void setBuilderTool(BuilderTool builderTool) {
		this.builderTool = builderTool;
	}
	
	public void cleanUp(){
		lights.clear();
		entities.clear();
		hud.clear();
		setBuilderTool(null);
		debugSpheres.clear();
	}
}
