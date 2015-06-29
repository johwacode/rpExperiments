package utils.math;

import static org.lwjgl.system.glfw.GLFW.glfwGetWindowSize;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class Matrix4f {
	private static final float FOV = 70; //fieldOfView -> angle
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	
	/**
	 * first Number = row, second one = column. <br/>
	 * m00, m01, m02, m03, <br/>
  	 * m10, m11, m12, m13, <br/>
  	 * m20, m21, m22, m23, <br/>
  	 * m30, m31, m32, m33; <br/>
	 */
	public float m00, m01, m02, m03,
			  	 m10, m11, m12, m13,
			  	 m20, m21, m22, m23,
			  	 m30, m31, m32, m33;
	
	public Matrix4f(){
		setIdentity();
	}
	
	public Matrix4f(float m00, float m01, float m02, float m03, float m10,
			float m11, float m12, float m13, float m20, float m21, float m22,
			float m23, float m30, float m31, float m32, float m33) {
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m03 = m03;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;
		this.m30 = m30;
		this.m31 = m31;
		this.m32 = m32;
		this.m33 = m33;
	}



	/**
	 * stores the Matrix into a given FloatBuffer
	 */
	public void store(FloatBuffer buf) {
		buf.put(m00);
		buf.put(m10);
		buf.put(m20);
		buf.put(m30);
		buf.put(m01);
		buf.put(m11);
		buf.put(m21);
		buf.put(m31);
		buf.put(m02);
		buf.put(m12);
		buf.put(m22);
		buf.put(m32);
		buf.put(m03);
		buf.put(m13);
		buf.put(m23);
		buf.put(m33);
	}

	public static void translate(Vector3f vec, Matrix4f src, Matrix4f dest) {
		dest.m03 += src.m00 * vec.x + src.m01 * vec.y + src.m02 * vec.z;
		dest.m13 += src.m10 * vec.x + src.m11 * vec.y + src.m12 * vec.z;
		dest.m23 += src.m20 * vec.x + src.m21 * vec.y + src.m22 * vec.z;
		dest.m33 += src.m30 * vec.x + src.m31 * vec.y + src.m32 * vec.z;
	}
	
	/**
	 * 
	 * @param angle in radians
	 * @param src
	 * @return
	 */
	public static void rotateX(float angle, Matrix4f src, Matrix4f dest){
		if(angle==0) return;
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);
		/* inefficient:
		Matrix4f rot = new Matrix4f(
							1,		0,		0,		0,
							0,		cos,	-sin,	0,
							0,		sin,	cos,	0,
							0,		0,		0,		1
							);
		return product(src, rot, rot);
		*/
		float t0 = src.m01*cos+src.m02*sin;
		float t1 = src.m11*cos+src.m12*sin;
		float t2 = src.m21*cos+src.m22*sin;
		float t3 = src.m31*cos+src.m32*sin;
		dest.m02 = src.m01*-sin+src.m02*cos;
		dest.m12 = src.m11*-sin+src.m12*cos;
		dest.m22 = src.m21*-sin+src.m22*cos;
		dest.m32 = src.m31*-sin+src.m32*cos;
		dest.m01 = t0;
		dest.m11 = t1;
		dest.m21 = t2;
		dest.m31 = t3;
		
		if(dest!=src){
				dest.m00 = src.m00;
				dest.m03 = src.m03;
				dest.m13 = src.m13;
				dest.m13 = src.m13;
				dest.m23 = src.m23;
				dest.m23 = src.m23;
				dest.m33 = src.m33;
				dest.m33 = src.m33;
		}
	}
	
	public static void rotateY(float angle, Matrix4f src, Matrix4f dest){
		if(angle==0) return;
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);
		/* inefficient:
		Matrix4f rot = new Matrix4f(
							cos,	0,		sin,	0,
							0,		1,		0,		0,
							-sin,	0,		cos,	0,
							0,		0,		0,		1
							);
		product(src, rot, dest);
		*/
		float t0 = src.m00*cos - src.m02*sin;
		float t1 = src.m10*cos - src.m12*sin;
		float t2 = src.m20*cos - src.m22*sin;
		float t3 = src.m30*cos - src.m32*sin;
		dest.m02 = src.m00*sin+src.m02*cos;
		dest.m12 = src.m10*sin+src.m12*cos;
		dest.m22 = src.m20*sin+src.m22*cos;
		dest.m32 = src.m30*sin+src.m32*cos;
		dest.m00 = t0;
		dest.m10 = t1;
		dest.m20 = t2;
		dest.m30 = t3;
		
		if(dest!=src){
			dest.m01 = src.m01;
			dest.m11 = src.m11;
			dest.m21 = src.m21;
			dest.m31 = src.m31;
			dest.m03 = src.m03;
			dest.m13 = src.m13;
			dest.m23 = src.m23;
			dest.m33 = src.m33;
		}
	}
	
	public static void rotateZ(float angle, Matrix4f src, Matrix4f dest){
		if(angle==0) return;
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);
		/*
		Matrix4f rot = new Matrix4f(
							cos,	-sin,	0,		0,
							sin,	cos,	0,		0,
							0,		0,		1,		0,
							0,		0,		0,		1
							);
		dest = product(src, rot, dest);
		*/
		float t0 = src.m00*cos + src.m01*sin;
		float t1 = src.m10*cos + src.m11*sin;
		float t2 = src.m20*cos + src.m21*sin;
		float t3 = src.m30*cos + src.m31*sin;
		dest.m01 = src.m01*-sin + src.m01*cos;
		dest.m11 = src.m11*-sin + src.m11*cos;
		dest.m21 = src.m21*-sin + src.m21*cos;
		dest.m31 = src.m31*-sin + src.m31*cos;
		dest.m00 = t0;
		dest.m10 = t1;
		dest.m20 = t2;
		dest.m30 = t3;
		if (dest!=src){
			dest.m02 = src.m02;
			dest.m12 = src.m12;
			dest.m22 = src.m22;
			dest.m32 = src.m32;
			dest.m03 = src.m03;
			dest.m13 = src.m13;
			dest.m23 = src.m23;
			dest.m33 = src.m33;
		}
	}
	
	public static Matrix4f product(Matrix4f left, Matrix4f right){
		Matrix4f result = new Matrix4f();
		return product(left, right, result);
	}
		
	public static Matrix4f product(Matrix4f left, Matrix4f right, Matrix4f dest){
		float m00 = left.m00*right.m00 + left.m01*right.m10 + left.m02*right.m20 + left.m03*right.m30;
		float m01 = left.m00*right.m01 + left.m01*right.m11 + left.m02*right.m21 + left.m03*right.m31;
		float m02 = left.m00*right.m02 + left.m01*right.m12 + left.m02*right.m22 + left.m03*right.m32;
		float m03 = left.m00*right.m03 + left.m01*right.m13 + left.m02*right.m23 + left.m03*right.m33;

		float m10 = left.m10*right.m00 + left.m11*right.m10 + left.m12*right.m20 + left.m13*right.m30;
		float m11 = left.m10*right.m01 + left.m11*right.m11 + left.m12*right.m21 + left.m13*right.m31;
		float m12 = left.m10*right.m02 + left.m11*right.m12 + left.m12*right.m22 + left.m13*right.m32;
		float m13 = left.m10*right.m03 + left.m11*right.m13 + left.m12*right.m23 + left.m13*right.m33;
		
		float m20 = left.m20*right.m00 + left.m21*right.m10 + left.m22*right.m20 + left.m23*right.m30;
		float m21 = left.m20*right.m01 + left.m21*right.m11 + left.m22*right.m21 + left.m23*right.m31;
		float m22 = left.m20*right.m02 + left.m21*right.m12 + left.m22*right.m22 + left.m23*right.m32;
		float m23 = left.m20*right.m03 + left.m21*right.m13 + left.m22*right.m23 + left.m23*right.m33;
		
		float m30 = left.m30*right.m00 + left.m31*right.m10 + left.m32*right.m20 + left.m33*right.m30;
		float m31 = left.m30*right.m01 + left.m31*right.m11 + left.m32*right.m21 + left.m33*right.m31;
		float m32 = left.m30*right.m02 + left.m31*right.m12 + left.m32*right.m22 + left.m33*right.m32;
		float m33 = left.m30*right.m03 + left.m31*right.m13 + left.m32*right.m23 + left.m33*right.m33;
		
		dest.m00 = m00;
		dest.m01 = m01;
		dest.m02 = m02;
		dest.m03 = m03;
		dest.m10 = m10;
		dest.m11 = m11;
		dest.m12 = m12;
		dest.m13 = m13;
		dest.m20 = m20;
		dest.m21 = m21;
		dest.m22 = m22;
		dest.m23 = m23;
		dest.m30 = m30;
		dest.m31 = m31;
		dest.m32 = m32;
		dest.m33 = m33;
		
		return dest;
	}

	private void setIdentity() {
		m00=1;	m01=0;	m02=0;	m03=0;
		m10=0;	m11=1;	m12=0;	m13=0;
		m20=0;	m21=0;	m22=1;	m23=0;
		m30=0;	m31=0;	m32=0;	m33=1;
		
	}
	
	public static Matrix4f scale(Vector3f vec, Matrix4f src, Matrix4f dest) {
		if (dest == null)
			dest = new Matrix4f();
		dest.m00 = src.m00 * vec.x;
		dest.m10 = src.m10 * vec.x;
		dest.m20 = src.m20 * vec.x;
		dest.m30 = src.m30 * vec.x;
		dest.m01 = src.m01 * vec.y;
		dest.m11 = src.m11 * vec.y;
		dest.m21 = src.m21 * vec.y;
		dest.m31 = src.m31 * vec.y;
		dest.m02 = src.m02 * vec.z;
		dest.m12 = src.m12 * vec.z;
		dest.m22 = src.m22 * vec.z;
		dest.m32 = src.m32 * vec.z;
		return dest;
	}
	
	/**
	 * transforms vertexPosition as in:
	 * vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
	 * @param x, y, z of the vertex.
	 * result: worldPosition.xyz -> no 4th dimension
	 */
	public Vector3f multiplyWithVector3fAndOne(float x, float y, float z){
		return new Vector3f(
				x*m00+y*m01+z*m02+m03,
				x*m10+y*m11+z*m12+m13,
				x*m20+y*m21+z*m22+m23
				);
	}
	
	public float calcDeterminant(){
		return  m00*m11*m22*m33 + m00*m12*m23*m31 + m00*m13*m21*m32 +
				m01*m10*m23*m32 + m01*m12*m20*m33 + m01*m13*m22*m30 +
				m02*m10*m21*m33 + m02*m11*m23*m30 + m02*m13*m20*m31 +
				m03*m10*m20*m31 + m03*m11*m20*m32 + m03*m12*m21*m30 -
				m00*m11*m23*m32 - m00*m12*m21*m33 - m00*m13*m22*m31 -
				m01*m10*m22*m33 - m01*m12*m23*m30 - m01*m13*m20*m32 -
				m02*m10*m23*m31 - m02*m11*m20*m33 - m02*m13*m21*m30 -
				m03*m10*m21*m32 - m03*m11*m22*m30 - m03*m12*m20*m31;
	}
	
	/**
	 * only works for transformationMatrices (a.k.a. last row = (0,0,0,1))
	 */
	public Matrix4f getInverse(){
		if(m30!=0 || m31!=0 || m32!=0 || m33!=1)throw new IllegalArgumentException("building the inverse only works with transformationMatrixes! (matrix was: "+this+")");
		float det = calcDeterminant();
		if (det==0) return null;
		else{
			Matrix4f adj = new Matrix4f(
					m11*m22 - m12*m21,
					m02*m21 - m01*m22,
					m01*m12 - m02*m11,
					m01*m13*m23 + m02*m11*m23 + m03*m12*m21 - m01*m12*m23 - m02*m13*m21 - m03*m11*m22,
					
					m12*m20 - m10*m22,
					m00*m22 - m02*m20,
					m02*m10 - m00*m12,
					m00*m12*m23 + m02*m13*m20 + m03*m10*m22 - m00*m13*m22 - m02*m10*m23 - m03*m12*m20,
					
					m10*m21 - m11*m20,
					m01*m20 - m00*m21,
					m00*m11 - m01*m10,
					m00*m13*m21 + m01*m10*m23 + m03*m11*m20 - m00*m11*m23 - m01*m13*m20 - m03*m10*m21,
					
					0,
					0,
					0,
					m00*m11*m22 + m01*m12*m20 + m02*m10*m21 - m00*m12*m21 - m01*m10*m22 - m02*m11*m20
					);
			float f = 1/det;
			scale(new Vector3f(f, f, f), adj, adj);
			return adj;
		}
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation,
			float rx, float ry, float rz,
			float scale){
		Matrix4f matrix = new Matrix4f();
		translate(translation, matrix, matrix);
		rotateX((float) Math.toRadians(rx), matrix, matrix);
		rotateY((float) Math.toRadians(ry), matrix, matrix);
		rotateZ((float) Math.toRadians(rz), matrix, matrix);
		if(scale!=1) scale(new Vector3f(scale, scale, scale), matrix, matrix);
		return matrix;
	}

	public static Matrix4f createViewMatrix(float pitch, float yaw, Vector3f position){
		Matrix4f viewMatrix = new Matrix4f();
		rotateX((float) Math.toRadians(pitch), viewMatrix, viewMatrix);
		rotateY((float) Math.toRadians(yaw), viewMatrix, viewMatrix);
		Vector3f negativeCameraPos = new Vector3f(-position.x, -position.y, -position.z);
		translate(negativeCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;
	}
	
	public static Matrix4f createProjectionMatrix(long window){
		IntBuffer widthB = BufferUtils.createIntBuffer(1);
		IntBuffer heightB = BufferUtils.createIntBuffer(1);
		glfwGetWindowSize(window, widthB, heightB);
		float aspectRatio = (float) widthB.get() / (float) heightB.get();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV/2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) /frustum_length);
		projectionMatrix.m23 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m32 = -1;
		projectionMatrix.m33 = 0;
		return projectionMatrix;
	}
	
	@Override
	public String toString(){
		String newLine = System.getProperty("line.separator");
		return ""+m00+" "+m01+" "+m02+" "+m03+newLine+
				m10+" "+m11+" "+m12+" "+m13+newLine+
				m20+" "+m21+" "+m22+" "+m23+newLine+
				m30+" "+m31+" "+m32+" "+m33+newLine;
	}
	
}
