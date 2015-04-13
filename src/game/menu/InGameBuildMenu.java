package game.menu;

import game.BuilderTool;
import game.RacingPlanetsGame.RPGameMode;
import rpEngine.graphical.objects2d.Button;

public class InGameBuildMenu extends InGameMenu {
	private BuilderTool tool;

	public InGameBuildMenu(MenuController ctrl) {
		super(ctrl);
	}
	public void setTool(BuilderTool tool){
		this.tool = tool;
	}

	@Override
	protected void createVisibleStuff(){
		addImage("rp_logo", 3f, 4f, 5f, 4.5f);
		buttons.add(new Button("save", "SAVE MAP", 3.5f, 4.3f, true));
		buttons.add(new Button("continue", "CONTINUE", 3.5f, 3.2f, true));
		buttons.add(new Button("exit", "EXIT", 3.5f, 2.1f, true));
		setReady();
	}
	
	@Override
	protected void handleClickEvent(String objName) {
		switch(objName){
		case "save": tool.saveTrack(); break;
		case "continue": deactivateThis(); break;
		case "exit": setGameMode(RPGameMode.MENUMODE); break;
		}
	}
}
