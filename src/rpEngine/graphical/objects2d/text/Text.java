package rpEngine.graphical.objects2d.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import rpEngine.graphical.model.Model2D;
import rpEngine.graphical.objects2d.HUDModelGenerator;

public class Text {
	private static Map<Character, Model2D> models = new HashMap<>();
	
	private static Map<String, StringData> currentText = new HashMap<>();
	
	/**
	 * creates a String at the given position.
	 * the String is stored as a List of Model2D,
	 * which has to be rendered and cleared once each frame. 
	 */
	public static String createString(String name, String content, float x, float y){
		if(!Font.fontLoaded) Font.init_calibri_german();
		
		deleteString(name); //falls bereits vorhanden wird key überschrieben
		StringData data = new StringData(x,y);
		
		float posX = x;
		for(char c : content.toCharArray()){
			data.addChar(c, posX, y);
			posX += CharacterLookUp.getChar(c).width*8;
		}
		currentText.put(name, data);
		return name;
	}
	
	/**
	 * Method for reposition an existing String.
	 * reprints an existing String. breaks if either the String doesn't exist or the position of the 
	 * String didnt't change. [the content of the String is NOT relevant]
	 * creates a new String, if the position has to be refreshed.
	 * @param name id of the String
	 * @param content only needed, if the StringPosition changed for creating a new String.
	 * @param x
	 * @param y
	 */
	public static void repositionString(String name, String content, float x, float y){
		StringData sd = currentText.get(name);
		if(sd==null || (sd.getStringPosX()==x&&sd.getStringPosY()==y))return;
		createString(name, content, x, y);
	}
	
	public static boolean containsString(String name){
		return currentText.containsKey(name);
	}
	
	
	/**
	 * removes a String from screen
	 * @param name = id where the string is registered. not it's value!
	 */
	public static void deleteString(String name){
		if(!currentText.containsKey(name)) return;
		for(Entry<Character, List<Long>> e : currentText.get(name).positions.entrySet()){
			getChar(e.getKey()).removePositions(e.getValue());
		}
		currentText.remove(name);
	}
	
	public static List<Model2D> getRenderList(){
		List<Model2D> list = new LinkedList<>();
		for(StringData data : currentText.values()){
			for(char c : data.positions.keySet()){
				if(!list.contains(c)) list.add(getChar(c));
			}
		}
		return list;
	}
	
	
	/**
	 * returns the model of the character. if it's not created yet it will be.
	 */
	private static Model2D getChar(char c){		
		if(models.containsKey(c)) return models.get(c);
		return createModel(c);
	}
	
	/**
	 * creates a new Charactermodel and stores it into the charactermodel-map
	 */
	private static Model2D createModel(char c){
		CharacterLookUp charData = CharacterLookUp.getChar(c);
		
		float sizeH = 7, sizeV = 3;
		float[] texCoordinates = {
				charData.x, charData.y+charData.height,
				charData.x+charData.width, charData.y+charData.height,
				charData.x+charData.width, charData.y,
				charData.x, charData.y
				};
		Model2D model = HUDModelGenerator.createRectangle(charData.width*sizeH, charData.height*sizeV, CharacterLookUp.texture, texCoordinates);
		model.setName("character "+c);
		models.put(c, model);
		return model;
	}
	
	public static void printCharMap(){
		String s = "";
		for(char c : models.keySet()){
			s += c+" ";
		}
		System.out.println(s);
	}
	
	public static void clear() {
		currentText.clear();
	}
	
	
	
	/**
	 * @author joh
	 * collects Characterpositions per string
	 * e.g. String "hello" shall be drawn on screen
	 *      -> StringData holds a map
	 *      -> keys are the needed characters h, e, l, o 
	 *      -> every char has a model, that is stored globally
	 *      -> if a model should be drawn it needs 1-n positions where to draw it
	 *      -> these positions are stored in StringData as the chars Values (better: the position's id)
	 *      
	 * advantage: models only have to be created once, if a word should be cleared the StringData knows 
	 * which positionentries of which charModels should be eliminated
	 */
	private static class StringData{
		private Map<Character, List<Long>> positions;
		private float stringPosX, stringPosY;
		
		private StringData(float stringPosX, float stringPosY){
			positions = new HashMap<>();
			this.stringPosX = stringPosX;
			this.stringPosY = stringPosY;
		}
		
		public void addChar(char c, float posX, float posY){
			Model2D model = getChar(c);
			if(!positions.containsKey(c)) positions.put(c, new ArrayList<Long>());
			positions.get(c).add(model.addPosition(posX, posY));
		}
		
		public float getStringPosX(){
			return stringPosX;
		}
		public float getStringPosY(){
			return stringPosY;
		}
		
		@Override
		public String toString(){
			String data = "StringData: ";
			data += "\n ->chars[";
			for(char c: positions.keySet()){
				data += " '"+c;
				data += "'(x"+positions.get(c).size()+")";
			}
			data+="]";
			return data;
		}
	}
}
