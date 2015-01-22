package rpEngine.graphical.shader;

import java.util.List;

import rpEngine.graphical.objects.Camera;
import rpEngine.graphical.objects.Light;
import utils.math.Matrix4f;
import utils.math.Vector3f;

public class EntityShader extends ShaderProgram{
	
	//TODO: reset value
	private static final int MAX_LIGHTS = 10;
	
	private static String VERTEX_FILE = "vertexShader.glsl";
	private static String FRAGMENT_FILE = "fragmentShader.glsl";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int[] location_lightPosition;
	private int[] location_lightColour;
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_useFakeLighting;
	private int location_fogColour;
	
	public EntityShader(){
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		location_fogColour = super.getUniformLocation("fogColour");
		
		location_lightPosition = new int[MAX_LIGHTS];
		location_lightColour = new int[MAX_LIGHTS];
		for(int i = 0; i<MAX_LIGHTS; i++){
			location_lightPosition[i] = super.getUniformLocation("lightPosition["+i+"]");
			location_lightColour[i] = super.getUniformLocation("lightColour["+i+"]");
		}
	}
	
	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadViewMatrix(Camera camera){
		super.loadMatrix(location_viewMatrix, camera.getViewMatrix());
	}
	
	public void loadLight(List<Light> lights){
		
		for(int i=0; i<MAX_LIGHTS; i++){
			if(i<lights.size()){
				super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
				super.loadVector(location_lightColour[i], lights.get(i).getColour());
			}
			else{
				super.loadVector(location_lightPosition[i], new Vector3f(0,0,0));
				super.loadVector(location_lightColour[i], new Vector3f(0,0,0));
			}
		}
	}
	
	public void loadShineVariables(float damper, float reflectivity){
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
	public void loadFakeLightingVariable(boolean useFake){
		super.loadBoolean(location_useFakeLighting, useFake);
	}
	
	public void loadFogColour(float r, float g, float b){
		super.loadVector(location_fogColour, new Vector3f(r,g,b));
	}
	
}
