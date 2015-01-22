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
	protected List<Clickable> clickableElements = new ArrayList<>(); //3d-Area?, Button
	protected List<String> textElements = new ArrayList<>();
	protected static Clickable currentHovered;
	private boolean isReady = false;
	
	protected Menu currentSubMenu;
	
	private static ByteBuffer buffer1 = BufferUtils.createByteBuffer(Double.BYTES);
	private static ByteBuffer buffer2= BufferUtils.createByteBuffer(Double.BYTES);
	
	protected void setReady(){
		GLFW.glfwWaitEvents();
		isReady = true;
	}
	
	private boolean isReady(){
		if(currentSubMenu!=null)return currentSubMenu.isReady();
		return isReady;
	}
		
	@Override
	public List<Model2D> getModels(){
		if(currentSubMenu!=null) return currentSubMenu.getModels();
		else{
			List<Model2D> res = new LinkedList<>();
			res.addAll(super.getModels());
			for(Button btn : buttons) res.addAll(btn.getModels());
			return res;
		}
	}
	
	public List<Clickable> getClickables(){
		if(currentSubMenu!=null) return currentSubMenu.getClickables();
		List<Clickable> res = new LinkedList<>();
		res.addAll(clickableElements);
		res.addAll(buttons);
		return res;
	}
	
	public boolean handleMouseMovement(long window){
		if(currentSubMenu!=null) return currentSubMenu.handleMouseMovement(window);
		glfwGetCursorPos(window, buffer1, buffer2);		
		float mouseX = (float) buffer1.asDoubleBuffer().get();
		float mouseY = (float) buffer2.asDoubleBuffer().get();
		glfwGetWindowSize(window, buffer1, buffer2);
		mouseX/=buffer1.asIntBuffer().get()/10;
		mouseY=(1-mouseY/buffer2.asIntBuffer().get())*10;	//TODO: change MenuPosition on resize (and gamerendering)
		
		for(Clickable obj: getClickables()){
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
	public String click(){return (currentSubMenu==null)? this.getClass().getSimpleName() : currentSubMenu.click();}
	
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
		if(!isReady()) return;
		if(currentSubMenu!=null) currentSubMenu.handleClickEvent(objName);
		return;
	}
	
	protected void switchToMenu(Menu submenu){
		//delete Text
		for(String key: textElements){
			Text.deleteString(key);
		}
		textElements.clear();
		for(Button btn: buttons){
			Text.deleteString(btn.getID());
		}
		buttons.clear();
		//set subMenu
		currentSubMenu = submenu;
	}
}