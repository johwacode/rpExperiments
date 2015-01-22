package rpEngine.graphical.model;

public class VAObject {
	private int id, count;
	private float furthestDistance;

	public VAObject(int id, int count, float furthestDistance) {
		this.id = id;
		this.count = count;
		this.furthestDistance = furthestDistance;
	}
	
	public VAObject(int id, int count) {
		this.id = id;
		this.count = count;
		this.furthestDistance = -1;
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
}
