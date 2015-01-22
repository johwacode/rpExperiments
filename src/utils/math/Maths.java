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
	 * checks whether a point is inside the given Box. The BoxSides are parallel to the CoordinateAxes
	 * @param point the point to check
	 * @param boxMinXYZ the minimal x, y and z -values of the box; 
	 * @param boxMaxXYZ the maximal values.
	 * @return true, if point is inside(borderlines included), false otherwise
	 */
	public static boolean pointIsInBox(Vector3f point, Vector3f boxMinXYZ, Vector3f boxMaxXYZ){
		return (point.x >= boxMinXYZ.x && point.x <= boxMaxXYZ.x
				&& point.y >= boxMinXYZ.y && point.y <= boxMaxXYZ.y
				&& point.z >= boxMinXYZ.z && point.z <= boxMaxXYZ.z);
	}
}
