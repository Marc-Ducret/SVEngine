package net.slimevoid.gl.model;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ModelManager {
	
	private final ModelLoader loader;
	private final String modelFolder;
	private final Map<String, Model> models = new HashMap<>();
	
	public ModelManager(ModelLoader loader, String modelFolder) {
		this.loader = loader;
		this.modelFolder = modelFolder;
	}
	
	public int createVertexArray(float...data) {
		int vao = glGenVertexArrays();
		int vbo = glGenBuffers();
		glBindVertexArray(vao);
			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 6*4, 0);
			glEnableVertexAttribArray(0);
			glVertexAttribPointer(1, 3, GL_FLOAT, false, 6*4, 3*4);
			glEnableVertexAttribArray(1);
		glBindVertexArray(0);
		return vao;
	}
	
	public Model getModel(String name) {
		if(!models.containsKey(name)) {
			try {
				models.put(name, loader.loadModel(this, modelFolder+"/"+name));
			} catch(IOException e) {
				throw new RuntimeException(e); //TODO better error handling with default model
			}
		}
		return models.get(name);
	}
}
