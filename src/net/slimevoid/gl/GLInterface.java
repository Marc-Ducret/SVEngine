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
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
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
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;

import net.slimevoid.gl.gui.Gui;
import net.slimevoid.gl.gui.Rectangle;
import net.slimevoid.gl.model.ModelManager;
import net.slimevoid.gl.model.ObjLoader;
import net.slimevoid.gl.shader.ShaderManager;
import net.slimevoid.gl.shader.ShaderProgram;
import net.slimevoid.gl.texture.Texture;
import net.slimevoid.gl.texture.TextureManager;
import net.slimevoid.lang.math.Mat4;

public class GLInterface {
 
	private static long window;
	
	private static boolean started = false;
	public static int windowWidth, windowHeight;
	private static String windowTitle;
	private static boolean windowsResizable;
	
	private static ShaderManager shaderManager;
	private static ModelManager modelManager;
	private static TextureManager textureManager;
	private static Camera cam;
	
	private static List<Drawable> drawables = new ArrayList<>();
	private static Rectangle rectangles;
	private static Gui currentGui;
	private static long lastTick, nextTick;
	
	private static int frameCT, tickCT, fps, tps;
	private static long lastCount = getTimeMicro();
	
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
	
	private static Callback debugCallback;
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
			if(currentGui != null) currentGui.keyChanged(key, action);
		});
		
		glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
			if(currentGui != null) currentGui.mouseButtonChanged(button, action);
		});
		
		glfwSetCursorPosCallback(window, (window, x, y) -> {
			if(currentGui != null) currentGui.mouseMoved((int) x, windowHeight - (int) y);
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
		glfwSwapInterval(1); //TODO better solution mb and find out gliches when high fps
		glfwShowWindow(window);
		
		GL.createCapabilities();
		debugCallback = GLUtil.setupDebugMessageCallback(System.out); //TODO make better
	}
	
	private static void initManagers() {
		shaderManager = new ShaderManager("shader");
		modelManager = new ModelManager(new ObjLoader(), "model");
		textureManager = new TextureManager("texture");
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
		glClearColor(0.8f, 0.8f, 0.9f, 0.0f);
		
		Mat4 modelMat = new Mat4();
		Mat4 viewMat2D = new Mat4();
		
		
		while ( !glfwWindowShouldClose(window) ) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			frameCT++;
			drawWorld(modelMat);
			drawGui(viewMat2D, modelMat);
			
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
	}
	
	private static void drawWorld(Mat4 modelMat) {
		glEnable(GL_DEPTH_TEST);
		glDisable(GL_BLEND);
		ShaderProgram program = shaderManager.getProgram("base");
		modelManager.loadWaitingModels();
		drawList(program, getInterpolation(), modelMat);
	}
	
	private static void drawList(ShaderProgram program, float interpolation, Mat4 modelMat) {
		if(drawables.isEmpty()) return;
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
	
	private static void drawGui(Mat4 viewMat, Mat4 modelMat) {
		if(currentGui == null) return;
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		viewMat.setTranslate(-1, -1, 0);
		viewMat.scale(2F / windowWidth, 2F / windowHeight, 1);
		ShaderProgram program = shaderManager.getProgram("plane");
		glUseProgram(program.getId());
		program.setMat4("viewMat", viewMat);
		clearRectangles();
		currentGui.draw();
		addText("FPS: "+fps, "consolas", 14, windowWidth - 80, windowHeight-16);
		addText("TPS: "+tps, "consolas", 14, windowWidth - 80, windowHeight-32);
		for(Rectangle r = rectangles; r != null; r = r.next) {
			modelMat.setTranslate(r.x, r.y, 0);
			modelMat.scale(r.w, r.h, 1);
			Texture tex = textureManager.getTexture(r.texture);
			program.setUniformVec3("color", r.color);
			program.setVec2("texOff", r.texX / (float) tex.w, r.texY / (float) tex.h);
			program.setVec2("texScale", r.texW / (float) tex.w, r.texH / (float) tex.h);
			program.setSampler2D("texture", tex);
			program.setMat4("modelMat", modelMat);
			glBindVertexArray(modelManager.getVaoRectangle());
			glDrawArrays(GL_TRIANGLES, 0, 6);
			glBindVertexArray(0);
		}
	}
	
	private static void free() {
		System.out.println("Stopped LWJGL");
		debugCallback.free();
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
		tickCT++;
		if(lastTick - lastCount >= 1000000) {
			lastCount += 1000000;
			fps = frameCT; frameCT = 0;
			tps = tickCT; tickCT = 0;
		}
	}
	
	public static void addDrawable(Drawable d) {
		synchronized(drawables) {
			drawables.add(d);
		}
	}
	
	public static void addRectangle(Rectangle r) {
		r.next = rectangles;
		rectangles = r;
	}
	
	private static StringBuilder builder = new StringBuilder();
	public static void addText(String txt, String font, int size, int x, int y) {
		builder.setLength(0);
		String texture = builder.append("#font_").append(font).append('_').append(size).toString();
		for(int i = 0; i < txt.length(); i ++) {
			char c = txt.charAt(i);
			Rectangle r = Rectangle.poolRectangle(x + (size-4)*i, y, size, size); //TODO -3??
			r.setTexture(texture);
			r.setTextureOffset((c%16)*16, 256 - 16 -(c/16)*16-3); //TODO -3??
			r.setColor(0, 0, 0);
			addRectangle(r);
		}
	}
	
	private static void clearRectangles() {
		while(rectangles != null) {
			rectangles.free();
			rectangles = rectangles.next;
		}
	}
	
	public static void closeGui() {
		if(currentGui == null) throw new RuntimeException("No gui to close");
		currentGui = currentGui.getParent();
	}
	
	public static void changeGui(Gui gui) {
		gui.solve();
		gui.setParent(currentGui);
		currentGui = gui;
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