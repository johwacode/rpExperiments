package rpEngine.graphical.shader;

import rpEngine.graphical.objects.Camera;
import utils.math.Matrix4f;
import utils.math.Vector2f;
import utils.math.Vector3f;

public class ParticleShader extends ShaderProgram{
	
	private static String VERTEX_FILE = "particleVertexShader.glsl";
	private static String FRAGMENT_FILE = "particleFragmentShader.glsl";
	
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_scale;
	private int location_fogColour;
	
	public ParticleShader(){
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "vertices");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "positions");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_scale = super.getUniformLocation("scale");
		location_fogColour = super.getUniformLocation("fogColour");
	}
	
	public void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadViewMatrix(Camera camera){
		super.loadMatrix(location_viewMatrix, camera.getViewMatrix());
	}
	
	public void loadScale(Vector2f scale){
		super.loadVector(location_scale, scale);
	}
	
	public void loadFogColour(float r, float g, float b){
		super.loadVector(location_fogColour, new Vector3f(r,g,b));
	}
	
}
