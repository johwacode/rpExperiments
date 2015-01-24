package game.menu;

import static org.lwjgl.system.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.system.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.system.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.system.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.system.glfw.GLFW.glfwGetWindowSize;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.glfw.GLFW;

import rpEngine.graphical.model.Model2D;
import rpEngine.graphical.objects2d.Button;
import rpEngine.graphical.objects2d.Clickable;
import rpEngine.graphical.objects2d.HUDElement;
import rpEngine.graphical.objects2d.text.Text;

public abstract class Menu extends HUDElement implements Clickable{
	protected List<Button> buttons = new ArrayList<>();
	protected List<String> textElements = new ArrayList<>();
	protected static Clickable currentHovered;
	private boolean isReady = false;
	
	public Menu(MenuController ctrl){
		controller = ctrl;
	}
	
	protected MenuController controller;
	
	private static ByteBuffer buffer1 = BufferUtils.createByteBuffer(Double.BYTES);
	private static ByteBuffer buffer2= BufferUtils.createByteBuffer(Double.BYTES);
	
	protected void setReady(){
		GLFW.glfwWaitEvents();
		isReady = true;
	}
	
	private boolean isReady(){
		return isReady;
	}
		
	@Override
	public List<Model2D> getModels(){
		List<Model2D> res = new LinkedList<>();
		res.addAll(super.getModels());
		for(Button btn : buttons) res.addAll(btn.getModels());
		return res;
	}
	
	public List<Button> getButtons(){
		return buttons;
	}
	
	public boolean handleMouseMovement(long window){
		glfwGetCursorPos(window, buffer1, buffer2);		
		float mouseX = (float) buffer1.asDoubleBuffer().get();
		float mouseY = (float) buffer2.asDoubleBuffer().get();
		glfwGetWindowSize(window, buffer1, buffer2);
		mouseX/=buffer1.asIntBuffer().get()/10;
		mouseY=(1-mouseY/buffer2.asIntBuffer().get())*10;	//TODO: change MenuPosition on resize (and gamerendering)
		
		for(Clickable obj: getButtons()){
			if(obj.isInArea(mouseX, mouseY)){
				if(currentHovered != obj) System.out.println("currentHovered: "+obj);
				currentHovered = obj;
				return true;
			}
		}
		currentHovered = null;
		return false;
	}
	
	@Override
	public boolean isInArea(float x, float y){return true;}
	@Override
	public String click(){
		return this.getClass().getSimpleName();
	}
	
	public void processInput(int key, int action){
		if(!isReady()) return;
		//TODO: current-wechsel über tastatur
		if(currentHovered==null) return;
		else if(key==GLFW_MOUSE_BUTTON_LEFT && action==GLFW_PRESS)
			currentHovered.click(); //klickanimation beginnt, eigentl. click aber erst bei release
		else if(key==GLFW_MOUSE_BUTTON_LEFT && action==GLFW_RELEASE)
			System.out.println("klick auf: "+currentHovered.click());
			handleClickEvent(currentHovered.click());
	}
	
	protected void handleClickEvent(String objName){
		return;
	}
	
	protected void switchToSubMenu(Menu submenu){
		clear();
		controller.setMenu(submenu);
	}
	
	protected void switchToParentMenu(){
		clear();
		controller.backToParent();
	}
	
	private void clear(){
		for(String key: textElements){
			Text.deleteString(key);
		}
		textElements.clear();
		for(Button btn: buttons){
			btn.clear();
		}
		buttons.clear();
	}
	
	@Override
	public String toString(){
		String result = this.getClass().getSimpleName();
		result += "\n contains: ";
		result += "\n "+buttons.size()+" buttons.";
		return result;
	}
}