package rpEngine.graphical.objects2d;

import rpEngine.graphical.objects2d.text.Text;
import rpEngine.graphical.structs.HUDfriendly;

public class GearDisplay extends HUDElement{
	private float posX, posY;
	private final String contentName = "GearContent";
	
	public GearDisplay(float posX, float posY){
		this.posX = posX;
		this.posY = posY;
		addModel(HUDModelGenerator.createRectangle(posX, posY, 0.8f, 1.1f, "circle1"));
	}
	
	@Override
	public void refreshDisplay(HUDfriendly source){
		Text.deleteString(contentName);
		String value = source.getHUDmessage(contentName);
		Text.createString(contentName, value, posX+0.31f, posY+0.15f);
	}
}
