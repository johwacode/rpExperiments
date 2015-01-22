package game.menu;

import game.RacingPlanetsGame.RPGameMode;
import rpEngine.graphical.objects2d.Button;
import rpEngine.graphical.objects2d.HUDModelGenerator;
import rpEngine.graphical.objects2d.text.Text;

public class InGameBuildMenu extends Menu{
	
	public InGameBuildMenu(MenuController ctrl){
		super(ctrl);
		this.addModel(HUDModelGenerator.createRectangle(3f, 4f, 5f, 4.5f, "rp_logo"));
		this.textElements.add(Text.createString("version", "v.0.07.5", 8, 0.5f));
		buttons.add(new Button("btn1", "..........", 3.5f, 4.3f, false));
		buttons.add(new Button("buildMode", "BUILD-MODE", 3.5f, 3.2f, true));
		buttons.add(new Button("btn3", "..........", 3.5f, 2.1f, false));
	}

	@Override
	protected void handleClickEvent(String objName) {
		switch(objName){
		case "buildMode": controller.getGame().setMode(RPGameMode.BUILDMODE);
		}
	}
}
