package rpEngine.vehicle;

import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_END;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_RIGHT_CONTROL;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.system.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.system.glfw.GLFW.glfwGetKey;
import rpEngine.graphical.structs.UserController;

public class UserInteractionController implements UserController{
	private Vehicle vehicle;
	private long window;
	
	public UserInteractionController(long window){
		this.window = window;
	}
	
	public void setVehicle(Vehicle vehicle){
		this.vehicle = vehicle;
	}
	
	public void move(){
		if(glfwGetKey(window, GLFW_KEY_UP)==GLFW_PRESS){
			vehicle.getEngine().fuel(0.4f);
		}
		if(glfwGetKey(window, GLFW_KEY_DOWN)==GLFW_PRESS){
			//TODO: bremsen
		}

		if(glfwGetKey(window, GLFW_KEY_LEFT)==GLFW_PRESS){
			vehicle.getSteering().turnLeft(0.5f);
		}
		if(glfwGetKey(window, GLFW_KEY_RIGHT)==GLFW_PRESS){
			vehicle.getSteering().turnRight(0.5f);
		}
	}
	
	public void processInput(int key, int action) {
		if(action!=GLFW_PRESS) return;
    		switch(key){
    		case GLFW_KEY_RIGHT_CONTROL:
				vehicle.startOrStop();
				break;
    		case GLFW_KEY_RIGHT_SHIFT:
				vehicle.getTransmissionUnit().increaseGear();
				break;
    		case GLFW_KEY_END:
				vehicle.getTransmissionUnit().releaseGear();
				break;
		}
	}
}
