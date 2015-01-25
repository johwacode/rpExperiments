package game.menu;

import game.RacingPlanetsGame.RPGameMode;
import rpEngine.graphical.objects2d.Button;
import rpEngine.graphical.objects2d.HUDModelGenerator;

public class LoadTrackMenu extends Menu{
	
	public LoadTrackMenu(MenuController ctrl){
		super(ctrl);
		this.addModel(HUDModelGenerator.createRectangle(0.5f, 5f, 2.3f, 2f, "rp_logo"));
		buttons.add(new Button("trackname", "TRACKNAME", 3.5f, 4.5f, true));
		buttons.add(new Button("back", "BACK", 3.5f, 2.5f, true));
		setReady();
	}

	@Override
	protected void handleClickEvent(String objName) {
		switch(objName){
		case "trackname": controller.getGame().setMode(RPGameMode.BUILDMODE); break;
		case "back": this.switchToParentMenu(); break;
		}
	}
}