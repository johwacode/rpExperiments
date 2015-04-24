package rpEngine.graphical.model;

public class VAObject {
	private int id, count;
	private float[] vertexData;
	private float furthestDistance;

	public VAObject(int id, int count, float[] vertexData, float furthestDistance) {
		this.id = id;
		this.count = count;
		this.vertexData = vertexData;
		this.furthestDistance = furthestDistance;
	}
	
	public VAObject(int id, int count, float[] vertexData) {
		this(id, count, vertexData, -1);
	}

	public int getId() {
		return id;
	}

	public int getVertexCount() {
		return count;
	}

	public float getFurthestDistance() {
		return furthestDistance;
	}

	public float[] getVertices() {
		return vertexData;
	}
}
