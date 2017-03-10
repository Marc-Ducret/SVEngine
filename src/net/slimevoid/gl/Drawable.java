package net.slimevoid.gl;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import net.slimevoid.gl.model.Model;
import net.slimevoid.gl.shader.ShaderProgram;
import net.slimevoid.lang.math.Mat4;

public class Drawable {
	
	private Mat4 mat = new Mat4();
	private Mat4 prevMat = new Mat4();
	private final Model model;
	
	public Drawable(Model model) {
		this.model = model;
		mat.loadIdentity();
		prevMat.loadIdentity();
	}
	
	public void draw(ShaderProgram program, float interpolation, Mat4 modelMat) {
		synchronized(this.mat) {
			if(interpolation == 0) modelMat.set(prevMat);
			else modelMat.set(prevMat).mul((1 - interpolation) / interpolation).add(mat).mul(interpolation);
		}
		program.setUniformVec3("primColor", model.colPrim);
		program.setUniformVec3("altColor", model.colAlt);
		program.setMat4("modelMat", modelMat);
		glBindVertexArray(model.vao);
		glDrawArrays(GL_TRIANGLES, 0, model.count);
		glBindVertexArray(0);
	}
	
	public void updateMat(Mat4 mat) {
		synchronized(this.mat) {
			prevMat.set(this.mat);
			this.mat.set(mat);
		}
	}
}
