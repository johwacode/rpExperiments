package rpEngine.graphical.model;

import java.io.Serializable;

import utils.math.Maths;
import utils.math.Matrix4f;
import utils.math.Vector3f;

public class BoundingBox implements Serializable{
	private static final long serialVersionUID = 6770415385404257160L;
	
	private float left, right, top, bottom, front, back;
	
	/**radius of a sphere, containing the box-shaped boundingBox */
	private float boundingSphereRadius, boundingSphereRadiusSQ;
	
	/**
	 * constructs a symmetric BoundingBoxwith center in the middle.
	 * width in x-Direction, height in y-Direction and length in z. 
	 */
	public BoundingBox(float width, float height, float length){
		this(width/2, width/2, height/2, height/2, length/2, length/2);
	}

	/**
	 * 
	 * @param left = lowest X
	 * @param right = highest X
	 * @param top = highest Y
	 * @param bottom = lowest Y
	 * @param front = lowest Z
	 * @param back = highest Z
	 */
	public BoundingBox(float left, float right, float top, float bottom,
			float front, float back) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		this.front = front;
		this.back = back;
		//store highest value as Radius
		boundingSphereRadius = Math.max(Math.max(Math.max(left, right), Math.max(top, bottom)), Math.max(front, back));
		boundingSphereRadiusSQ = boundingSphereRadius*boundingSphereRadius;
	}

	/**
	 * WARNING: INCOMPLETE IMPLEMENTATION
	 */
	public boolean intersects(Matrix4f transFormationMatrix_this, BoundingBox toCompare, Matrix4f transformationMatrix_toCompare){
		//create a composite transformation from both objects
		Matrix4f transformation = transFormationMatrix_this.getInverse();
		Matrix4f.product(transformation, transformationMatrix_toCompare, transformation);
		Vector3f[] otherBox = transformBoxCoordinates(transformation);
		//check if one of the new corners is in range of this as sphere and if so whether it lies inside
		for(Vector3f corner:otherBox){
			if(corner.length2()<boundingSphereRadiusSQ && Maths.pointIsInBox(corner, left, right, bottom, top, front, back)) return true;
		}
		//TODO: check for Boxes surrounding eachother / cutting edges
		return false;
	}
	
	private Vector3f[] transformBoxCoordinates(Matrix4f transformationMatrix){
		Vector3f[] result = new Vector3f[8];
		result[0] = transformationMatrix.multiplyWithVector3fAndOne(front, top, left);
		result[1] = transformationMatrix.multiplyWithVector3fAndOne(front, top, right);
		result[2] = transformationMatrix.multiplyWithVector3fAndOne(front, bottom, left);
		result[3] = transformationMatrix.multiplyWithVector3fAndOne(front, bottom, right);
		result[4] = transformationMatrix.multiplyWithVector3fAndOne(back, top, left);
		result[5] = transformationMatrix.multiplyWithVector3fAndOne(back, top, right);
		result[6] = transformationMatrix.multiplyWithVector3fAndOne(back, bottom, left);
		result[7] = transformationMatrix.multiplyWithVector3fAndOne(back, bottom, right);
		return result;
	}
	
	/**
	 * fabric to generate a BoundingBox from VertexData
	 * @param vertices
	 * @return
	 */
	public static BoundingBox generateBox(float[] vertices){
		float left=0, right=0, top=0, bottom=0, front=0, back=0;
		for(int i=0; i<vertices.length/3; i+=3){
			float x=vertices[i],
				  y=vertices[i+1],
				  z=vertices[i+2];
			if(x<left)left=x;
			else if(x>right)right=x;
			if(y<bottom)bottom=y;
			else if(y>top)top=y;
			if(z<front)front=z;
			else if(z>back)back=z;
		}
		return new BoundingBox(left, right, top, bottom, front, back);
	}
}
