package rpEngine.graphical.objects2d;

import rpEngine.graphical.objects2d.text.Text;
import rpEngine.graphical.structs.HUDfriendly;

public class RPMmeter extends HUDElement{
	private float posX, posY;
	private final String contentName = "RPMContent";
	
	public RPMmeter(float posX, float posY){
		this.posX = posX;
		this.posY = posY;
		addModel(HUDModelGenerator.createRectangle(posX, posY, 1.53f, 2f, "circle2"));
		Text.createString("RPMlabel", "RPM", posX+0.35f, posY+0.9f);
	}
	
	@Override
	public void refreshDisplay(HUDfriendly source){
		Text.deleteString(contentName);
		String content = source.getHUDmessage(contentName);
		Text.createString(contentName, content, posX+0.4f, posY+0.4f);
	}
}
