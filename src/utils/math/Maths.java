package utils.math;

public class Maths {
	public static float getBarycentricHeight(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
	
	/**
	 * a == b with epsilon = 0.00000001f
	 */
	public static boolean floatEquals(float a, float b){
		return Math.abs(a-b) < 0.00000001f;
	}
	
	/**
	 * checks whether a point is inside the given Box. The BoxSides are parallel to the CoordinateAxes
	 * @param point the point to check
	 * 
	 * @param minx
	 * @param maxx
	 * @param miny
	 * @param maxy
	 * @param minz
	 * @param maxz
	 * @param x, y, z the minimal/maximal x, y and z -values of the box;
	 * @return true, if point is inside(borderlines included), false otherwise
	 */
	public static boolean pointIsInBox(Vector3f point, float minx, float maxx, float miny, float maxy, float minz, float maxz){
		return (point.x >= minx && point.x <= maxx
				&& point.y >= miny && point.y <= maxy
				&& point.z >= minz && point.z <= maxz);
	}
}
