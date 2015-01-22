package game;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.system.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.system.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.system.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.system.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.system.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.system.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.system.glfw.GLFW.glfwInit;
import static org.lwjgl.system.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.system.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.system.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.system.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.system.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.system.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.system.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.system.glfw.GLFW.glfwTerminate;
import static org.lwjgl.system.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.system.glfw.GLFW.glfwWindowShouldClose;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.system.glfw.ErrorCallback;
import org.lwjgl.system.glfw.GLFWvidmode;
import org.lwjgl.system.glfw.WindowCallback;
import org.lwjgl.system.glfw.WindowCallbackAdapter;
 
public class Gameloop {
 
    private long window;
    private RacingPlanetsGame game;
	//runtime-Information:
	private Runtime runtime = Runtime.getRuntime();
	private long maxMemory = 0, maxMemoryReserved = 0;
 
    public void execute() {
        try {
            initGL();
            initSceneGraph();
            loop();
            glfwDestroyWindow(window);
            game.cleanUp();
        } finally {
            glfwTerminate();
            
            createProcessInfoLog();
        }
    }
 
    private void initGL() {
        glfwSetErrorCallback(ErrorCallback.Util.getDefault());
 
        if ( glfwInit() != GL11.GL_TRUE )
            throw new IllegalStateException("Unable to initialize GLFW");
 
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
 
        int WIDTH = 800;
        int HEIGHT = 600;
 
        window = glfwCreateWindow(WIDTH, HEIGHT, "RP", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");
 
        
        WindowCallback.set(window, new WindowCallbackAdapter() {
            @Override
            public void key(long window, int key, int scancode, int action, int mods) {
               game.processInput(key, action);
            }
            @Override
            public void mouseButton(long window, int button, int action, int mods) {
               game.processInput(button, action);
            }
            //TODO: resize content (bisher nur aspectRatio)
            /*
            @Override
            public void windowSize(long window, int width, int height) {
            	try{
            		scene.getRenderer().setProjectionmatrix(window);
            		//ARBViewportArray.glGetFloati(target, index, data);
            	}catch(NullPointerException e){
            		System.out.println("not inited yet.");
            	}
            }
            */
        });
 
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(
            window,
            (GLFWvidmode.width(vidmode) - WIDTH) / 2,
            (GLFWvidmode.height(vidmode) - HEIGHT) / 2
        );
 
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
 
        glfwShowWindow(window);
    }
    
    private void initSceneGraph(){
    	GLContext.createFromCurrent();
    	createSystemInfoLog();
    	game = new RacingPlanetsGame(window);
    }
    
    private void createSystemInfoLog(){
    	String newLine = System.getProperty("line.separator");
    	
    	String sysInfo = "SystemInfo:";
    	sysInfo += newLine+"-----------";
        sysInfo += newLine+ "OS: " + System.getProperty("os.name");
        sysInfo += ", " + System.getProperty("os.version");
        sysInfo += ", " + System.getProperty("os.arch");
        sysInfo += newLine;
        sysInfo += newLine+ "JRE-Vendor: " + System.getProperty("java.vendor");
        sysInfo += " (" + System.getProperty("java.vendor.url")+")";
        sysInfo += newLine+ "JRE-Version: " + System.getProperty("java.version");
        sysInfo += newLine;
        sysInfo += newLine+ "GPU: " + GL11.glGetString(GL11.GL_RENDERER);
        sysInfo += newLine+ "Hersteller: " + GL11.glGetString(GL11.GL_VENDOR);
        sysInfo += newLine+ "GL-Version: " + GL11.glGetString(GL11.GL_VERSION);
        sysInfo += newLine+ "GLSL-Version: " + GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION);
        
        System.out.println(sysInfo);
    }
    
    private void createProcessInfoLog(){
    	String newLine = System.getProperty("line.separator");
    	
        long freeMemory = runtime.freeMemory();
        long totalMemory = runtime.totalMemory();
        int availableProcessors = runtime.availableProcessors();
        
        String pInfo = newLine+"ProcessInfo:";
        pInfo += newLine+"-----------";
        pInfo += newLine+ "max.MemoryUsage: "+maxMemory/(1024*1024)+" MB";
        pInfo += newLine+ "max.MemoryReserved: "+maxMemoryReserved/(1024*1024)+" MB";
        pInfo += newLine+ "current Total Memory: "+totalMemory/(1024*1024)+" MB";
        pInfo += newLine+ "currentFree Memory: "+freeMemory/(1024*1024)+" MB";
        pInfo += newLine+ "-> currentUsed: "+(totalMemory-freeMemory)/(1024*1024)+" MB";
        pInfo += newLine+ "availableCPUs: "+availableProcessors;
        
        System.out.println(pInfo);
    }
 
    private void loop() {
        
        while ( glfwWindowShouldClose(window) == GL_FALSE ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            game.update();
            
            glfwSwapBuffers(window);
            glfwPollEvents();
            
            //ProcessResourceInfo
            long currentReserved = runtime.totalMemory();
            long currentMem = currentReserved-runtime.freeMemory();
            if(currentMem>maxMemory)
            	maxMemory=currentMem;
            if(currentReserved>maxMemoryReserved)
            	maxMemoryReserved=currentReserved;
        }
    }
 
    public static void main(String[] args) {
    	//TODO: uncomment before Export
    	/*
    	OutputStream output;
		try {
			output = new FileOutputStream("rp_debugfile.log");
			PrintStream printOut = new PrintStream(output);
			System.setOut(printOut);
	    	System.setErr(printOut);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	*/
        new Gameloop().execute();
    }
}