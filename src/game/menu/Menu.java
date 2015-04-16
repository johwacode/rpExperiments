package game.menu;

import static org.lwjgl.system.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.system.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.system.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.system.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.system.glfw.GLFW.glfwGetWindowSize;
import game.RacingPlanetsGame.RPGameMode;

import java.io.Serializable;
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
		addImage("mainmenu_background", 0, -7.0f, 12.9f, 17);
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
		for(Button btn : buttons) res.addAll(btn.getModels());
		res.addAll(super.getModels());
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
				//if(currentHovered != obj) System.out.println("currentHovered: "+obj);
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
	
	public boolean processInput(int key, int action){
		if(!isReady()) return false;
		//TODO: current-wechsel über tastatur
		if(currentHovered==null) return false;
		else{
			if(key==GLFW_MOUSE_BUTTON_LEFT){
				switch(action){
				case GLFW_PRESS: currentHovered.click(); //klickanimation beginnt, eigentl. click aber erst bei release
					break;
				case GLFW_RELEASE:
					System.out.println("klick auf: "+currentHovered.click());
					handleClickEvent(currentHovered.click());
					return true;
				}
			}
		}
		return false;
	}
	
	protected void handleClickEvent(String objName){
		return;
	}
	
	protected void setGameMode(RPGameMode newMode){
		clear();
		controller.getGame().setMode(newMode);
	}
	
	protected void setGameMode(RPGameMode newMode, Serializable data){
		Serializable[] multipleData = {data};
		setGameMode(newMode, multipleData);
	}
	
	protected void setGameMode(RPGameMode newMode, Serializable[] multipleData){
		clear();
		controller.getGame().setMode(newMode, multipleData);
	}
	
	protected void switchToSubMenu(Menu submenu){
		clear();
		controller.setMenu(submenu);
	}
	
	protected void switchToParentMenu(){
		clear();
		controller.backToParent();
	}
	
	protected void clear(){
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