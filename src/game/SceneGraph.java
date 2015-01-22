package game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import rpEngine.graphical.objects.Camera;
import rpEngine.graphical.objects.Entity;
import rpEngine.graphical.objects.Light;
import rpEngine.graphical.objects.Terrain;
import rpEngine.graphical.objects.Trackpart;
import rpEngine.graphical.objects2d.HUDElement;

public class SceneGraph {
	private Terrain terrain;
	private List<Entity> entities;
	private List<HUDElement> hud;
	private Camera camera;
	private List<Light> lights;
	private ChunkMap chunkMap;
	private BuilderTool builderTool;
	
	public SceneGraph(){
		entities = new LinkedList<>();
		hud = new ArrayList<>();
	}
	
	public Terrain getTerrain() {
		return terrain;
	}
	public void setTerrain(Terrain terrain) {
		this.terrain = terrain;
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
		if(lights==null) lights = new ArrayList<>();
		this.lights.add(light);
	}
	public ChunkMap getChunkMap() {
		return chunkMap;
	}
	public List<Trackpart> getModels(int radiusFromCam) {
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
}
