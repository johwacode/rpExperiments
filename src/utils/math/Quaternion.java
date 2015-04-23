package utils.math;

public class Quaternion {
	float a, b, c, d;

	public Quaternion(float a, float b, float c, float d) {
		super();
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	
	public Quaternion invert(){
		return new Quaternion(a, -b, -c, -d);
	}
	
	public static Quaternion multiply(Quaternion left, Quaternion right){
		return null;
	}
	
	public void doRotation(Vector3f aim){
	}
}
