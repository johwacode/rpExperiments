package rpEngine.graphical.objects;

import rpEngine.graphical.model.Loader;
import rpEngine.graphical.model.Material;
import rpEngine.graphical.model.Model;
import rpEngine.graphical.model.Texture;
import utils.math.Vector3f;
import static utils.math.Vector3f.dot;
import static utils.math.Vector3f.sub;
import static utils.math.Vector3f.add;

public class Prism extends Entity {
	private static Texture asphalt = new Texture(Loader.loadTexture(Material.ASPHALT, "asphalt", true));
	private static final float height = 0.4f;
	
	private static float[] normals =
			{ 0.57735f, -0.57735f, -0.57735f,
			  0.57735f, -0.57735f,  0.57735f,
			 -0.57735f, -0.57735f, -0.57735f,
			 -0.57735f,  0.57735f, -0.57735f,
			  0.57735f,  0.57735f,  0.57735f,
			  0.57735f,  0.57735f, -0.57735f,
			  0.57735f, -0.57735f, -0.57735f,
			  0.57735f,  0.57735f, -0.57735f};
					
	private static float[] texCoords =
				{ 0, 1,
				  1, 1,
				  0, 0,
				  0, 1,
				  1, 0,
				  0, 0,
				  1, 0,
				  1, 1
				};

	private static int[] indices = 
		{ 2, 1, 0, 3, 5, 4, 3, 2, 0, 0, 5, 3, 0, 1, 5, 1, 4, 5, 3, 4, 1, 1, 2, 3};
	
	private Vector3f normalOrigin, uBeta, uGamma;
	private float plainDet, kBeta, kGamma;
	
	public Prism(Vector3f a, Vector3f b, Vector3f c) {
		super(null, null, 0, 0, 0, 1);
		super.setModel(new Model(Loader.loadEntityToVAO(calcVertices(a, b, c), texCoords, normals, indices, Math.max(a.length(), Math.max(b.length(), c.length()))), asphalt));
		prepareCollisionData(a, b, c);
	}
	
	/**
	 * Tests for an intersection by a ray.
	 * disregards height of prism -> just top-Triangle 
	 * @return null if no intersection or point lies in plain.   
	 */
	public float intersectionTest(Vector3f point, Vector3f direction){
		//Point on plain?
		float angleDir = Vector3f.dot(direction, normalOrigin);
		if(Math.abs(angleDir)< 1e-15 )return -1; //either point is in plain OR direction is parallel to plain 
		float distance = (plainDet - dot(point, normalOrigin))/angleDir;
		if(distance<=0) return -1;
		//calc point
		Vector3f intersection = direction.times(distance);
		add(intersection, point, intersection);
		//is point in triangle?
		float beta = Vector3f.dot(uBeta, intersection) + kBeta;
		if(beta<0) return -1;
		float gamma = Vector3f.dot(uGamma, intersection) + kGamma;
		if(gamma<0) return -1;
		float alpha = 1-(beta+gamma);
		if(alpha<0) return -1;
		
		return distance;
	}
	
