package game.menu;

import game.RacingPlanetsGame;
import game.RacingPlanetsGame.RPGameMode;
import rpEngine.graphical.objects2d.Button;
import rpEngine.graphical.objects2d.HUDModelGenerator;

public class LoadTrackMenu extends Menu{
	private RacingPlanetsGame game;
	
	public LoadTrackMenu(RacingPlanetsGame gameInstance){
		this.game = gameInstance;
		this.addModel(HUDModelGenerator.createRectangle(0.5f, 5f, 2.3f, 2f, "rp_logo"));
		//buttons.add(new Button("trackname", "TRACKNAME", 3.5f, 4.3f, true));
		buttons.add(new Button("back", "BACK", 3.5f, 2.3f, true));
		setReady();
	}

	@Override
	protected void handleClickEvent(String objName) {
		super.handleClickEvent(objName);
		
		switch(objName){
		case "trackname": game.setMode(RPGameMode.BUILDMODE); break;
		case "back": this.switchToMenu(new MainMenu(game)); break;
		}
	}
}