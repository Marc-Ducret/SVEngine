package net.slimevoid.gl;

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

public class ModelManager {
	
	public int createVertexArray(int size, float...data) {
		int vao = glGenVertexArrays();
		int vbo = glGenBuffers();
		glBindVertexArray(vao);
			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
			glVertexAttribPointer(0, size, GL_FLOAT, false, 0, 0);
			glEnableVertexAttribArray(0);
		glBindVertexArray(0);
		return vao;
	}
}
