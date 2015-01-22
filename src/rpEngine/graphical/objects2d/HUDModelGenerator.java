package rpEngine.graphical.objects2d;

import rpEngine.graphical.model.Loader;
import rpEngine.graphical.model.Model2D;
import rpEngine.graphical.model.Texture;

public class HUDModelGenerator {
	/**
	 * Raster: unten links: 0,0 oben rechts: 10,10
	 */
	private static float[] hudTexCoords = { 0,1,
											1,1,
											1,0,
											0,0
											};
	private static int[] hudIndices = {0,1,2,2,3,0};
	
	/**
	 * creates a 2D-Rectangle with original TextureCoordinates, but without a position to display.
	 */
	public static Model2D createRectangle(float width, float height, String textureName){
		return createRectangle(width, height, textureName, null);
	}
	
	/**
	 * creates a 2D-Rectangle with original TextureCoordinates, and a position(x,y) to display.
	 */
	public static Model2D createRectangle(float x, float y, float width, float height, String textureName){
		Model2D model = createRectangle(width, height, textureName, null);
		model.addPosition(x, y);
		return model;
	}
	
	/**
	 * creates a 2D-Rectangle with TextureCoordinates-param, but without a position to display.
	 */
	public static Model2D createRectangle(float width, float height, String textureName, float[] texCoordinates){
		float[] hudVertices = {
				0,0,
				width,0,
				width, height,
				0, height
		};
		
		Texture hudTex = new Texture(Loader.loadTexture("hud/"+textureName, false));
		Model2D model = (texCoordinates==null)?
				Loader.load2DToVAO(hudVertices, hudTexCoords, hudIndices, hudTex):
				Loader.load2DToVAO(hudVertices, texCoordinates, hudIndices, hudTex);
		return model;
	}
}
