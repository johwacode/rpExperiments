package game;

import static org.lwjgl.system.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.system.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_4;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_SLASH;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.system.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.system.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_I;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_BACKSPACE;
import static org.lwjgl.system.glfw.GLFW.glfwGetInputMode;
import static org.lwjgl.system.glfw.GLFW.glfwGetKey;

import java.nio.DoubleBuffer;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.glfw.GLFW;

import rpEngine.graphical.model.Loader;
import rpEngine.graphical.model.Material;
import rpEngine.graphical.model.Texture;
import rpEngine.graphical.objects.Camera;
import rpEngine.graphical.objects.Curve;
import rpEngine.graphical.objects.Entity;
import rpEngine.graphical.objects.ParticlePath;
import rpEngine.graphical.objects.Sphere;
import rpEngine.graphical.objects.Terrain;
import rpEngine.graphical.objects.Trackpart;
import rpEngine.graphical.structs.HUDfriendly;
import rpEngine.graphical.structs.TrackAnchor;
import rpEngine.graphical.structs.UserController;
import utils.fileLoader.RPFileLibrary;
import utils.math.Vector3f;

public class BuilderTool implements UserController, HUDfriendly{
	private ChunkMap chunkMap;
	private Terrain terrain;
	private Tool tool;
	private Trackpart currentTrackpart;
	
	private long window;
	private Camera camera;
	
	private Texture sphereTexture;
	private Texture asphalt;

	public BuilderTool(SceneGraph scene){
		this.camera = scene.getCamera();
		this.window = camera.getWindow();
		this.chunkMap = scene.getChunkMap();
		this.terrain = scene.getTerrain();
		
		asphalt = new Texture(Loader.loadTexture(Material.ASPHALT, "asphalt", true));
		asphalt.setShineDamper(30);
		asphalt.setReflectivity(5);
		
		createInitialPrisms();
		
		tool = new CurveTool();
	}
	
	@Override
	public String getHUDmessage(String name) {
		switch(name){
		case "currentTool": return tool.getClass().getSimpleName(); //TODO: durch int ersetzen?
		default: return "";
		}
	}
	
	public void createInitialPrisms(){
		TrackAnchor start = new TrackAnchor(new Vector3f(375, 8.1f, -0.01f), new Vector3f(0,0,-1), new Vector3f(-1, 0, 0));
		chunkMap.registerModel(Curve.createPreview(start, -0.01f, -0.3f, 20, 0).buildCurve());
		chunkMap.registerModel(Curve.createPreview(Curve.getLastAnchor(), 0.02f, 0.1f, 15, 0).buildCurve());
	}
	

	public void move(){
		tool.move();
	}
	
