package rpEngine.graphical.renderer;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import rpEngine.graphical.model.Texture;
import rpEngine.graphical.model.VAObject;
import rpEngine.graphical.objects.Terrain;
import rpEngine.graphical.shader.TerrainShader;
import utils.math.Matrix4f;
import utils.math.Vector3f;

public class TerrainRenderer {

	private TerrainShader shader;
	
	public TerrainRenderer(TerrainShader shader){
		this.shader = shader;
		shader.start();
		shader.connectTextureUnits();
		shader.stop();
	}
	
	public void render(List<Terrain> terrains){
		for (Terrain terrain: terrains){
			prepareTerrain(terrain);
			loadModelMatrix(terrain);
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(),
					GL11.GL_UNSIGNED_INT, 0);
		}
		unbindTexturedModel();
	}
	
	private void prepareTerrain(Terrain terrain){
		VAObject vao = terrain.getModel();
		GL30.glBindVertexArray(vao.getId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		bindTextures(terrain);
		shader.loadShineVariables(1,0); //TODO wieder variabel setzen
	}
	
	private void bindTextures(Terrain terrain){
		Texture[] texturePack = terrain.getTexturePack();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack[0].getID());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack[1].getID());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack[2].getID());
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack[3].getID());
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getID());
	}
	
	private void unbindTexturedModel(){
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL30.glBindVertexArray(0);
	}
	
	private void loadModelMatrix(Terrain terrain){
		Matrix4f transformationMatrix = 
				Matrix4f.createTransformationMatrix(new Vector3f(terrain.getX(), 0.49f, terrain.getZ()),
						0, 0, 0,
						1);
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
}
