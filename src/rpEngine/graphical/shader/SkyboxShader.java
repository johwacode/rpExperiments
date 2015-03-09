package rpEngine.graphical.shader;

import rpEngine.graphical.objects.Camera;
import utils.math.Matrix4f;
import utils.math.Vector3f;

public class SkyboxShader extends ShaderProgram{

	private static final String VERTEX_FILE = "skyboxVertexShader.glsl";
	private static final String FRAGMENT_FILE = "skyboxFragmentShader.glsl";
	
	private static final float ROTATION_SPEED = 0.01f; //radians per second
	
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_fogColor;
	
	private int location_cubeMapDay;
	private int location_cubeMapNight;
	private int location_blendFactor;
	
	private float currentRotation = 0;
	
	public SkyboxShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(location_projectionMatrix, matrix);
	}

	public void loadViewMatrix(Camera camera){
		Matrix4f matrix = camera.getViewMatrix();
		//no translation relative to Player
		matrix.m03 = 0;
		matrix.m13 = 0;
		matrix.m23 = 0;
		//slow rotation
		currentRotation -= ROTATION_SPEED * 0.016f; //TODO: get real time per frame
		Matrix4f.rotateY(currentRotation, matrix, matrix);
		super.loadMatrix(location_viewMatrix, matrix);
	}
	
	public void loadFogColor(float r, float g, float b){
		super.loadVector(location_fogColor, new Vector3f(r,g,b));
	}
	
	public void loadBlendFactor(float blendFactor){
		super.loadFloat(location_blendFactor, blendFactor);
	}
	
	public void loadDayAndNightTextures(){
		super.loadInt(location_cubeMapDay, 0);
		super.loadInt(location_cubeMapNight, 1);
	}
	
	
	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_fogColor = super.getUniformLocation("fogColor");
		location_cubeMapDay = super.getUniformLocation("cubeMapDay");
		location_cubeMapNight = super.getUniformLocation("cubeMapNight");
		location_blendFactor = super.getUniformLocation("blendFactor");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
