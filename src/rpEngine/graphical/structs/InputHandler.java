package rpEngine.graphical.structs;

public interface InputHandler{
	public int getInputHandlingPriority();
	public boolean processInput(int key, int action);
	public void move(long window);
}
