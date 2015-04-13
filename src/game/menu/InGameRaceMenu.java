package game.menu;

import game.RacingPlanetsGame.RPGameMode;
import rpEngine.graphical.objects2d.Button;

public class InGameRaceMenu extends InGameMenu {

	public InGameRaceMenu(MenuController ctrl) {
		super(ctrl);
	}

	@Override
	protected void createVisibleStuff(){
		addImage("rp_logo", 3f, 4f, 5f, 4.5f);
		buttons.add(new Button("continue", "CONTINUE", 3.5f, 4.3f, true));
		buttons.add(new Button("exit", "EXIT", 3.5f, 3.2f, true));
		setReady();
	}
	
	@Override
	protected void handleClickEvent(String objName) {
		switch(objName){
		case "continue": deactivateThis(); break;
		case "exit": setGameMode(RPGameMode.MENUMODE); break;
		}
	}
}