	public void processInput(int key, int action) {
		if(action!=GLFW_PRESS) return;
		switch(key){
		case GLFW_KEY_S: 
			if(glfwGetKey(window, GLFW_KEY_LEFT_CONTROL)==GLFW_PRESS){
				String date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, Locale.US).format(new Date());
				date = date.replaceAll("\\/|\\.|\\:", "");
				date = date.replaceAll(" ", "_");
				RPFileLibrary.writeToFile("savedTracks", "trackPart-"+date+".rpf", chunkMap.getContent());
			}
			break;
		//case GLFW_KEY_1: if(tool.getClass()!= PrismTool.class) tool = new PrismTool(); break;
		//case GLFW_KEY_2: if(tool.getClass()!= QuadTool.class) tool = new QuadTool(); break;
		case GLFW_KEY_3: if(tool.getClass()!= CurveTool.class) tool = new CurveTool(); break;
		//case GLFW_KEY_4: if(tool.getClass()!= PointerTool.class) tool = new PointerTool(); break;
		default: tool.processInput(key, action);
		}
	}
	
	public List<ParticlePath> getParticleStreams() {
		return tool.getParticleStreams();
	}
	
	public List<Entity> getPreview(){
		return tool.getPreview();
	}
	
	
	
	private interface Tool{
		public void createTool();
		public void processInput(int key, int action);
		public void move();
		public List<Entity> getPreview();
		public List<ParticlePath> getParticleStreams();
	}
	

	
	private class CurveTool implements Tool{
		private Curve.Preview previewCurve;
		private float pitch, angleXZ, distance, heightDifference;
		
		private CurveTool(){
			resetValues();
			createTool();
		}
		
		private void resetValues(){
			pitch = 0;
			angleXZ = 0;
			distance = 10;
			heightDifference = 0;
		}
		
		public void createTool() {
			previewCurve = Curve.createPreview(Curve.getLastAnchor(), angleXZ, heightDifference, distance, pitch);
		}
		
		private void createTrackPart(){
			chunkMap.registerModel(previewCurve.buildCurve());
		}

		@Override
		public void processInput(int key, int action) {
    		switch(key){
    		case GLFW_MOUSE_BUTTON_LEFT: if(glfwGetInputMode(window, GLFW_CURSOR)!=GLFW_CURSOR_DISABLED)break;
    		case GLFW_KEY_ENTER:
    			createTrackPart();
    			createTool();
				break;
    		}
		}

		@Override
		public void move() {
			try{
				if(glfwGetKey(window, GLFW_KEY_LEFT_CONTROL)==GLFW_PRESS)
					return;
				
				if(glfwGetKey(window, GLFW_KEY_W)==GLFW_PRESS ||
					glfwGetKey(window, GLFW_KEY_A)==GLFW_PRESS ||
					glfwGetKey(window, GLFW_KEY_S)==GLFW_PRESS ||
					glfwGetKey(window, GLFW_KEY_D)==GLFW_PRESS){
					//createTool();
				}
				
				if(glfwGetKey(window, GLFW_KEY_LEFT)==GLFW_PRESS){
					angleXZ -= 0.005f;
					createTool();
				}
				if(glfwGetKey(window, GLFW_KEY_RIGHT)==GLFW_PRESS){
					angleXZ += 0.005f;
					createTool();
				}
				if(glfwGetKey(window, GLFW_KEY_UP)==GLFW_PRESS){
					distance += 0.1f;
					createTool();
				}
				if(glfwGetKey(window, GLFW_KEY_DOWN)==GLFW_PRESS){
					distance -= 0.1f;
					createTool();
				}
				if(glfwGetKey(window, GLFW_KEY_RIGHT_SHIFT)==GLFW_PRESS){
					heightDifference += 0.05f;
					createTool();
				}
				if(glfwGetKey(window, GLFW_KEY_SLASH)==GLFW_PRESS){ //Minus-Taste, US-Layout und so..
					heightDifference -= 0.05f;
					createTool();
				}
				if(glfwGetKey(window, GLFW_KEY_BACKSPACE)==GLFW_PRESS){ //Minus-Taste, US-Layout und so..
					resetValues();
					createTool();
				}
				if(glfwGetKey(window, GLFW_KEY_I)==GLFW_PRESS){ //Minus-Taste, US-Layout und so..
					chunkMap.printMap();
				}
				/*
				 if(glfwGetInputMode(window, GLFW_CURSOR)==GLFW_CURSOR_DISABLED){
				 
					DoubleBuffer xpos = BufferUtils.createDoubleBuffer(1);
					DoubleBuffer ypos = BufferUtils.createDoubleBuffer(1);
					GLFW.glfwGetCursorPos(window, xpos, ypos);
					
					if(xpos.get()!=0 || ypos.get()!=0)
						createTool();
				}
				*/
			}catch(NullPointerException e){
			}
		}

		@Override
		public List<ParticlePath> getParticleStreams(){
			List<ParticlePath> r = new LinkedList<>();
			return r;
		}

		@Override
		public List<Entity> getPreview() {
			//TODO surely ineffective to always create new List, isn't it?
			List<Entity> e = new LinkedList<>();
			e.add(previewCurve);
			return e;
		}
	}
}
