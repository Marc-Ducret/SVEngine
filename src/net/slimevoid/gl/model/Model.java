package net.slimevoid.gl.model;

import net.slimevoid.lang.math.Vec3;

public class Model {
	
	public int vao;
	public int count;
	public final Vec3 colPrim, colAlt;
	
	public Model(int vao, int count) {
		this.vao = vao;
		this.count = count;
		colPrim = new Vec3(.8F, .8F, .8F);
		colAlt = new Vec3(.2F, .2F, .2F);
	}
}
