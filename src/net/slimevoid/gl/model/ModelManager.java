package net.slimevoid.gl.model;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glVertexAttribIPointer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.slimevoid.utils.Utils;

public class ModelManager {
	
	private final ModelLoader loader;
	private final String modelFolder;
	private final Map<String, Model> models = new HashMap<>();
	private final List<String> toLoad = new ArrayList<>();
	private String[] materials;
	
	public ModelManager(ModelLoader loader, String modelFolder) {
		this.loader = loader;
		this.modelFolder = modelFolder;
	}
	
	public void init() throws IOException {
		materials = Utils.readRessource(modelFolder+"/materials").split("\n");
	}
	
	public int getMaterialIndex(String mat) {
		for(int i = 0; i < materials.length; i ++) if(materials[i].equalsIgnoreCase(mat)) return i;
		throw new IllegalArgumentException("Unknown material "+mat);
	}
	
	public int createVertexArray(float[] geom, int[] styles, float[] edgeWear) {
		int vao = glGenVertexArrays();
		glBindVertexArray(vao);
			glBindBuffer(GL_ARRAY_BUFFER, glGenBuffers());
			glBufferData(GL_ARRAY_BUFFER, geom, GL_STATIC_DRAW);
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 6*4, 0);
			glEnableVertexAttribArray(0);
			glVertexAttribPointer(1, 3, GL_FLOAT, false, 6*4, 3*4);
			glEnableVertexAttribArray(1);
			glBindBuffer(GL_ARRAY_BUFFER, glGenBuffers());
			glBufferData(GL_ARRAY_BUFFER, styles, GL_STATIC_DRAW);
			glVertexAttribIPointer(2, 1, GL_INT, 0, 0);
			glEnableVertexAttribArray(2);
			glBindBuffer(GL_ARRAY_BUFFER, glGenBuffers());
			glBufferData(GL_ARRAY_BUFFER, edgeWear, GL_STATIC_DRAW);
			for(int i = 0; i < 6; i ++) {
				glVertexAttribPointer(3+i, 3, GL_FLOAT, false, 6*3*4, i*3*4);
				glEnableVertexAttribArray(3+i);
			}
		glBindVertexArray(0);
		return vao;
	}
	
	public Model getModel(String name) {
		synchronized(models) {
			if(!models.containsKey(name)) {
				if(materials == null) throw new RuntimeException("Materials not initialized");
				models.put(name, new Model(0, 0));
				toLoad.add(name);
			}
			return models.get(name);
		}
	}
	
	public void loadWaitingModels() {
		synchronized(models) {
			while(!toLoad.isEmpty()) {
				String name = toLoad.remove(0);
				try {
					Model m = loader.loadModel(this, modelFolder+"/"+name); //TODO better method
					Model real = models.get(name);
					real.vao = m.vao;
					real.count = m.count;
				} catch(IOException e) {
					throw new RuntimeException(e); //TODO better error handling with default model
				}
			}
		}
	}
}
