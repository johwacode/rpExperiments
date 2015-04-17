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
import game.InputController;
import rpEngine.graphical.structs.InputHandler;

public class UserInteractionController extends VehicleController implements InputHandler{
	public UserInteractionController(String driverName, VehiclePosition position, Vehicle vehicle){
		super(driverName, position, vehicle);
		InputController.registerHandler(this);
	}
	
	public void move(long window){
		if(glfwGetKey(window, GLFW_KEY_UP)==GLFW_PRESS){
			super.fuel(0.6f);
		}
		if(glfwGetKey(window, GLFW_KEY_DOWN)==GLFW_PRESS){
			super.useBreak(0.7f);
		}

		if(glfwGetKey(window, GLFW_KEY_LEFT)==GLFW_PRESS){
			super.turnLeft(10);
		}
		if(glfwGetKey(window, GLFW_KEY_RIGHT)==GLFW_PRESS){
			super.turnRight(10);
		}
	}
	
	public boolean processInput(int key, int action) {
		if(action!=GLFW_PRESS) return false;
    		switch(key){
    		case GLFW_KEY_RIGHT_CONTROL:
				//vehicle.startOrStop();
				break;
    		case GLFW_KEY_RIGHT_SHIFT:
				//vehicle.getTransmissionUnit().increaseGear();
				break;
    		case GLFW_KEY_END:
				//vehicle.getTransmissionUnit().releaseGear();
				break;
		}
    	return false;
	}

	@Override
	public int getInputHandlingPriority() {
		return 7;
	}
}
