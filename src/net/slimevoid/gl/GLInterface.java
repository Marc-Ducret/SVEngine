package net.slimevoid.gl;

import static java.lang.Math.PI;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F5;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import net.slimevoid.gl.model.ModelManager;
import net.slimevoid.gl.model.ObjLoader;
import net.slimevoid.gl.shader.ShaderManager;
import net.slimevoid.gl.shader.ShaderProgram;
import net.slimevoid.lang.math.Mat4;

public class GLInterface {
 
	private static long window;
	
	private static boolean started = false;
	private static int windowWidth, windowHeight;
	private static String windowTitle;
	private static boolean windowsResizable;
	
	private static ShaderManager shaderManager;
	private static ModelManager modelManager;
	private static Camera cam;
	
	private static List<Drawable> drawables = new ArrayList<>(); //TODO finish impl
	private static long lastTick, nextTick;
	
	private static boolean alive;
	
	public static void start(int w, int h, String title, boolean resizable) throws InterruptedException {
		if(started) throw new RuntimeException("GL already started");
		alive = true;
		started = true;
		windowWidth = w;
		windowHeight = h;
		windowTitle = title;
		windowsResizable = resizable;
		
		Object lock = new Object();
		System.out.println("Starting LWJGL " + Version.getVersion());
		new Thread(new Runnable() {
			@Override
			public void run() {
				init();
				synchronized(lock) {
					lock.notify();
				}
				loop();
				free();
			}
		}).start();
		synchronized(lock) {
			lock.wait();
		}
	}

	private static void init() {
		GLFWErrorCallback.createPrint(System.err).set();
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");
		initWindow();
		initManagers();
		initScene();
	}
	
	private static void initWindow() {
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, windowsResizable ? GLFW_TRUE : GLFW_FALSE);
		glfwWindowHint(GLFW_SAMPLES, 8);

		window = glfwCreateWindow(windowWidth, windowHeight, windowTitle, NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); //TODO keyboard / mouse managment
			if ( key == GLFW_KEY_F5 && action == GLFW_RELEASE )
				shaderManager.unloadShaders();
		});

		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);
			glfwGetWindowSize(window, pWidth, pHeight);
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		}

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);
	}
	
	private static void initManagers() {
		shaderManager = new ShaderManager("shader");
		modelManager = new ModelManager(new ObjLoader(), "model");
		try {
			modelManager.init();
		} catch (IOException e) {
			throw new RuntimeException("Can't initialize model manager", e);
		}
	}
	
	private static void initScene() {
		cam = new Camera();
		cam.setupProjection(windowWidth / (float) windowHeight, (float) PI/2.5F, .01F, 100F);
		cam.computeMat();
	}
	
	private static void loop() {
		GL.createCapabilities();

		glClearColor(0.8f, 0.8f, 0.9f, 0.0f);
		glEnable(GL_DEPTH_TEST);
		
		
		Mat4 modelMat = new Mat4();
		
		
		while ( !glfwWindowShouldClose(window) ) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			ShaderProgram program = shaderManager.getProgram("base");
			modelManager.loadWaitingModels();
			draw(program, getInterpolation(), modelMat);
			
			glfwSwapBuffers(window);
			glfwPollEvents();
			
			int error = glGetError();
			if(error != GL_NO_ERROR)
				System.out.println("GL error: "+error);
		}
	}
	
	private static void draw(ShaderProgram program, float interpolation, Mat4 modelMat) {
		modelMat.loadIdentity();
		glUseProgram(program.getId());
		synchronized(cam) {
			cam.computeMat();
			program.setMat4("projMat", cam.projMat);
			program.setMat4("viewMat", cam.viewMat);
			program.setUniformVec3("camPos", cam.getPos());
		}
		synchronized(drawables) {
			for(Drawable d : drawables) d.draw(program, interpolation, modelMat);
		}
	}
	
	private static void free() {
		alive = false;
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	
	private static float getInterpolation() {
		long time = getTimeMicro();
		if(time < lastTick) return 0;
		if(time > nextTick) return 1;
		return (time-lastTick)/((float) (nextTick - lastTick));
	}
	
	public static void provideTickInfo(long nextTickEstimate) {
		lastTick = getTimeMicro();
		nextTick = nextTickEstimate;
	}
	
	public static void addDrawable(Drawable d) {
		synchronized(drawables) {
			drawables.add(d);
		}
	}
	
	/**
	 * Modify camera within synchronized blocks
	 */
	public static Camera getCamera() {
		return cam;
	}
	
	public static long getTimeMicro() {
		return System.nanoTime() / 1000;
	}
	
	public static boolean isAlive() {
		return alive;
	}
	
	public static ModelManager getModelManager() {
		return modelManager;
	}
}