package rpEngine.graphical.objects2d.text;

import java.util.HashMap;
import java.util.Map;

public class CharacterLookUp {
	
	private static Map<Character, CharacterLookUp> map = new HashMap<>();
	
	public float x, y, width, height;
	public static String texture;
	private static float scaleX, scaleY;

	private CharacterLookUp(char character, float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public static void setTexture(String newTexture, float imageWidth, float imageHeight){
		texture = newTexture;
		scaleX = 1/imageWidth;
		scaleY= 1/imageHeight;
		map.clear();
	}
	
	public static void addCharacter(char character, float x, float y, float width, float height){
		map.put(character, new CharacterLookUp(character, x*scaleX, y*scaleY, width*scaleX, height*scaleY));
	}
	
	public static CharacterLookUp getChar(char character){
		if(!map.containsKey(character)) return getChar('#');
		return map.get(character);
	}
}
