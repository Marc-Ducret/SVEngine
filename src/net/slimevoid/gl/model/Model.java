package net.slimevoid.gl.model;

import com.sun.prism.ps.Shader;

public class Model {
	
	public final Shader shad;
	public final int vao;
	public final int count;
	
	public Model(Shader shad, int vao, int count) {
		this.shad = shad;
		this.vao = vao;
		this.count = count;
	}
}
