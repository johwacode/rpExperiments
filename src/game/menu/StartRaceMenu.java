package game.menu;

import game.RacingPlanetsGame.RPGameMode;

import java.io.File;
import java.io.Serializable;

import rpEngine.graphical.objects2d.Button;
import rpEngine.graphical.objects2d.text.Text;
import utils.fileLoader.RPFileLibrary;

public class StartRaceMenu extends Menu{
		File[] files;
	
	public StartRaceMenu(MenuController ctrl){
		super(ctrl);
		//Logo
		addImage("rp_logo", 0.5f, 5f, 2.3f, 2f);
		textElements.add(Text.createString("startRaceMenu_title", "TRACK-CONSTRUCTOR", 0.5f, 8.5f));
		//Files
		files = RPFileLibrary.readFilenames("savedTracks");
		for(int i=0; i<files.length; i++){
			buttons.add(new Button("savedTrack_"+i, files[i].getName(), 3.5f, 7.5f-0.8f*i, true));
		}
		//back
		buttons.add(new Button("back", "BACK", 3.5f, 7f-0.8f*files.length, true));
		//ready
		setReady();
	}
	
	

	@Override
	protected void handleClickEvent(String objName) {
		switch(objName){
		case "back": this.switchToParentMenu(); break;
		default:
			if(objName.startsWith("savedTrack_")) loadTrack(Integer.parseInt(objName.substring(11, 12)));
		}
		
	}
	
	//TODO create Method in Gamefile for loading TrackData
	private void loadTrack(int i){
		System.out.println("..loading Track#"+i+"..");
		Serializable dataList = RPFileLibrary.readFile("savedTracks/"+files[i].getName());
		setGameMode(RPGameMode.RACINGMODE, dataList);
	}
}