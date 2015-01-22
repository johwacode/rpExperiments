package rpEngine.graphical.shader;

import utils.math.Vector2f;

public class HUDShader extends ShaderProgram{
	
	private static final String VERTEX_FILE = "HUDvertexShader.glsl";
	private static final String FRAGMENT_FILE = "HUDfragmentShader.glsl";
	
	private int location_screenPosition;
	
	public HUDShader(){
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "coord"); //of texture
	}

	@Override
	protected void getAllUniformLocations() {
		location_screenPosition = super.getUniformLocation("screenPosition");
	}
	
	public void loadScreenPosition(Vector2f position){
		super.loadVector(location_screenPosition, position);
	}
}
