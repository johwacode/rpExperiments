package game;

import static org.lwjgl.system.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.system.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.system.glfw.GLFW.glfwGetInputMode;
import game.menu.MainMenu;
import game.menu.MenuController;

import java.io.Serializable;
import java.util.List;

import org.lwjgl.system.glfw.GLFW;

import rpEngine.graphical.model.Loader;
import rpEngine.graphical.model.Material;
import rpEngine.graphical.model.Texture;
import rpEngine.graphical.objects.Camera;
import rpEngine.graphical.objects.Curve;
import rpEngine.graphical.objects.Curve.SerializableCurveData;
import rpEngine.graphical.objects.Entity;
import rpEngine.graphical.objects.Light;
import rpEngine.graphical.objects.ParticleStream;
import rpEngine.graphical.objects.Terrain;
import rpEngine.graphical.objects2d.DebugLine;
import rpEngine.graphical.objects2d.HUDElement;
import rpEngine.graphical.objects2d.ToolBoxDisplay;
import rpEngine.graphical.objects2d.text.Text;
import rpEngine.graphical.renderer.MasterRenderer;
import rpEngine.graphical.structs.TrackAnchor;
import utils.math.Vector3f;

public class RacingPlanetsGame {
	public enum RPGameMode {MENUMODE, BUILDMODE, RACINGMODE}
	private GameMode currentMode;
	private MasterRenderer renderer;
	private SceneGraph scene;
	
	private long window;
	private DebugLine debugLine = new DebugLine(0.2f, 9.4f);
	
	public RacingPlanetsGame(long window){
		this.window = window;
		this.renderer = new MasterRenderer(window);
		this.scene = new SceneGraph();
		this.currentMode = new MenuMode(null);
	}
	
	public void setMode(RPGameMode newMode){
		setMode(newMode, null);
	}
	
	public void setMode(RPGameMode newMode, Serializable args){
		currentMode.cleanUp();
		switch(newMode){
		case MENUMODE: currentMode = new MenuMode(args); break;
		case BUILDMODE: currentMode = new BuildMode(args); break;
		case RACINGMODE: currentMode = new Race(args); break;
		}
	}
	
	public void update(){
		currentMode.render();
	}
	
	public void cleanUp(){
		currentMode.cleanUp();
		debugLine.stopRunning();
		
		renderer.cleanUp();
		Loader.cleanUp();
	}
	
	
	public abstract class GameMode{
		/**
		 * @param args just an empty constructor -> do what you want
		 */
		GameMode(Serializable args){}
		abstract void render();
		void cleanUp(){
			InputController.clear();
			Text.clear();
		}
	}
	
	
	
	
	
	
	
	
	/**
	 * Stay cool man, you're in the Menu-mode. Nothing exiting here,
	 * just a few settings, loading, connecting and such stuff.
	 * And of course choosing what to do next a.k.a. loading a GameMode.
	 */
	public class MenuMode extends GameMode{
		MenuController menuController;

		public MenuMode(Serializable args) {
			super(args);
			menuController = new MenuController(RacingPlanetsGame.this, MainMenu.class);
		}

		@Override
		public void render() {
			menuController.getCurrent().handleMouseMovement(window);
			
			renderer.processHUDElement(menuController.getCurrent());
			
			debugLine.printMessages();
			renderer.processHUDElement(debugLine);
			
			renderer.render2D();
		}
	}
	
	/**
	 * speed up and win, racing on your custom build tracks, while 
	 * each opponent built his own track part  
	 */
	public class Race extends GameMode{
		private int maxViewDistance = 3;
		
		public Race(Serializable args) {
			super(args);
			initTerrain();
			initEnvironment(args);
			initVehicles(args);
			initHUD();
			initCamera(window);
		}
		
		@Override
		public void render() {
			scene.getCamera().move();
			if(glfwGetInputMode(window, GLFW_CURSOR)==GLFW_CURSOR_DISABLED)
				GLFW.glfwSetCursorPos(window, 0, 0);
			
			renderer.processTerrain(scene.getTerrain());
			
			for(Curve c:scene.getModels(maxViewDistance)){
				renderer.processEntity(c);
			}
			
			for(Entity e:scene.getEntities()){
				renderer.processEntity(e);
			}
			
			
			for(HUDElement e: scene.getHUDElements()){
				try{
				//e.refreshDisplay(scene.getBuilderTool());
				}catch(NullPointerException npe){
					npe.printStackTrace();
				}
				renderer.processHUDElement(e);
			}
			debugLine.printMessages();
			renderer.processHUDElement(debugLine);
			
			renderer.render(scene.getLights(),  scene.getCamera());
		}
		
		
		
		private void initTerrain(){
			Texture[] texturePack = new Texture[4];
			Texture blendMap;
			
			texturePack[0] = new Texture(Loader.loadTexture(Material.WATER, "surface_water3", true));
			texturePack[1] = new Texture(Loader.loadTexture(Material.GRASS, "grassy2", true));
			texturePack[2] = new Texture(Loader.loadTexture(Material.DIRT, "dirt", true));
			texturePack[3] = new Texture(Loader.loadTexture(Material.MUD, "mud", true));
			blendMap = new Texture(Loader.loadTexture("blendMap", false));
			
			scene.setTerrain(new Terrain(0, -1, texturePack, blendMap));
		}
		
		private void initHUD(){
			scene.addToHUD(new ToolBoxDisplay(9.35f, 7));
		}
		
