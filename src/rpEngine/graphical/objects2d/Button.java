package rpEngine.graphical.objects2d;

import rpEngine.graphical.model.Model2D;
import rpEngine.graphical.objects2d.text.Text;


public class Button extends HUDElement implements Clickable{
	private static final float height=1, width=2.8f;
	private static Model2D model = HUDModelGenerator.createRectangle(width, height, "lightBall");
	private static Model2D modelHovered = HUDModelGenerator.createRectangle(width, height, "circle_hover");
	private static Model2D modelClicked = HUDModelGenerator.createRectangle(width, height, "circle_click");
	
	private ButtonState state;
	private String id;
	private Model2D currentModel;
	private long modelPositionID;
	
	private float x,y;
	
	public Button(String id, String label, float xPos, float yPos, boolean isActive){
		this.id = id;
		this.x = xPos;
		this.y = yPos;
		state = (isActive)? new Active(): new Inactive();
		Text.createString("button"+id, label, xPos+0.5f, yPos+0.1f);
	}
	
	private void changeModel(Model2D newModel){
		clearModels();
		if(currentModel!=null)currentModel.removePosition(modelPositionID);
		currentModel = newModel;
		addModel(newModel);
		this.modelPositionID = currentModel.addPosition(x, y);
	}
	
	
	@Override
	public boolean isInArea(float mouseX, float mouseY) {
		boolean result = (mouseX>=x && mouseX<=x+width
							&& mouseY>=y && mouseY <=y+height);
		state.handleMouseOver(result);
		return result;
	}

	@Override
	public String click() {
		state.handleClick();
		return id;
	}
	
	protected abstract class ButtonState{
		/**
		 * @param mouseIn: true=mouse is in area, false=mouse is out of Area
		 */
		void handleMouseOver(boolean mouseIn){}

		void handleClick(){}
	}
	
	private class Active extends ButtonState{
		public Active(){
			changeModel(model);
		}

		@Override
		public void handleMouseOver(boolean mouseIn) {
			if(mouseIn)state = new Hover();
		}
	}
	
	private class Hover extends ButtonState{
		public Hover(){
			changeModel(modelHovered);
		}

		@Override
		public void handleMouseOver(boolean mouseIn) {
			if(!mouseIn)state = new Active();
		}

		@Override
		public void handleClick() {
			state = new Clicked();
		}
	}
	
	private class Clicked extends ButtonState{
		public Clicked(){
			changeModel(modelClicked);
		}

		@Override
		public void handleMouseOver(boolean mouseIn) {
			if(!mouseIn)state = new Active();
		}
	}
	
	private class Inactive extends ButtonState{
		public Inactive(){
			changeModel(model);
		}

		@Override
		public void handleMouseOver(boolean mouseIn) {
			//do nothing
		}
	}
	
	public String getID(){
		return "button"+id;
	}
	
	@Override
	public String toString(){
		return "Button "+id+":"+state.getClass().getSimpleName();
	}
}
