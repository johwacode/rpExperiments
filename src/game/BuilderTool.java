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
import static org.lwjgl.system.glfw.GLFW.glfwGetInputMode;
import static org.lwjgl.system.glfw.GLFW.glfwGetKey;

import java.nio.DoubleBuffer;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Date;
import java.util.Locale;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.glfw.GLFW;

import rpEngine.graphical.model.Loader;
import rpEngine.graphical.model.Material;
import rpEngine.graphical.model.Texture;
import rpEngine.graphical.objects.Camera;
import rpEngine.graphical.objects.ParticlePath;
import rpEngine.graphical.objects.Sphere;
import rpEngine.graphical.objects.Terrain;
import rpEngine.graphical.objects.Trackpart;
import rpEngine.graphical.structs.HUDfriendly;
import rpEngine.graphical.structs.UserController;
import utils.fileLoader.RPFileLibrary;
import utils.math.Vector3f;

public class BuilderTool implements UserController, HUDfriendly{
	private ChunkMap chunkMap;
	private Terrain terrain;
	private Tool tool;
	private Trackpart currentTrackpart;
	private Sphere[] anchorSpots = new Sphere[2];
	
	private long window;
	private Camera camera;
	
	private Texture sphereTexture;
	private Texture asphalt;

	public BuilderTool(SceneGraph scene){
		this.camera = scene.getCamera();
		this.window = camera.getWindow();
		this.chunkMap = scene.getChunkMap();
		this.terrain = scene.getTerrain();
		
		sphereTexture = new Texture(Loader.loadTexture(Material.RED, "transparentRed", true));
		sphereTexture.setShineDamper(30);
		//sphereTexture.setReflectivity(5);
		asphalt = new Texture(Loader.loadTexture(Material.ASPHALT, "asphalt", true));
		asphalt.setShineDamper(30);
		asphalt.setReflectivity(5);
		
		createInitialPrisms();
		
		tool = new QuadTool();
		tool.createTool();
	}
	
	@Override
	public String getHUDmessage(String name) {
		switch(name){
		case "currentTool": return tool.getClass().getSimpleName(); //TODO: durch int ersetzen?
		default: return "";
		}
		
	}
	
	public void createInitialPrisms(){
		Vector3f camPos = camera.getPosition();
		currentTrackpart = Trackpart.generateStart(new Vector3f(camPos.x, -100, camPos.z), camera.getDirection(0, 1), chunkMap, terrain)[1];
		setAnchorSpots();
	}
	
	

	private void setAnchorSpots() {
		Vector3f[] positions = currentTrackpart.getAnchors();
		anchorSpots[0] = new Sphere(new Vector3f(positions[0].x, positions[0].y, positions[0].z), 0.1f, sphereTexture);
		anchorSpots[1] = new Sphere(new Vector3f(positions[1].x, positions[1].y, positions[1].z), 0.1f, sphereTexture);
	}
	
