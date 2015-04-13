package game.menu;

import game.RacingPlanetsGame.RPGameMode;
import rpEngine.graphical.objects2d.Button;
import rpEngine.graphical.objects2d.text.Text;

public class InGameBuildMenu extends Menu{
	
	public InGameBuildMenu(MenuController ctrl){
		super(ctrl);
		addImage("rp_logo", 3f, 4f, 5f, 4.5f);
		textElements.add(Text.createString("under_construction", "SORRY, NO INTERACTION HERE YET.", 2.2f, 1f));
		buttons.add(new Button("save", "SAVE MAP", 3.5f, 4.3f, true));
		buttons.add(new Button("continue", "CONTINUE", 3.5f, 3.2f, true));
		buttons.add(new Button("exit", "EXIT", 3.5f, 2.1f, true));
	}

	@Override
	protected void handleClickEvent(String objName) {
		switch(objName){
		case "save": break;
		case "continue": break;
		case "exit": controller.getGame().setMode(RPGameMode.MENUMODE); break;
		}
	}
}
