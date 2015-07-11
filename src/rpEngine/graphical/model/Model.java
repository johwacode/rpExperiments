package rpEngine.graphical.model;

import utils.fileLoader.OBJLoader;

/**
 * Model-Object that contains vao and texture
 */
public class Model {
	private VAObject vao;
	private Texture texture;
	
	public Model(VAObject vao, Texture texture) {
		this.vao = vao;
		this.texture = texture;
	}
	
	public Model(String modelFileName, String textureFileName, boolean collisionBox){
		this(OBJLoader.loadOBJ(modelFileName, collisionBox), new Texture(Loader.loadTexture(textureFileName, true)));
	}
	
	public VAObject getVao() {
		return vao;
	}
	public Texture getTexture() {
		return texture;
	}
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
}