	public List<Sphere> getSpheres(){
		return tool.getSpheres();
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
		case GLFW_KEY_1: if(tool.getClass()!= PrismTool.class) tool = new PrismTool(); break;
		case GLFW_KEY_2: if(tool.getClass()!= QuadTool.class) tool = new QuadTool(); break;
		case GLFW_KEY_3: if(tool.getClass()!= CurveTool.class) tool = new CurveTool(); break;
		case GLFW_KEY_4: if(tool.getClass()!= PointerTool.class) tool = new PointerTool(); break;
		default: tool.processInput(key, action);
		}
	}
	
	public List<ParticlePath> getParticleStreams() {
		return tool.getParticleStreams();
	}
	
	
	
	private interface Tool{
		public void createTool();
		public void processInput(int key, int action);
		public void move();
		public List<Sphere> getSpheres();
		public List<ParticlePath> getParticleStreams();
	}
	
	
	private class PrismTool implements Tool{
		private Sphere movableSpot;
		//damit movableSpot abwechselnd links/rechts erstellt wird
		private short nextSide = 1;
		
		public void createTool() {
			float posY = 1;
			Vector3f spotPos = new Vector3f();
			Vector3f.sub(camera.getPosition(), camera.getDirection(0, 12), spotPos);
			if(anchorSpots[0]!=null)
				posY = anchorSpots[0].getPosition().y;
				movableSpot = new Sphere(spotPos, 0.1f, sphereTexture);
			Vector3f.add(spotPos, camera.getDirection(nextSide * 90, 3.5f), movableSpot.getPosition());
			nextSide*=-1;
			movableSpot.getPosition().y = posY;
		}

		@Override
		public void processInput(int key, int action) {
    		switch(key){
    		case GLFW_KEY_ENTER:
    			currentTrackpart = new Trackpart(anchorSpots[0].getPosition(),
    											anchorSpots[1].getPosition(),
    											movableSpot.getPosition(),
    											chunkMap);
    			setAnchorSpots();
    			createTool();
				break;
    		}
		}

		@Override
		public void move() {
			try{
				if(glfwGetKey(window, GLFW_KEY_UP)==GLFW_PRESS){
					Vector3f dir = new Vector3f();
					Vector3f.sub(movableSpot.getPosition(), camera.getPosition(), dir);
					dir.normalise();
					dir.scale(0.1f);
					movableSpot.increasePosition(dir.x, 0, dir.z);
				}
				if(glfwGetKey(window, GLFW_KEY_DOWN)==GLFW_PRESS){
					Vector3f dir = new Vector3f();
					Vector3f.sub(movableSpot.getPosition(), camera.getPosition(), dir);
					dir.normalise();
					dir.scale(-0.1f);
					movableSpot.increasePosition(dir.x, 0, dir.z);
				}
		
				if(glfwGetKey(window, GLFW_KEY_LEFT)==GLFW_PRESS){
					movableSpot.increasePosition(camera.getDirection(-90, 0.1f));
				}
				if(glfwGetKey(window, GLFW_KEY_RIGHT)==GLFW_PRESS){
					movableSpot.increasePosition(camera.getDirection(+90, 0.1f));
				}
				if(glfwGetKey(window, GLFW_KEY_RIGHT_SHIFT)==GLFW_PRESS){
					movableSpot.increasePosition(0, 0.1f, 0);
				}
				if(glfwGetKey(window, GLFW_KEY_SLASH)==GLFW_PRESS){ //Minus-Taste, US-Layout und so..
					if(movableSpot.getPosition().y>0.6)
						movableSpot.increasePosition(0, -0.1f, 0);
					
				}
			}catch(NullPointerException e){
			}
		}

		@Override
		public List<Sphere> getSpheres() {
			List<Sphere> res = new LinkedList<>();
			if(movableSpot!=null)res.add(movableSpot);
			res.add(anchorSpots[0]);
			res.add(anchorSpots[1]);
			return res;
		}

		@Override
		public List<ParticlePath> getParticleStreams() {
			return new ArrayList<ParticlePath>();
		}
	}
	
	
	private class QuadTool implements Tool{
		private Sphere[] movableSpots;
		private List<ParticlePath> connectionStreams;
		private float pitch=-2, angle=0;
		
		private QuadTool(){
			movableSpots = new Sphere[2];
			connectionStreams = new ArrayList<>();
			for(int i=0; i<2; i++){
				connectionStreams.add(new ParticlePath(new ArrayList<Vector3f>(), camera));
			}
			
			createTool();
		}
		
		public void createTool() {
			float posY = 1;
			Vector3f spotPos0 = camera.getPickResult(angle, pitch);
			Vector3f spotPos1 = spotPos0.duplicate();
			
			Vector3f.add(spotPos0, camera.getDirection(90, 8f), spotPos0);
			Vector3f.add(spotPos1, camera.getDirection(-90, 8f), spotPos1);
			
			posY = anchorSpots[0].getPosition().y;
			movableSpots[0] = new Sphere(spotPos0, 0.1f, sphereTexture);
			movableSpots[1] = new Sphere(spotPos1, 0.1f, sphereTexture);
			
			movableSpots[0].getPosition().y = posY;
			movableSpots[1].getPosition().y = posY;
			
			refreshParticlePath();
		}
		
		private void refreshParticlePath(){
			Vector3f center = new Vector3f();
			Vector3f.add(movableSpots[0].getPosition(), movableSpots[1].getPosition(), center);
			center.scale(0.5f);
			
			for(int i=0; i<=1; i++){
				List<Vector3f> path = connectionStreams.get(i).getPath();
				path.clear();
				path.add(anchorSpots[i].getPosition());
				path.add(movableSpots[i].getPosition());
				path.add(center);
			}
		}
		
		private void createPrisms(){
			new Trackpart(anchorSpots[0].getPosition(),
						anchorSpots[1].getPosition(),
						movableSpots[0].getPosition(),
						chunkMap);
			currentTrackpart = new Trackpart(anchorSpots[1].getPosition(),
					movableSpots[0].getPosition(),
					movableSpots[1].getPosition(),
						chunkMap);
			setAnchorSpots();
			createTool();
		}

		@Override
		public void processInput(int key, int action) {
    		switch(key){
    		case GLFW_MOUSE_BUTTON_LEFT: if(glfwGetInputMode(window, GLFW_CURSOR)!=GLFW_CURSOR_DISABLED)break;
    		case GLFW_KEY_ENTER:
    			createPrisms();
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
					createTool();
				}
				
				if(glfwGetKey(window, GLFW_KEY_LEFT)==GLFW_PRESS){
					angle+=0.5f;
					createTool();
				}
				if(glfwGetKey(window, GLFW_KEY_RIGHT)==GLFW_PRESS){
					angle-=0.5f;
					createTool();
				}
				if(glfwGetKey(window, GLFW_KEY_UP)==GLFW_PRESS){
					pitch-=0.2f;
					createTool();
				}
				if(glfwGetKey(window, GLFW_KEY_DOWN)==GLFW_PRESS){
					pitch+=0.2f;
					createTool();
				}
				if(glfwGetKey(window, GLFW_KEY_RIGHT_SHIFT)==GLFW_PRESS){
					movableSpots[0].increasePosition(0, 0.1f, 0);
					movableSpots[1].increasePosition(0, 0.1f, 0);
					refreshParticlePath();
				}
				if(glfwGetKey(window, GLFW_KEY_SLASH)==GLFW_PRESS){ //Minus-Taste, US-Layout und so..
					if(movableSpots[0].getPosition().y>-10){//TODO: überlegen, ob Terrainhöhe als Untergrenze
						movableSpots[0].increasePosition(0, -0.1f, 0);
						movableSpots[1].increasePosition(0, -0.1f, 0);
						refreshParticlePath();
					}
				}
				if(glfwGetInputMode(window, GLFW_CURSOR)==GLFW_CURSOR_DISABLED){
					DoubleBuffer xpos = BufferUtils.createDoubleBuffer(1);
					DoubleBuffer ypos = BufferUtils.createDoubleBuffer(1);
					GLFW.glfwGetCursorPos(window, xpos, ypos);
					
					if(xpos.get()!=0 || ypos.get()!=0)
						createTool();
				}
			}catch(NullPointerException e){
			}
		}

		@Override
		public List<Sphere> getSpheres() {
			List<Sphere> res = new LinkedList<>();
			if(movableSpots[0]!=null){
				res.add(movableSpots[0]);
				res.add(movableSpots[1]);
			}
			res.add(anchorSpots[0]);
			res.add(anchorSpots[1]);
			return res;
		}

		@Override
		public List<ParticlePath> getParticleStreams() {
			return connectionStreams;
		}
	}

	
	private class CurveTool implements Tool{
		private Sphere movableSpot;
		//damit movableSpot abwechselnd links/rechts erstellt wird
		private short nextSide = 1;
		
		public void createTool() {
			float posY = 1;
			Vector3f spotPos = new Vector3f();
			Vector3f.sub(camera.getPosition(), camera.getDirection(0, 12), spotPos);
			if(movableSpot!=null)
				posY = movableSpot.getPosition().y;
				movableSpot = new Sphere(spotPos, 0.2f, sphereTexture);
			Vector3f.add(spotPos, camera.getDirection(nextSide * 90, 3.5f), movableSpot.getPosition());
			nextSide*=-1;
			movableSpot.getPosition().y = posY;
		}

		@Override
		public void processInput(int key, int action) {
    		switch(key){
    		case GLFW_KEY_ENTER:
    			currentTrackpart = new Trackpart(anchorSpots[0].getPosition(),
    											anchorSpots[1].getPosition(),
    											movableSpot.getPosition(),
    											chunkMap);
    			setAnchorSpots();
    			createTool();
				break;
		}
			
		}

		@Override
		public void move() {
			try{
				if(glfwGetKey(window, GLFW_KEY_UP)==GLFW_PRESS){
					Vector3f dir = new Vector3f();
					Vector3f.sub(movableSpot.getPosition(), camera.getPosition(), dir);
					dir.normalise();
					dir.scale(0.1f);
					movableSpot.increasePosition(dir.x, 0, dir.z);
				}
				if(glfwGetKey(window, GLFW_KEY_DOWN)==GLFW_PRESS){
					Vector3f dir = new Vector3f();
					Vector3f.sub(movableSpot.getPosition(), camera.getPosition(), dir);
					dir.normalise();
					dir.scale(-0.1f);
					movableSpot.increasePosition(dir.x, 0, dir.z);
				}
		
				if(glfwGetKey(window, GLFW_KEY_LEFT)==GLFW_PRESS){
					movableSpot.increasePosition(camera.getDirection(-90, 0.1f));
				}
				if(glfwGetKey(window, GLFW_KEY_RIGHT)==GLFW_PRESS){
					movableSpot.increasePosition(camera.getDirection(+90, 0.1f));
				}
				if(glfwGetKey(window, GLFW_KEY_RIGHT_SHIFT)==GLFW_PRESS){
					movableSpot.increasePosition(0, 0.1f, 0);
				}
				if(glfwGetKey(window, GLFW_KEY_SLASH)==GLFW_PRESS){ //Minus-Taste, US-Layout und so..
					if(movableSpot.getPosition().y>0.6)
						movableSpot.increasePosition(0, -0.1f, 0);
					
				}
			}catch(NullPointerException e){
			}
		}

		@Override
		public List<Sphere> getSpheres() {
			List<Sphere> res = new LinkedList<>();
			if(movableSpot!=null)res.add(movableSpot);
			res.add(anchorSpots[0]);
			res.add(anchorSpots[1]);
			return res;
		}

		@Override
		public List<ParticlePath> getParticleStreams() {
			return new ArrayList<ParticlePath>();
		}
	}
	
	
	private class PointerTool implements Tool{
		private Sphere movableSpot;
		//damit movableSpot abwechselnd links/rechts erstellt wird
		private short nextSide = 1;
		
		public void createTool() {
			float posY = 1;
			Vector3f spotPos = new Vector3f();
			Vector3f.sub(camera.getPosition(), camera.getDirection(0, 12), spotPos);
			if(movableSpot!=null)
				posY = movableSpot.getPosition().y;
				movableSpot = new Sphere(spotPos, 0.1f, sphereTexture);
			Vector3f.add(spotPos, camera.getDirection(nextSide * 90, 3.5f), movableSpot.getPosition());
			nextSide*=-1;
			movableSpot.getPosition().y = posY;
		}

		@Override
		public void processInput(int key, int action) {
    		switch(key){
    		case GLFW_KEY_ENTER:
    			currentTrackpart = new Trackpart(anchorSpots[0].getPosition(),
    											anchorSpots[1].getPosition(),
    											movableSpot.getPosition(),
    											chunkMap);
    			setAnchorSpots();
    			createTool();
				break;
		}
			
		}

		@Override
		public void move() {
			try{
				if(glfwGetKey(window, GLFW_KEY_UP)==GLFW_PRESS){
					Vector3f dir = new Vector3f();
					Vector3f.sub(movableSpot.getPosition(), camera.getPosition(), dir);
					dir.normalise();
					dir.scale(0.1f);
					movableSpot.increasePosition(dir.x, 0, dir.z);
				}
				if(glfwGetKey(window, GLFW_KEY_DOWN)==GLFW_PRESS){
					Vector3f dir = new Vector3f();
					Vector3f.sub(movableSpot.getPosition(), camera.getPosition(), dir);
					dir.normalise();
					dir.scale(-0.1f);
					movableSpot.increasePosition(dir.x, 0, dir.z);
				}
		
				if(glfwGetKey(window, GLFW_KEY_LEFT)==GLFW_PRESS){
					movableSpot.increasePosition(camera.getDirection(-90, 0.1f));
				}
				if(glfwGetKey(window, GLFW_KEY_RIGHT)==GLFW_PRESS){
					movableSpot.increasePosition(camera.getDirection(+90, 0.1f));
				}
				if(glfwGetKey(window, GLFW_KEY_RIGHT_SHIFT)==GLFW_PRESS){
					movableSpot.increasePosition(0, 0.1f, 0);
				}
				if(glfwGetKey(window, GLFW_KEY_SLASH)==GLFW_PRESS){ //Minus-Taste, US-Layout und so..
					if(movableSpot.getPosition().y>0.6)
						movableSpot.increasePosition(0, -0.1f, 0);
					
				}
			}catch(NullPointerException e){
			}
		}

		@Override
		public List<Sphere> getSpheres() {
			List<Sphere> res = new LinkedList<>();
			if(movableSpot!=null)res.add(movableSpot);
			res.add(anchorSpots[0]);
			res.add(anchorSpots[1]);
			return res;
		}

		@Override
		public List<ParticlePath> getParticleStreams() {
			return new ArrayList<ParticlePath>();
		}
	}
}
