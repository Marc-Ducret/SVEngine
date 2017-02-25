package net.slimevoid.gl;

import static java.lang.Math.sin;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
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
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import net.slimevoid.gl.shader.ShaderManager;
import net.slimevoid.gl.shader.ShaderProgram;
import net.slimevoid.lang.math.Mat4;
import net.slimevoid.lang.math.Vec3;

public class GLInterface {
 
	private static long window;
	
	private static boolean started = false;
	private static int windowWidth, windowHeight;
	private static String windowTitle;
	private static boolean windowsResizable;
	
	public static void start(int w, int h, String title, boolean resizable) {
		if(started) throw new RuntimeException("GL already started");
		started = true;
		windowWidth = w;
		windowHeight = h;
		windowTitle = title;
		windowsResizable = resizable;
		
		System.out.println("Starting LWJGL " + Version.getVersion());
		new Thread(new Runnable() {
			@Override
			public void run() {
				init();
				loop();
				free();
			}
		}).start();
	}

	private static void init() {
		GLFWErrorCallback.createPrint(System.err).set();
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, windowsResizable ? GLFW_TRUE : GLFW_FALSE);

		window = glfwCreateWindow(windowWidth, windowHeight, windowTitle, NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); //TODO keyboard / mouse managment
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

	private static void loop() {
		GL.createCapabilities();

		// Set the clear color
		glClearColor(0.8f, 0.8f, 0.9f, 0.0f);
		
		//TEST
		ShaderManager sm = new ShaderManager("");
		ModelManager mm = new ModelManager();
		int vao = mm.createVertexArray(3, 
						-.5F, -.5F, 0F, 
						+.5F, -.5F, 0F,
						  0F, +.5F, 0F);
		
		int ct = 0;
		Mat4 mat = new Mat4();
		mat.loadIdentity();
		Vec3 v = new Vec3();
		Vec3 axis = new Vec3();
		axis.set(0, 0, 1);
		//END TEST
		
		while ( !glfwWindowShouldClose(window) ) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			//TEST
			ct++;
			v.set((float) sin(ct * .03F) * .5F, 0, 0);
			mat.loadIdentity();
			mat.rotate(ct*.03F, axis);
			mat.translate(v);
			ShaderProgram program = sm.getProgram("test"+((ct/60)%2));
			glUseProgram(program.getId());
			program.setMat4("mat", mat);
			glBindVertexArray(vao);
			glDrawArrays(GL_TRIANGLES, 0, 3);
			glBindVertexArray(0);
			//END TEST
			
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
	}
	
	private static void free() {
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
}