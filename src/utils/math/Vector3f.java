package utils.math;

import java.io.Serializable;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;


public class Vector3f implements Serializable{
	private static final long serialVersionUID = 1L;
	public float x, y, z;
	
	public Vector3f(float x, float y, float z){
		this.x=x; this.y=y; this.z=z;
	}
	
	/**
	 * empty Vector, no values inited
	 */
	public Vector3f(){
	}

	public void normalise() {
		scale(1/length());
	}
	
	public Vector3f duplicate(){
		return new Vector3f(x, y, z);
	}

	public void scale(float scale) {
		x*=scale;
		y*=scale;
		z*=scale;
	}
	
	public Vector3f times(float scale){
		Vector3f res = duplicate();
		res.scale(scale);
		return res;
	}

	public float length() {
		return (float) Math.sqrt(x*x+y*y+z*z);
	}
	
	/**
	 * squared length of the vector (no sqrt)
	 */
	public float length2() {
		return x*x+y*y+z*z;
	}

	public static void add(Vector3f left, Vector3f right,
			Vector3f dest) {
		dest.x=left.x+right.x;
		dest.y=left.y+right.y;
		dest.z=left.z+right.z;
	}

	public static void sub(Vector3f left, Vector3f right,
			Vector3f dest) {
		dest.x=left.x-right.x;
		dest.y=left.y-right.y;
		dest.z=left.z-right.z;
	}

	public void negate(Vector3f dest) {
		dest.x = -x;
		dest.y = -y;
		dest.z = -z;
	}
	
	public static FloatBuffer toFloatBuffer(Vector3f... vectors){
		FloatBuffer f = BufferUtils.createFloatBuffer(vectors.length*3);
		for(Vector3f v: vectors){
			f.put(v.x);
			f.put(v.y);
			f.put(v.z);
		}
		return f;
	}
	
	public static Vector3f min(Vector3f a, Vector3f b, char dimension){
		switch(dimension){
		case 'x': if(a.x<=b.x)return a; 
			return b;
		case 'y': if(a.y<=b.y)return a; 
			return b;
		case 'z':if(a.z<=b.z)return a; 
			return b;
		default: throw new IllegalArgumentException("Vector-Dimension '"+dimension+"' nicht bekannt.");
		}
	}
	
	public static Vector3f cross(Vector3f left, Vector3f right){
		Vector3f dest = new Vector3f();
		dest.x = left.y*right.z - left.z*right.y;
		dest.y = left.z*right.x - left.x*right.z;
		dest.z = left.x*right.y - left.y*right.x;
		return dest;
	}
	
	public static Vector3f max(Vector3f a, Vector3f b, char dimension){
		switch(dimension){
		case 'x': if(a.x>=b.x)return a; 
			return b;
		case 'y': if(a.y>=b.y)return a; 
			return b;
		case 'z':if(a.z>=b.z)return a; 
			return b;
		default: throw new IllegalArgumentException("Vector-Dimension '"+dimension+"' nicht bekannt.");
		}
	}
	
	public static float dot(Vector3f left, Vector3f right){
		return left.x*right.x+left.y*right.y+left.z*right.z;
	}
	
	/**
	 * rotates the given Vector in XZ.
	 * @param angle
	 */
	public void rotateXZ(float angle){
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);
		x = cos*x - sin*z;
		z = sin*x + cos*z;
	}
	
	public boolean equals(Vector3f vec){
		return (x==vec.x && y==vec.y && z==vec.z);
	}
	
	@Override
	public String toString(){
		return "("+x+", "+y+", "+z+")";
	}
}
