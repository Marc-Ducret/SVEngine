package net.slimevoid.gl.gui;

import net.slimevoid.lang.math.Vec3;

public class Rectangle {
	
	private static Rectangle pool;
	
	public float x, y, w, h;
	public String texture;
	public Rectangle next;
	public float texX, texY, texW, texH;
	public Vec3 color = new Vec3(1, 1, 1);
	
	private Rectangle() {}
	
	public Rectangle setTextureOffset(float x, float y) {
		texX = x;
		texY = y;
		return this;
	}
	
	public Rectangle setTextureSize(float w, float h) {
		texW = w;
		texH = h;
		return this;
	}
	
	public void offset(float x, float y) {
		this.x += x; this.y += y;
	}
	
	public Rectangle setColor(int col) {
		return setColor(((col >> 16) & 0xFF) / (float) 0xFF, 
						((col >> 8 ) & 0xFF) / (float) 0xFF,
						((col >> 0 ) & 0xFF) / (float) 0xFF);
	}
	
	public Rectangle setColor(float r, float g, float b) {
		color.set(r, g, b);
		return this;
	}
	
	public Rectangle setTexture(String texture) {
		this.texture = texture;
		return this;
	}
	
	public void free() {
		next = pool;
		pool = this;
	}
	
	public static Rectangle poolRectangle(float x, float y, float w, float h) {
		Rectangle r;
		if(pool != null) {
			r = pool;
			pool = pool.next;
		} else r = new Rectangle();
		r.x = x;
		r.y = y;
		r.w = w;
		r.h = h;
		r.texture = "empty";
		r.texX = 0;
		r.texY = 0;
		r.texW = w;
		r.texH = h;
		r.color.set(1, 1, 1);
		return r;
	}
}
