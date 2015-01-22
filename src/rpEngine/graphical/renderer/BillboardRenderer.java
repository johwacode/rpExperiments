package rpEngine.graphical.renderer;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import rpEngine.graphical.model.Model;
import rpEngine.graphical.model.Texture;
import rpEngine.graphical.model.VAObject;
import rpEngine.graphical.objects.Billboard;
import rpEngine.graphical.shader.BillboardShader;

public class BillboardRenderer {
	
	private BillboardShader shader;
	
	public BillboardRenderer(BillboardShader shader){
		this.shader = shader;
	}

	public void render(List<Billboard> streams){
		for(Billboard stream: streams){
			prepareTexturedModel(stream);
			GL11.glDrawElements(GL11.GL_TRIANGLE_STRIP, 4, GL11.GL_UNSIGNED_INT, 0);
			unbindTexturedModel();
		}
	}
	
	private void prepareTexturedModel(Billboard stream){
		Model model = stream.getModel();
		VAObject vao = model.getVao();
		GL30.glBindVertexArray(vao.getId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		Texture texture = model.getTexture();
		if(texture.hasTransparency()){
			MasterRenderer.disableCulling();
		}
		shader.loadScale(stream.getScale());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
	}
	
	private void unbindTexturedModel(){
		MasterRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}	
}