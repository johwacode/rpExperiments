package rpEngine.graphical.shader;

import rpEngine.graphical.objects.Camera;
import utils.math.Matrix4f;

public class SkyboxShader extends ShaderProgram{

	private static final String VERTEX_FILE = "skyboxVertexShader.glsl";
	private static final String FRAGMENT_FILE = "skyboxFragmentShader.glsl";
	
	private int location_projectionMatrix;
	private int location_viewMatrix;
	
	public SkyboxShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(location_projectionMatrix, matrix);
	}

	public void loadViewMatrix(Camera camera){
		Matrix4f matrix = camera.getViewMatrix();
		matrix.m03 = 0;
		matrix.m13 = 0;
		matrix.m23 = 0;
		super.loadMatrix(location_viewMatrix, matrix);
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
