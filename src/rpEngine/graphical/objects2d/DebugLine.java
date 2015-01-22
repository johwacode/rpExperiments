package rpEngine.graphical.objects2d;

import java.util.LinkedList;
import java.util.List;

import rpEngine.graphical.objects2d.text.Text;

public class DebugLine extends HUDElement implements Runnable{
	private static List<Long> timestamps = new LinkedList<>();
	private static List<String> messages = new LinkedList<>();
	private Thread debugThread;
	
	private boolean running = true;

	private final int DISPLAYTIME = 500;
	
	private float posX, posY;
	
	public DebugLine(float posX, float posY){
		this.posX = posX;
		this.posY = posY;
		debugThread = new Thread(this);
		debugThread.start();
	}
	
	public static void addMessage(String message){
		timestamps.add(System.currentTimeMillis());
		messages.add(message);
	}
	
	public String deleteFirstMessage(){
		timestamps.remove(0);
		return messages.remove(0);
	}
	
	public void printMessages(){
		clearModels();
		try{
			int i=messages.size()-1;
			for(float y = posY; y>0 && i>=0; y-=0.4f){
				String string = "> "+messages.get(i);
				//Text.createString(string, posX, y);
				//TODO: an neue Text-Implementierung anpassen.
				i--;
			}
		} catch(IndexOutOfBoundsException e){
			
		}
	}

	public void stopRunning(){
		running = false;
	}
	
	@Override
	public void run() {
		final int SLEEPTIME = 200;
		while(running){
			try{
				while(timestamps.get(0)+DISPLAYTIME < System.currentTimeMillis()){
					deleteFirstMessage();
				}
			} catch (IndexOutOfBoundsException e){
				
			}
				
			try {
				Thread.sleep(SLEEPTIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
