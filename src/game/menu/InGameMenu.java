package game.menu;

import static org.lwjgl.system.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.system.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.system.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.system.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.system.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.system.glfw.GLFW.glfwSetInputMode;
import game.InputController;

import org.lwjgl.system.glfw.GLFW;

import rpEngine.graphical.structs.InputHandler;

public abstract class InGameMenu extends Menu{
	public boolean isActive = false;
	private ActivationHandler activationHandler;
	
	public InGameMenu(MenuController ctrl){
		super(ctrl);
		activationHandler = new ActivationHandler();
		InputController.registerHandler(activationHandler);
	}
	
	protected abstract void createVisibleStuff();
	
	protected void deactivateThis(){
		InputController.removeHandler(controller);
		clear();
		isActive = false;
		glfwSetInputMode(InputController.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		GLFW.glfwSetCursorPos(InputController.getWindow(), 0, 0);
	}
	
	public void activate(){
		if(isActive)return;
		InputController.registerHandler(controller);
		createVisibleStuff();
		isActive = true;
		glfwSetInputMode(InputController.getWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
	}
	
	private class ActivationHandler implements InputHandler{
		@Override
		public int getInputHandlingPriority() {
			return 9;
		}

		@Override
		public boolean processInput(int key, int action) {
			if(action==GLFW_PRESS&&key==GLFW_KEY_ESCAPE){
				if(isActive)deactivateThis();
				else activate();
				return true;
			}
			if(action!=GLFW_PRESS) return false;
			switch(key){
			case GLFW_KEY_ESCAPE:
				if(isActive)deactivateThis();
				else activate();
				return true;
			default: return false;
			}
		}

		@Override
		public void move(long window) {	
		}
	}
}
