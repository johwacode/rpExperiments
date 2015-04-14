package game.menu;

import game.RacingPlanetsGame.RPGameMode;
import rpEngine.graphical.objects2d.Button;
import rpEngine.graphical.objects2d.text.Text;

public class MainMenu extends Menu{
	
	public MainMenu(MenuController ctrl){
		super(ctrl);
		addImage("rp_logo", 3f, 4f, 5f, 4.5f);
		textElements.add(Text.createString("version", "v.0.08-1", 8.2f, 0.5f));
		buttons.add(new Button("race", "START RACE", 3.5f, 4.8f, true));
		buttons.add(new Button("buildMode", "BUILD-MODE", 3.5f, 3.8f, true));
		buttons.add(new Button("load", "LOAD TRACK", 3.5f, 2.8f, true));
		buttons.add(new Button("quitGame", "QUIT GAME", 3.5f, 1.2f, true));
		setReady();
	}

	@Override
	protected void handleClickEvent(String objName) {		
		switch(objName){
		case "buildMode": setGameMode(RPGameMode.BUILDMODE); break;
		case "race": switchToSubMenu(new StartRaceMenu(controller)); break;
		case "load": switchToSubMenu(new LoadTrackMenu(controller)); break;
		case "quitGame": controller.getGame().quitGame(); break;
		}
	}
}
