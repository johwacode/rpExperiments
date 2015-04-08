package rpEngine.graphical.objects;

import static org.lwjgl.system.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.system.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.system.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_V;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.system.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.system.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.system.glfw.GLFW.glfwGetInputMode;
import static org.lwjgl.system.glfw.GLFW.glfwGetKey;
import static org.lwjgl.system.glfw.GLFW.glfwSetInputMode;
import game.SceneGraph;

import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.glfw.GLFW;

import rpEngine.graphical.objects2d.DebugLine;
import rpEngine.graphical.structs.UserController;
import rpEngine.vehicle.Vehicle;
import utils.math.Matrix4f;
import utils.math.Vector3f;

public class Camera implements UserController{
	public enum Mode {
		FIRST_PERSON,
		THIRD_PERSON,
		FREE_MOVEMENT
	}

	private Vector3f position;
	public float pitch;	//(Höhe)
	public float yaw;		//left/right 
	private float roll;		//Neigung
	private long window;
	private CameraMode currentMode;
	private Vehicle vehicle = null;
	private SceneGraph scene;
	
	private Matrix4f viewMatrix;
	private boolean matrixUpToDate = false;

	
	public Camera(long window, Vector3f position, SceneGraph scene){
		this.window = window;
		this.position = position;
		currentMode = new FreeMovementMode();
		this.scene = scene;
		checkTerrainHeight();
	}
	
	public void nextMode(){
		currentMode.switchToNextMode();
		DebugLine.addMessage(""+currentMode.getEnum());
		matrixUpToDate = false;
	}
	
	public void setVehicle(Vehicle vehicle){
		this.vehicle = vehicle;
		currentMode = new ThirdPersonMode();
		matrixUpToDate = false;
	}
	
	/**
	 * @return true if collision is detected, false otherwise. Also adjusts the height, if needed. (still little rough)
	 */
	public boolean checkForCollision(Vector3f direction){
		//TODO: rewrite when Curve-collisionDetection is working 
		//create testpoint
		Vector3f testPoint = Vector3f.add(position, direction);
		//set yMin=terrain.height, yMax=250;
		float yPlayerBottom = testPoint.y-2, yPlayerTop = testPoint.y+0.5f;
		float highestBeneath = scene.getTerrain().getTerrainHeight(testPoint.x, testPoint.z);
		if(yPlayerBottom<highestBeneath)return true;
		float lowestAbove=250;
		if(yPlayerTop>lowestAbove)return true;
		
		//If no intersection save highestBeneath and lowestAbove
		try{
			for(Curve curve: scene.getChunkMap().getModels(testPoint.x, testPoint.z)){
				Vector3f p = curve.getClosestIntersection(testPoint, direction);
				if(p!=null){
					float distSQ = Vector3f.sub(testPoint, p).length2();
					//debugsphere:
					SceneGraph.addDebugSphere(p);
					System.out.println("Point: "+p);
					System.out.println("Distance² to Point: "+distSQ);
					if(distSQ<2) return true;	
				}
			}
			//if playerleaves ChunkMap:
		} catch(ArrayIndexOutOfBoundsException e){
			System.err.println("Player out of Map-area");
			if(position.y+direction.y<5){
				position.y=5;
				direction.y=0;
			}
		}
		return false;
		
	}
	
	/**
	 * at current Position -> collisionDetection
	 */
	public void checkTerrainHeight(){
		position.y = Math.max(position.y, scene.getTerrain().getTerrainHeight(position.x, position.z)+20);
	}
	
	/**
	 * next point in angle/pitch from camPosition (on Terrain) 
	 */
	public Vector3f getPickResult(float angle, float pitch){
		Terrain terrain = scene.getTerrain();
		position.y = Math.max(position.y, terrain.getTerrainHeight(position.x, position.z)+3);
		Vector3f direction = getDirection(angle, 0.5f);
		direction.y = (float) Math.toRadians(pitch+this.pitch);
		Vector3f test = position.duplicate();
		for(int i=0; i<150;i++){
			Vector3f.sub(test, direction, test);
			if(terrain.getTerrainHeight(test.x, test.z)+1>test.y)return test;
		}
		return test;
	}
	
	
	public Matrix4f getViewMatrix(){
		if(!matrixUpToDate) viewMatrix = Matrix4f.createViewMatrix(getPitch(), getYaw(), getPosition());
		return viewMatrix;
	}
	
	/**
	 * berechnet einen Vector in der x-z-Ebene, der von der Sichtrichtung um eine angegebene Gradzahl abweicht
	 * @param angle Winkel in Grad, im Uhrzeigersinn
	 * @param scale Länge des berechneten Vectors
	 * @return
	 */
	public Vector3f getDirection(float angle, float scale){
		double radians = -Math.toRadians(yaw-angle);
		Vector3f direction = new Vector3f(
				(float) (Math.sin(radians)),
							0,
				(float) (Math.cos(radians))
				);
		direction.normalise();
		direction.scale(scale);
		return direction;
	}
	
	public Vector3f getPosition() {
		return currentMode.getPosition();
	}

	public float getPitch() {
		return currentMode.getPitch();
	}

	public float getYaw() {
		return currentMode.getYaw();
	}

	public float getRoll() {
		return roll;
	}
	
