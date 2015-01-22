package game.menu;

import game.RacingPlanetsGame;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

public class MenuController {
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
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void setMenu(Menu menu){
		menuLevels.add(0, menu);
	}
	
	public void backToParent(){
		if(menuLevels.size()>1){
			menuLevels.remove(0);
		}
	}
	
	public Menu getCurrent(){
		return menuLevels.get(0);
	}
	
	public RacingPlanetsGame getGame(){
		return game;
	}
}
