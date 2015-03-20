package rpEngine.graphical.model;

public class Model {
	private VAObject vao;
	private Texture texture;
	
	public Model(VAObject vao, Texture texture) {
		super();
		this.vao = vao;
		this.texture = texture;
	}
	public VAObject getVao() {
		return vao;
	}
	public Texture getTexture() {
		return texture;
	}
	public void changeTexture(Texture texture){
		this.texture = texture;
	}

}
