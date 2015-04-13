package game;

import java.util.Comparator;
import java.util.TreeSet;

import rpEngine.graphical.structs.InputHandler;

public abstract class InputController {
	private static TreeSet<InputHandler> queue = new TreeSet<>(new InputComparator());
	private static long window;
	
	public static void setWindow(long windowID){
		clear();
		window = windowID;
	}
	
	public static long getWindow(){
		return window;
	}
	
	public static boolean registerHandler(InputHandler handler){
		return queue.add(handler);
	}
	
	public static boolean removeHandler(InputHandler handler){
		return queue.remove(handler);
	}
	
	public static void processInput(int key, int action){
		for(InputHandler handler: queue)
			if(handler.processInput(key, action)) return;
	}
	
	public static void move(){
		for(InputHandler handler: queue) handler.move(window);
	}
	
	public static void clear(){
		queue.clear();
	}
	
	private static class InputComparator implements Comparator<InputHandler>{
		@Override
		public int compare(InputHandler handler0, InputHandler handler1) {
			return (handler0.getInputHandlingPriority()<handler1.getInputHandlingPriority())? -1:1;
		}
	}
}