	/**
	 * calculates the value of the highest Point of the Prism at the given xz-Coordinates
	 * @return -100 if no intersection, else the y-value of the intersection.
	 */
	@Override
	public float getTopPositionAt(float x, float z){
		//Point on plain?
		Vector3f point = new Vector3f(x, -100, z);
		if(Math.abs(normalOrigin.y)< 1e-15 )return -100; //either point is in plain OR direction is parallel to plain 
		float distance = (plainDet - dot(point, normalOrigin))/normalOrigin.y;
		if(distance<=0) return -100;
		//calc point
		Vector3f intersection = new Vector3f(x, distance-100, z);
		//is point in triangle?
		float beta = Vector3f.dot(uBeta, intersection) + kBeta;
		if(beta<0) return -100;
		float gamma = Vector3f.dot(uGamma, intersection) + kGamma;
		if(gamma<0) return -100;
		float alpha = 1-(beta+gamma);
		if(alpha<0) return -100;
		
		return intersection.y;
	}
	
	
	private float[] calcVertices(Vector3f a, Vector3f b, Vector3f c) {
		//v1 ist der mit niedrigstem x, im zweifelsfall mit niedrigerem z-wert
		// die anderen Vektoren werden als tmp1 und tmp2 zwischengespeichert
		Vector3f v1 = a;
		Vector3f tmp1, tmp2, v2, v3;
		if(b.x<a.x || (b.x==a.x && b.z<a.z) ) v1=b;
		if(c.x<v1.x || (c.x==v1.x && c.z<v1.z) ){
			v1=c;
			tmp1=a;
			tmp2=b;
		}else if(v1==a){
			tmp1=b;
			tmp2=c;
		}else{
			tmp1=a;
			tmp2=c;
		}
		//Anstieg berechnen (dy/dx), gucken welcher der beiden niedriger ist.
		float dx1 = tmp1.x-v1.x;
		float dx2 = tmp2.x-v1.x;
		
		//dx==0 (siehe tangens) abfangen
		if(dx1 == 0 && dx2 !=0){
			v2 = tmp2;
			v3 = tmp1;
		} else if(dx2 == 0 && dx1 !=0){
			v2 = tmp1;
			v3 = tmp2;
		} else if(dx1 == 0 && dx2 ==0){
			throw new IllegalArgumentException(
					"Die drei Punkte liegen auf einer Geraden. Keine Prismabildung möglich.\n"+
					"Tangensproblem: dx1==dx2==0"+
					"\nPunkte: "+a+b+c
					);
		} else {
			
			//falls nichts abzufangen war Anstiege berechnen
			float mTmp1 = (tmp1.z-a.z)/dx1;
			float mTmp2 = (tmp2.z-a.z)/dx2;
			
			//Anstiege vergleichen, Punkte entsprechend zuweisen
			if(mTmp1<mTmp2){
				v2 = tmp1;
				v3 = tmp2;
			}else if(mTmp2<mTmp1){
				v2 = tmp2;
				v3 = tmp1;
			}else throw new IllegalArgumentException(
					"Die drei Punkte liegen auf einer Geraden. Keine Prismabildung möglich.\n"+
					"Anstiege identisch: m1 = "+mTmp1+
					", m2 = "+mTmp2+
					"\nPunkte: "+a+b+c
					);
		}
		
		//Vertex-Array aufstellen
		float[] res =
			{v1.x, 	v1.y, 		 v1.z,
			 v2.x, 	v2.y,		 v2.z,
			 v3.x, 	v3.y, 		 v3.z,
			 v3.x, 	v3.y-height, v3.z,
			 v2.x, 	v2.y-height, v2.z,
			 v1.x, 	v1.y-height, v1.z};
		return res;
	}
	
	public void prepareCollisionData(Vector3f v1, Vector3f v2, Vector3f v3){
		//Surfacenormal top ->ignored by light, just for collissionDetection for now
		Vector3f b = new Vector3f();
		Vector3f c = new Vector3f();
		sub(v2, v1, b);
		sub(v3, v1, c);
		normalOrigin = Vector3f.cross(b, c);
		normalOrigin.normalise();
		plainDet = dot(normalOrigin, v1);
		
		float bb = dot(b, b);
		float bc = dot(b, c);
		float cc = dot(c, c);
		float D = 1f/(bb*cc - bc*bc);
		float bbD = bb*D;
		float bcD = bc*D;
		float ccD = cc*D;
		
		uBeta = new Vector3f();
		sub(b.times(ccD),c.times(bcD), uBeta);
		uGamma = new Vector3f();
		sub(c.times(bbD),b.times(bcD), uGamma);
		
		kBeta = - dot(v1, uBeta);
		kGamma = - dot(v1, uGamma);
	}
}
