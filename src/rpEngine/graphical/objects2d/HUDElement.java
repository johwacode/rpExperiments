package rpEngine.graphical.objects2d;

import java.util.LinkedList;
import java.util.List;

import rpEngine.graphical.model.Model2D;
import rpEngine.graphical.structs.HUDfriendly;

public class HUDElement {
	private List<Model2D> models;
	
	
	public HUDElement(){
		models = new LinkedList<>();
	}
	
	public List<Model2D> getModels(){
		return models;
	}
	
	public void refreshDisplay(HUDfriendly source){
	}
	
	protected void addModel(Model2D model2D){
		models.add(0, model2D);
	}
	protected void addModels(List<Model2D> models){
		this.models.addAll(0, models);
	}
	protected void clearModels(){
		models.clear();
	}
}
