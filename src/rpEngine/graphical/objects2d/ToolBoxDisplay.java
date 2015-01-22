package rpEngine.graphical.objects2d;

import rpEngine.graphical.model.Model2D;
import rpEngine.graphical.structs.HUDfriendly;

public class ToolBoxDisplay extends HUDElement{
	public static final int PRISM=0, STRAIGHT=1, CURVE=2, POINTER=3, VEHICLETEST=4, DELETE=5;
	private static final String QueryName_currentTool = "currentTool";
	
	private static final int toolCount = 6;
	
	private float posX, posY;
	private Model2D[] icons;
	private Model2D highlightActive;
	
	private int currentTool = 1;
	
	public ToolBoxDisplay(float posX, float posY){
		this.posX = posX;
		this.posY = posY;
		
		//background
		Model2D background = HUDModelGenerator.createRectangle(0.5f, 0.5f, "toolbar2");
		for(int i=0; i<toolCount; i++){
			background.addPosition(posX, posY-0.5f*i);
		}
		addModel(background);
		
		highlightActive = HUDModelGenerator.createRectangle(posX, posY-0.5f*currentTool, 0.5f, 0.5f, "toolbar_current");
		addModel(highlightActive);
		
		//create icons
		icons = new Model2D[toolCount];
		icons[PRISM] = HUDModelGenerator.createRectangle(posX+0.05f, posY, 0.5f, 0.5f, "toolbar_prism");
		icons[STRAIGHT] = HUDModelGenerator.createRectangle(posX+0.05f, posY-0.5f, 0.5f, 0.5f, "toolbar_straight");
		icons[CURVE] = HUDModelGenerator.createRectangle(posX+0.05f, posY-0.5f*2, 0.5f, 0.5f, "toolbar_curve");
		icons[POINTER] = HUDModelGenerator.createRectangle(posX+0.05f, posY-0.5f*3, 0.5f, 0.5f, "toolbar_pick");
		icons[VEHICLETEST] = HUDModelGenerator.createRectangle(posX+0.05f, posY-0.5f*4, 0.5f, 0.5f, "toolbar_testVehicle");
		icons[DELETE] = HUDModelGenerator.createRectangle(posX+0.05f, posY-0.5f*5, 0.5f, 0.5f, "toolbar_delete");
		
		//draw icons
		for(int i=0; i<icons.length; i++){
			addModel(icons[i]);
		}
	}
	
	@Override
	public void refreshDisplay(HUDfriendly source){
		int tool;
		switch(source.getHUDmessage(QueryName_currentTool)){
			case "PrismTool": tool = PRISM; break;
			case "QuadTool": tool = STRAIGHT; break;
			case "CurveTool": tool = CURVE; break;
			default: tool = POINTER;
		}
		if (tool!=currentTool){
			highlightActive.clearPositions();
			currentTool = tool;
			highlightActive.addPosition(posX, posY-0.5f*currentTool);
		}
	}
}
