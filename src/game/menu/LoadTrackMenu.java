package game.menu;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import rpEngine.graphical.objects.Curve.SerializableCurveData;
import rpEngine.graphical.objects2d.Button;
import rpEngine.graphical.objects2d.HUDModelGenerator;
import utils.fileLoader.RPFileLibrary;

public class LoadTrackMenu extends Menu{
		File[] files;
	
	public LoadTrackMenu(MenuController ctrl){
		super(ctrl);
		//Logo
		this.addModel(HUDModelGenerator.createRectangle(0.5f, 5f, 2.3f, 2f, "rp_logo"));
		//Files
		files = RPFileLibrary.readFilenames("savedTracks");
		for(int i=0; i<files.length; i++){
			buttons.add(new Button("savedTrack_"+i, files[i].getName(), 3.5f, 4.5f-i, true));
		}
		//back
		buttons.add(new Button("back", "BACK", 3.5f, 4.5f-files.length, true));
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
		List<Serializable> dataList = (List<Serializable>) RPFileLibrary.readFile("savedTracks/"+files[i].getName());
		for(Serializable data: dataList){
			SerializableCurveData curveData = (SerializableCurveData) data;
			System.out.println("angleXZ: "+curveData.angleXZ);
		}
		//controller.getGame().setMode(RPGameMode.BUILDMODE);
	}
}