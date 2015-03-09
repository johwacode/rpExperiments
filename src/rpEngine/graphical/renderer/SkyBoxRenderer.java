package rpEngine.graphical.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import rpEngine.graphical.model.Loader;
import rpEngine.graphical.model.VAObject;
import rpEngine.graphical.shader.SkyboxShader;

public class SkyBoxRenderer {

	private static final float SIZE = 500f;
	
	private static final float[] VERTICES = {        
	    -SIZE,  SIZE, -SIZE,
	    -SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	    -SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE
	};
	
	private static String[] TEXTURE_FILES = {
		"sky_right",
		"sky_left",
		"sky_top",
		"sky_bottom",
		"sky_back",
		"sky_front"
	};
	
	private static String[] NIGHT_TEXTURE_FILES = {
		"nightRight",
		"nightLeft",
		"nightTop",
		"nightBottom",
		"nightBack",
		"nightFront"
	};
	
	private VAObject cube;
	private int textureDay, textureNight;
	private SkyboxShader shader;
	
	public SkyBoxRenderer(SkyboxShader shader){
		this.shader = shader;
		cube = Loader.loadPositionOnlyVAO(VERTICES);
		textureDay = Loader.loadCubeMapTexture(TEXTURE_FILES, "SkyBox");
		textureNight = Loader.loadCubeMapTexture(NIGHT_TEXTURE_FILES, "SkyBoxNight");
		shader.start();
		shader.loadDayAndNightTextures();
		shader.stop();
	}
	
	public void render(){
		GL30.glBindVertexArray(cube.getId());
		GL20.glEnableVertexAttribArray(0);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureDay);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureNight);
		shader.loadBlendFactor(0.6f); //TODO change daytime over time
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
}
