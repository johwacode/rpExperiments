package rpEngine.graphical.renderer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import rpEngine.graphical.model.Model2D;
import rpEngine.graphical.model.Texture;
import rpEngine.graphical.objects2d.HUDElement;
import rpEngine.graphical.objects2d.text.Text;
import rpEngine.graphical.shader.HUDShader;
import utils.math.Vector2f;
 
public class HUDRenderer {
	private HUDShader shader;
	
	public HUDRenderer(HUDShader shader){
		this.shader = shader;
	}
	
	public void render(List<HUDElement> hudObjects){
		renderModels(Text.getRenderList());
		for(HUDElement element: hudObjects){
			renderModels(element.getModels());
		}
	}
	
	private void renderModels(List<Model2D> hudObjects){
		for(Model2D model: hudObjects){
			for(Vector2f position: model.getPositions()){
				prepareTexturedModel(model, position);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVao().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
				unbindTexturedModel();
			}
		}
	}
	
	private void prepareTexturedModel(Model2D model, Vector2f position){
		GL30.glBindVertexArray(model.getVao().getId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		shader.loadScreenPosition(position);
		Texture texture = model.getTexture();
		MasterRenderer.disableCulling();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());
	}
		
	private void unbindTexturedModel(){
		MasterRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}
}