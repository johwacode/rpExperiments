package rpEngine.graphical.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import rpEngine.graphical.model.Model;
import rpEngine.graphical.objects.Camera;
import rpEngine.graphical.objects.Entity;
import rpEngine.graphical.objects.Light;
import rpEngine.graphical.objects.ParticleStream;
import rpEngine.graphical.objects.Terrain;
import rpEngine.graphical.objects2d.HUDElement;
import rpEngine.graphical.shader.EntityShader;
import rpEngine.graphical.shader.HUDShader;
import rpEngine.graphical.shader.ParticleShader;
import rpEngine.graphical.shader.SkyBoxRenderer;
import rpEngine.graphical.shader.SkyboxShader;
import rpEngine.graphical.shader.TerrainShader;
import utils.math.Matrix4f;



public class MasterRenderer {

	private static final float[] backgroundColor = {0.61f,  0.85f,  1f, 1};
	private static final float[] fogColor = {0.61f,  0.85f,  1f, 1};
	
	private Matrix4f projectionMatrix;
	
	private EntityShader shader = new EntityShader();
	private TerrainShader terrainShader = new TerrainShader();
	private HUDShader hudShader = new HUDShader();
	private ParticleShader particleShader = new ParticleShader();
	private SkyboxShader skyBoxShader = new SkyboxShader();
	private EntityRenderer entityRenderer;
	private TerrainRenderer terrainRenderer;
	private SkyBoxRenderer skyboxRenderer;
	private HUDRenderer hudRenderer;
	private ParticleRenderer particleRenderer;
	
	
	private Map<Model, List<Entity>> entities = new HashMap<>();
	private List<Terrain> terrains = new ArrayList<>();
	private List<HUDElement> hudObjects = new ArrayList<>();
	private List<ParticleStream> particleStreams = new ArrayList<>();
	
	
	public MasterRenderer(long window){
		enableCulling();
		entityRenderer = new EntityRenderer(shader);
		terrainRenderer = new TerrainRenderer(terrainShader);
		skyboxRenderer = new SkyBoxRenderer(skyBoxShader);
		hudRenderer = new HUDRenderer(hudShader);
		particleRenderer = new ParticleRenderer(particleShader);
		setProjectionmatrix(window);
	}
	
	public static void enableCulling(){
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling(){
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void render2D(){
		prepare();
		
		hudShader.start();
		hudRenderer.render(hudObjects);
		hudShader.stop();
		
		terrains.clear();
		entities.clear();
		hudObjects.clear();
		particleStreams.clear();
	}
	
	
	public void render(List<Light> lights, Camera camera){
		prepare();
		
		/* Spheres ausklammern
		
		System.out.println("-------------");
		for(List<Entity> entitylist: entities.values()){
			for(int i=entitylist.size()-1; i>=0; i--){
				System.out.println("Entity: "+entitylist.get(i));
				if(entitylist.get(i) instanceof Sphere){
					System.out.println("entfernt.");
					entitylist.remove(entitylist.get(i));
				}
			}
		}
		*/
		
		shader.start();
		shader.loadFogColour(fogColor[0], fogColor[1], fogColor[2]);
		shader.loadLight(lights);
		shader.loadViewMatrix(camera);
		entityRenderer.render(entities);
		shader.stop();
		
		terrainShader.start();
		terrainShader.loadFogColour(fogColor[0], fogColor[1], fogColor[2]);
		terrainShader.loadLight(lights);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains);
		terrainShader.stop();
		
		hudShader.start();
		hudRenderer.render(hudObjects);
		hudShader.stop();
		
		particleShader.start();
		particleShader.loadViewMatrix(camera);
		particleRenderer.render(particleStreams);
		particleShader.stop();
		
		skyboxRenderer.render(camera);
		
		terrains.clear();
		entities.clear();
		hudObjects.clear();
		particleStreams.clear();
	}
	
	public void processParticleStream(ParticleStream pStream){
		particleStreams.add(pStream);
	}
	
	public void processTerrain(Terrain terrain){
		terrains.add(terrain);
	}
	
	public void prepare(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(backgroundColor[0],
							backgroundColor[1],
							backgroundColor[2],
							backgroundColor[3]);
	}
	
	
	public void processEntity(Entity entity){
		Model entityModel = entity.getModel();
		List<Entity>batch = entities.get(entityModel);
		if(batch!=null) batch.add(entity);
		else{
			List<Entity> newBatch = new ArrayList<>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}
	
	public void processHUDElement(HUDElement hudElement){
		hudObjects.add(hudElement);
	}
	
	public void cleanUp(){
		shader.cleanUp();
		terrainShader.cleanUp();
		hudShader.cleanUp();
		particleShader.cleanUp();
	}
	
	public void setProjectionmatrix(long window){
		projectionMatrix = Matrix4f.createProjectionMatrix(window);
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		terrainShader.start();
		terrainShader.loadProjectionMatrix(projectionMatrix);
		terrainShader.stop();
		particleShader.start();
		particleShader.loadProjectionMatrix(projectionMatrix);
		particleShader.stop();
		skyBoxShader.start();
		skyBoxShader.loadProjectionMatrix(projectionMatrix);
		skyBoxShader.stop();
	}
}
