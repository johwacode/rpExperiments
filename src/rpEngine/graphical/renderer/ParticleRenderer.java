package rpEngine.graphical.renderer;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;

import rpEngine.graphical.model.Loader;
import rpEngine.graphical.model.Model;
import rpEngine.graphical.model.Texture;
import rpEngine.graphical.model.VAObject;
import rpEngine.graphical.objects.ParticleStream;
import rpEngine.graphical.shader.ParticleShader;

public class ParticleRenderer {
	
	private ParticleShader shader;
	
	public ParticleRenderer(ParticleShader shader){
		this.shader = shader;
	}

	public void render(List<ParticleStream> streams){
		for(ParticleStream stream: streams){
			prepareTexturedModel(stream);
			prepareInstance(stream);
			GL33.glVertexAttribDivisor(0, 0);
			GL33.glVertexAttribDivisor(1, 0);
			GL33.glVertexAttribDivisor(2, 1);
			GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, 4, stream.getParticleCount());
			unbindTexturedModel();
		}
	}
	
	private void prepareTexturedModel(ParticleStream stream){
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
	
	private void prepareInstance(ParticleStream stream){
		stream.update();
		Loader.storeBufferSubData(stream.getVBOPositionsID(), stream.getPositionData(), stream.getParticleCount()*3);
	}
	
}