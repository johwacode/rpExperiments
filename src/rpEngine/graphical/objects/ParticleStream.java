package rpEngine.graphical.objects;

import static org.lwjgl.system.glfw.GLFW.glfwGetTime;

import java.util.Arrays;
import java.util.Collections;

import rpEngine.graphical.model.Loader;
import rpEngine.graphical.model.Model;
import rpEngine.graphical.model.Texture;
import utils.math.Vector2f;
import utils.math.Vector3f;



public class ParticleStream {
	 //ContainerAttributes
	 public static final int maxParticles = 8000;
	 protected Particle[] particlesContainer = new Particle[maxParticles];
	 private int lastUsedParticle = 0;
	 protected int currentParticlesCount = 0;
	 
	 //general Particle-Attributes
	 protected static final float[] vertices = {
										-0.5f, -0.5f, 0,
										 0.5f, -0.5f, 0,
										-0.5f,  0.5f, 0,
										 0.5f,  0.5f, 0
									 	};
					
	 protected static float[] texCoords = {
				0, 1,	//0
				1, 1,	//1
				0, 0,	//2
				1, 0  	//3
				};
	
	
	 protected int spawnrate = 400;
	 protected Vector3f spawnPosition = new Vector3f(5, 10,-15);
	 protected float spreadSpawn = 0.2f;
	 protected Vector3f mainDirection = new Vector3f(0,0,-3);
	protected float spreadFlow = 0.05f;
	protected float lifetime = 5;
	protected Vector2f scale = new Vector2f(0.08f, 0.08f);
	
	//helperVars
	private double lastTime;
	protected Camera camera;
	
	//modeldata
	private Model model;
	private int vboPositionsID;
	
	//buffer-backup
	protected float[] positions;
	
	public ParticleStream(Camera camera){
		lastTime = glfwGetTime();
		this.camera = camera;
		Texture texture = new Texture(Loader.loadTexture("particle_metal", true));
		for(int i=0; i<maxParticles; i++) particlesContainer[i] = new Particle();
		model = new Model(Loader.loadParticleStreamToVAO(vertices, texCoords, maxParticles*3, this), texture);
		positions = new float[maxParticles*3];
	}
	
	public Model getModel(){
		return model;
	}
	
	public Vector2f getScale(){
		return scale;
	}
	
	public void setVBOPositionsID(int vboID){
		 vboPositionsID = vboID;
	}
	
	public int getVBOPositionsID(){
		 return vboPositionsID;
	}
	
	public float[] getPositionData(){
		 return positions;
	}
	
	public int getParticleCount(){
		return currentParticlesCount;
	}
	
	
	
	public void update(){
		 double delta = getTimeSinceLastCall();
		 createNewParticles((int) (Math.min( delta, 0.016f)*spawnrate));
		 simulateParticles(delta);
		 SortParticles();
	 }
	 
	 private double getTimeSinceLastCall(){
		 double currentTime = glfwGetTime();
		 double delta = currentTime-lastTime;
		 lastTime=currentTime;
		 return delta;
	 }
	 
	 protected void createNewParticles(int count){
		 for(int i=0; i<count; i++){
			 Particle p = particlesContainer[findUnusedParticle()];
			 p.life = lifetime;
			 Vector3f spawnRandomizer = new Vector3f(
					(float) (Math.random()-0.5f)*spreadSpawn,
					(float) (Math.random()-0.5f)*spreadSpawn,
					(float) (Math.random()-0.5f)*spreadSpawn
					 );
			 Vector3f.add(spawnRandomizer, spawnPosition, p.position);
			 Vector3f randomDir = new Vector3f(
					(float) (Math.random()-0.5f)*spreadFlow,
					(float) (Math.random()-0.5f)*spreadFlow,
					(float) (Math.random()-0.5f)*spreadFlow
					 );
			 Vector3f.add(mainDirection, randomDir, p.speed);
			 /*
			 p.r = (short)(Math.random()*256);
			 p.g = (short)(Math.random()*256);
			 p.b = (short)(Math.random()*256);
			 p.a = (short)(Math.random()*80);
			 */
		 }
	 }
	 
	 protected int simulateParticles(double delta){
		 currentParticlesCount = 0;
		 for(int i=0; i<maxParticles; i++){
			 Particle p = particlesContainer[i];
			 if(p.life>0){
				 p.life -= delta;
				 if(p.life>0){
					 //simulate
					 p.position.x+=p.speed.x*delta;
					 p.position.y+=p.speed.y*delta;
					 p.position.z+=p.speed.z*delta;
					 
					 Vector3f camToParticle = new Vector3f();
					 Vector3f.sub(camera.getPosition(), p.position, camToParticle);
					 p.cameraDistance = camToParticle.length2();
					 
					 //load Buffer
					 positions[3*currentParticlesCount+0] = p.position.x;
					 positions[3*currentParticlesCount+1] = p.position.y;
					 positions[3*currentParticlesCount+2] = p.position.z;
					 /*
					 colorData[4*currentParticlesCount+0] = p.r;
					 colorData[4*currentParticlesCount+1] = p.g;
					 colorData[4*currentParticlesCount+2] = p.b;
					 colorData[4*currentParticlesCount+3] = p.a;
					 */
				 }
				 else p.cameraDistance = -1f;
				 
				 currentParticlesCount++;
			 }
		 }
		 return currentParticlesCount;
	 }
	
	
	
	 protected int findUnusedParticle(){
		 //search positions after last-Used
		 for(int i = lastUsedParticle; i<particlesContainer.length; i++){
			 if(particlesContainer[i].life<0){
				 lastUsedParticle = i;
				 return i;
			 }
		 }
		 //search rest
		 for(int i = 0; i<lastUsedParticle; i++){
			 if(particlesContainer[i].life<0){
				 lastUsedParticle = i;
				 return i;
			 }
		 }
		 //all particles taken -> override first one
		 return 0;
	 }
	 
	 private void SortParticles(){
		 Arrays.sort(particlesContainer, Collections.reverseOrder());
	 }
	 
	 
	protected class Particle implements Comparable<Particle>{
		protected Vector3f position, speed;
		protected short r, g, b, a; 
		protected float life;
		protected Float cameraDistance;
		 
		protected Particle(){
			 life = -1;
			 cameraDistance = -1f;
			 speed = new Vector3f();
			 position = new Vector3f();
		 }
		 
		@Override
		public int compareTo(Particle p) {
			return cameraDistance.compareTo(p.cameraDistance);
		} 
		 
		 
	 }
}
