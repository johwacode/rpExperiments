package rpEngine.graphical.objects;

import java.util.List;

import utils.math.Vector3f;

public class ParticlePath extends ParticleStream {

	List<Vector3f> path;
	private float factor = 2;
	
	public ParticlePath(List<Vector3f> path, Camera camera) {
		super(camera);
		this.path = path;
	}
	
	public List<Vector3f> getPath(){
		return path;
	}
	
	@Override
	protected void createNewParticles(int count){
		 lifetime = (path.size()-1)/factor;
		 for(int i=0; i<count; i++){
			 Particle p = particlesContainer[findUnusedParticle()];
			 p.life = lifetime;
			 Vector3f spawnRandomizer = new Vector3f(
					(float) (Math.random()-0.5f)*spreadSpawn,
					(float) (Math.random()-0.5f)*spreadSpawn,
					(float) (Math.random()-0.5f)*spreadSpawn
					 );
			 Vector3f.add(spawnRandomizer, path.get(0), p.position);
			 
			 p.speed = new Vector3f(
					(float) (Math.random()-0.5f)*spreadFlow,
					(float) (Math.random()-0.5f)*spreadFlow,
					(float) (Math.random()-0.5f)*spreadFlow
					 );
		 }
	 }
	 
	@Override
	 protected int simulateParticles(double delta){
		 currentParticlesCount = 0;
		 for(int i=0; i<maxParticles; i++){
			 Particle p = particlesContainer[i];
			 if(p.life>0){
				 p.life -= delta;
				 if(p.life>0){
					 //simulate
					 Vector3f direction = new Vector3f();
					 int lastPassedPoint = (int) ((lifetime - p.life)*factor);
					 try{
						 Vector3f.sub(path.get(lastPassedPoint+1), path.get(lastPassedPoint), direction);
					 p.position.x+=(direction.x+p.speed.x)*delta*factor;
					 p.position.y+=(direction.y+p.speed.y)*delta*factor;
					 p.position.z+=(direction.z+p.speed.z)*delta*factor;
					 } catch(IndexOutOfBoundsException e){
						 System.err.println("Partikelfehler");
					 }
					 
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

}
