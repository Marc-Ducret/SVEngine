package net.slimevoid.gl.shader;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.slimevoid.utils.Utils;

public class ShaderManager {
	
	private final String shaderFolder;
	private final Map<String, ShaderProgram> programs;
	
	public ShaderManager(String shaderFolder) {
		this.shaderFolder = shaderFolder;
		this.programs = new HashMap<>();
	}
	
	public ShaderProgram getProgram(String name) {
		if(!programs.containsKey(name)) {
			try {
				programs.put(name, loadProgram(name));
			} catch(IOException e) {
				throw new RuntimeException(e); //TODO better error handling with default shader
			}
		}
		return programs.get(name);
	}
	
	private ShaderProgram loadProgram(String name) throws IOException {
		String[] files;
		try {
			files = Utils.readRessource(shaderFolder+"/"+name+".prog").split("\n");
		} catch(IOException e) {
			throw new IOException("Can't load program "+name, e);
		}
		int[] shads = new int[files.length];
		for(int i = 0; i < files.length; i ++) {
			String f = files[i];
			int type;
			if		(f.endsWith(".frag")) type = GL_FRAGMENT_SHADER;
			else if	(f.endsWith(".vert")) type = GL_VERTEX_SHADER;
			else throw new IOException("Can't load program "+name+" as "+f+" isn't .vert or .frag");
			int shad = glCreateShader(type);
			glShaderSource(shad, Utils.readRessource(shaderFolder+"/"+f));
			glCompileShader(shad);
			if(glGetShaderi(shad, GL_COMPILE_STATUS) == GL_FALSE)
				throw new IOException("Can't load program "+name+" as "+f+" doesn't compile.\n"+glGetShaderInfoLog(shad));
			shads[i] = shad;
		}
		int program = glCreateProgram();
		for(int shad : shads) glAttachShader(program, shad);
		glLinkProgram(program);
		if(glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE)
			throw new IOException("Can't load program "+name+" as linking failed.\n"+glGetProgramInfoLog(program));
		return new ShaderProgram (program);
	}
}