	public long getWindow(){
		return window;
	}
	
	public void move(){
		currentMode.move();
	}
	
	
	/**
	 * verarbeitet einfache Klicks per Maus & Tastatur.
	 * Aufruf durch Callback-Adapter.
	 */
	public void processInput(int key, int action) {
		if(action!=GLFW_PRESS) return;
    		switch(key){
    		case GLFW_KEY_V:
				this.nextMode();
				break;
    		case GLFW_KEY_ESCAPE:
    			glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    			break;
    		case GLFW_MOUSE_BUTTON_LEFT:
    			glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    			GLFW.glfwSetCursorPos(window, 0, 0);
    			break;
		}
    }
	
	private abstract class CameraMode{
		protected abstract Mode getEnum();
		protected abstract void switchToNextMode();
		protected Vector3f getPosition(){return position;}
		protected float getPitch(){return pitch;}
		protected float getYaw(){return yaw;}
		protected void move(){}
	}
	
	
	
	private class FirstPersonMode extends CameraMode{
		@Override
		protected Mode getEnum(){return Mode.FIRST_PERSON;}
		
		@Override
		protected void switchToNextMode(){
			currentMode = new ThirdPersonMode(); 
		}
		
		@Override
		public Vector3f getPosition() {
			Vector3f.add(vehicle.getPosition(), new Vector3f(0,0.9f,0), position);
			return position;
		}
		
		@Override
		public float getPitch(){
			//TODO: only for flatland
			return -2;
		}
		
		@Override
		public float getYaw(){
			float angle = (float) Math.acos(vehicle.getCurrentDirection().z / vehicle.getCurrentDirection().length());
			if(vehicle.getCurrentDirection().x>0)angle*=-1;
			yaw = (float) Math.toDegrees(angle);
			return yaw;
		}
	}
	
	private class ThirdPersonMode extends CameraMode{
		@Override
		protected Mode getEnum(){return Mode.THIRD_PERSON;}
		
		@Override
		protected void switchToNextMode(){
			currentMode = new FreeMovementMode();
		}
		
		@Override
		public Vector3f getPosition() {
			Vector3f distance = new Vector3f();
			vehicle.getCurrentDirection().negate(distance);
			distance.scale(50);
			distance.y-=5;
			Vector3f.sub(vehicle.getPosition(), distance, position);
			return position;
		}
		
		@Override
		public float getPitch(){
			//TODO: only for flatland
			return 27;
		}
		
		@Override
		public float getYaw(){
			float angle = (float) Math.acos(vehicle.getCurrentDirection().z / vehicle.getCurrentDirection().length());
			if(vehicle.getCurrentDirection().x>0)angle*=-1;
			yaw = (float) Math.toDegrees(angle);
			return yaw;
		}
	}
	
	private class FreeMovementMode extends CameraMode{
		@Override
		protected Mode getEnum(){return Mode.FREE_MOVEMENT;}
		
		@Override
		protected void switchToNextMode(){
			if(vehicle!=null)
				currentMode = new FirstPersonMode(); 
		}
		
		/**
		 * prüft bei Aufruf auf auszuführende Inputs.
		 * ->WASD...
		 */
		@Override
		protected void move(){
			Vector3f direction = new Vector3f();
			if(glfwGetKey(window, GLFW_KEY_W)==GLFW_PRESS){
				Vector3f.sub(direction, getDirection(0, 0.3f), direction);
			}
			if(glfwGetKey(window, GLFW_KEY_A)==GLFW_PRESS){
				Vector3f.add(direction, getDirection(-90, 0.20f), direction);
			}
			if(glfwGetKey(window, GLFW_KEY_S)==GLFW_PRESS){
				Vector3f.add(direction, getDirection(0, 0.2f), direction);
			}
			if(glfwGetKey(window, GLFW_KEY_D)==GLFW_PRESS){
				Vector3f.add(direction, getDirection(+90, 0.2f), direction);
			}
			if(glfwGetKey(window, GLFW_KEY_SPACE)==GLFW_PRESS){
				direction.y+=0.2;
			}
			if(glfwGetKey(window, GLFW_KEY_LEFT_SHIFT)==GLFW_PRESS){
				direction.y-=0.2;
			}
			if(direction.length2()>0 && !checkForCollision(direction)){
				Vector3f.add(position, direction, position);
				matrixUpToDate = false;
			}
			
			
			if(glfwGetInputMode(window, GLFW_CURSOR)==GLFW_CURSOR_DISABLED){
				DoubleBuffer xpos = BufferUtils.createDoubleBuffer(1);
				DoubleBuffer ypos = BufferUtils.createDoubleBuffer(1);
				GLFW.glfwGetCursorPos(window, xpos, ypos);
				
				float mouseDX = (float) xpos.get();
				if(mouseDX!=0){
					yaw = (yaw + mouseDX/5)%360;
					while(yaw<0) yaw+=360;

					matrixUpToDate = false;
				}
				float mouseDY = (float) ypos.get();
				if(mouseDY!=0){
					pitch+=mouseDY/5;
					if (pitch<-90) pitch = -90;
					else if(pitch>90) pitch = 90;
					matrixUpToDate = false;
				}
			}
		}
	}
}