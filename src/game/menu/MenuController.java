package game.menu;

import game.InputController;
import game.RacingPlanetsGame;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import rpEngine.graphical.structs.InputHandler;

public class MenuController implements InputHandler{
	private List<Menu> menuLevels;
	private RacingPlanetsGame game;
	
	public MenuController(RacingPlanetsGame game, Class<? extends Menu> topLevelMenuClazz){
		this.game = game;
		menuLevels = new LinkedList<>();
		try {
			menuLevels.add(topLevelMenuClazz.getDeclaredConstructor(this.getClass()).newInstance(this));
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			System.err.println("InvocationTargetException caused by:");
			e.getCause().printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		InputController.registerHandler(this);
	}
	
	public void setMenu(Menu menu){
		menuLevels.add(0, menu);
	}
	
	public void backToParent(){
		if(menuLevels.size()>1){
			menuLevels.remove(0);
		}
		try {
			menuLevels.set(0, menuLevels.get(0).getClass().getDeclaredConstructor(this.getClass()).newInstance(this));
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public Menu getCurrent(){
		return menuLevels.get(0);
	}
	
	public RacingPlanetsGame getGame(){
		return game;
	}

	@Override
	public int getInputHandlingPriority() {
		return 8;
	}

	@Override
	public boolean processInput(int key, int action) {
		return getCurrent().processInput(key, action);
	}

	@Override
	public void move(long window) {
		getCurrent().handleMouseMovement(window);
	}
}
