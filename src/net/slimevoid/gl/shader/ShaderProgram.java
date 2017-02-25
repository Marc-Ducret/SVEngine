package net.slimevoid.gl.shader;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import java.util.HashMap;
import java.util.Map;

import net.slimevoid.lang.math.Mat4;
import net.slimevoid.lang.math.Vec2;
import net.slimevoid.lang.math.Vec3;

public class ShaderProgram {
	
	protected ShaderProgram(int id) {
		this.id = id;
		uniformsLocations = new HashMap<String, Integer>();
	}
	
	public void setUniform1i(String name, int data) {
		glUniform1i(getUniformLoc(name), data);
	}
	
	public void setUniform1f(String name, float data) {
		glUniform1f(getUniformLoc(name), data);
	}
	
	public void setUniform3f(String name, float data0, float data1, float data2) {
		glUniform3f(getUniformLoc(name), data0, data1, data2);
	}
	
	public void setUniformVec3(String name, Vec3 v) {
		setUniform3f(name, v.x, v.y, v.z);
	}
	
//	public void setRenderTarget(String string, RenderTarget rt) {
//		setSampler2D(string+"Color", rt.getColorTex());
//		setSampler2D(string+"Depth", rt.getColorTex());
//	}
//	
//	public void setSampler2D(String string, Texture texture) { 
//		setSampler2D(string, texture.getTextureId());
//	}
	
	public void setSampler2D(String string, int textureId) { 
		glActiveTexture(GL_TEXTURE0 + textureId);
		glBindTexture(GL_TEXTURE_2D, textureId);
		setUniform1i(string, textureId);
	}
	
	public void setMat4(String string, Mat4 mat) {
		glUniformMatrix4fv(getUniformLoc(string), false, mat.m);
	}
	
	public void setVec2(String name, Vec2 v) {
		glUniform2f(getUniformLoc(name), (float) v.x, (float) v.y);
	}
	
	private int getUniformLoc(String name) {
		if(uniformsLocations.containsKey(name)) {
			return uniformsLocations.get(name);
		}
		int loc = glGetUniformLocation(id, name);
		uniformsLocations.put(name, loc);
		return loc;
	}
	
	public int getId() {
		return id;
	}
	
	private final int id;
	private Map<String, Integer> uniformsLocations;
}