		@SuppressWarnings("unchecked")
		private void initEnvironment(Serializable args){
			//sun
			scene.addLight(new Light(new Vector3f(300, -40, -10), new Vector3f(0.4f, 0.4f, 0.4f)));
			//spots
			Vector3f attenuation = new Vector3f(1, 0.01f, 0.002f);
			scene.addLight(new Light(new Vector3f(380, 10, -30), new Vector3f(2,0,0), attenuation));
			scene.addLight(new Light(new Vector3f(350, 17, -10), new Vector3f(0,2,2), attenuation));
			scene.addLight(new Light(new Vector3f(370, 8, -80), new Vector3f(2,2,0), attenuation));
			ChunkMap chunkMap = new ChunkMap(0, 800, -800, 0);
			if(args != null){
					//TODO: organize more flexible and for more datatypes
					List<Serializable> dataList = (List<Serializable>) args;
					for(Serializable data: dataList){
						try{
							SerializableCurveData curveData = (SerializableCurveData) data;
							chunkMap.registerModel(new Curve(curveData));
						} catch(IllegalArgumentException e){
							System.out.println("Unknown Data");
						}
						  catch(ClassCastException c){
							  Curve.setLastAnchor((TrackAnchor) data);
						  }
					}
			}
			scene.setChunkMap(chunkMap);
		}
		
		private void initVehicles(Serializable args) {
			// TODO Auto-generated method stub
			
		}

		private void initCamera(long window){
			scene.setCamera(new Camera(window, new Vector3f(370, 8, -14), scene));
		}
		
	}
	
	
	/**
	 * User has a set of tools to build his own race track.
	 */
	public class BuildMode extends GameMode{
		private int maxViewDistance = 3;
		
		public BuildMode(Serializable args) {
			super(args);
			initTerrain();
			initEnvironment(args);
			initHUD();
			initCamera(window);
			initBuilderTool();
			/*TODO: delete following*/
			/*
			scene.addEntity(new Entity(new Model(OBJLoader.loadOBJ("mustang_gt500kr"),
												new Texture(Loader.loadTexture(Material.WATER, "surface_water3", true))),
										new Vector3f(0,0,0),
										0, 0, 0,
										1));
			*/
		}

		@Override
		public void render() {
			scene.getCamera().move();
			scene.getBuilderTool().move();
			if(glfwGetInputMode(window, GLFW_CURSOR)==GLFW_CURSOR_DISABLED)
				GLFW.glfwSetCursorPos(window, 0, 0);
			
			renderer.processTerrain(scene.getTerrain());
			
			for(Curve c:scene.getModels(maxViewDistance)){
				renderer.processEntity(c);
			}
			for(Entity e:scene.getBuilderTool().getPreview()){
				renderer.processEntity(e);
			}

			for(ParticleStream pStream:scene.getBuilderTool().getParticleStreams()){
				renderer.processParticleStream(pStream);
			}
			
			for(Entity e:scene.getEntities()){
				renderer.processEntity(e);
			}
			
			
			for(HUDElement e: scene.getHUDElements()){
				try{
				e.refreshDisplay(scene.getBuilderTool());
				}catch(NullPointerException npe){
					npe.printStackTrace();
				}
				renderer.processHUDElement(e);
			}
			debugLine.printMessages();
			renderer.processHUDElement(debugLine);
			
			renderer.render(scene.getLights(),  scene.getCamera());
		}
		
		
		
		private void initTerrain(){
			Texture[] texturePack = new Texture[4];
			Texture blendMap;
			
			texturePack[0] = new Texture(Loader.loadTexture(Material.WATER, "surface_water3", true));
			texturePack[1] = new Texture(Loader.loadTexture(Material.GRASS, "grassy2", true));
			texturePack[2] = new Texture(Loader.loadTexture(Material.DIRT, "dirt", true));
			texturePack[3] = new Texture(Loader.loadTexture(Material.MUD, "mud", true));
			blendMap = new Texture(Loader.loadTexture("blendMap", false));
			
			scene.setTerrain(new Terrain(0, -1, texturePack, blendMap));
		}
		
		private void initHUD(){
			scene.addToHUD(new ToolBoxDisplay(9.35f, 7));
		}
		
		@SuppressWarnings("unchecked")
		private void initEnvironment(Serializable args){
			//sun
			scene.addLight(new Light(new Vector3f(300, -40, -10), new Vector3f(0.4f, 0.4f, 0.4f)));
			//spots
			Vector3f attenuation = new Vector3f(1, 0.01f, 0.002f);
			scene.addLight(new Light(new Vector3f(380, 10, -30), new Vector3f(2,0,0), attenuation));
			scene.addLight(new Light(new Vector3f(350, 17, -10), new Vector3f(0,2,2), attenuation));
			scene.addLight(new Light(new Vector3f(370, 8, -80), new Vector3f(2,2,0), attenuation));
			ChunkMap chunkMap = new ChunkMap(0, 800, -800, 0);
			if(args != null){
					//TODO: organize more flexible and for more datatypes
					List<Serializable> dataList = (List<Serializable>) args;
					for(Serializable data: dataList){
						try{
							SerializableCurveData curveData = (SerializableCurveData) data;
							chunkMap.registerModel(new Curve(curveData));
						} catch(IllegalArgumentException e){
							System.out.println("Unknown Data");
						}
						  catch(ClassCastException c){
							  Curve.setLastAnchor((TrackAnchor) data);
						  }
					}
			}
			scene.setChunkMap(chunkMap);
		}

		private void initCamera(long window){
			Camera cam = new Camera(window, new Vector3f(370, 8, -14), scene);
			scene.setCamera(cam);
			InputController.registerHandler(cam);
		}
		
		private void initBuilderTool(){
			scene.setBuilderTool(new BuilderTool(scene));
		}
		
	}

}
