package net.slimevoid.gl.gui;

import net.slimevoid.lang.math.Vec3;

public class Rectangle {
	
	private static Rectangle pool;
	
	public int x, y, w, h;
	public String texture;
	public Rectangle next;
	public int texX, texY, texW, texH;
	public Vec3 color = new Vec3(1, 1, 1);
	
	private Rectangle() {}
	
	public Rectangle setTextureOffset(int x, int y) {
		texX = x;
		texY = y;
		return this;
	}
	
	public Rectangle setColor(float r, float g, float b) {
		color.set(r, g, b);
		return this;
	}
	
	public void free() {
		next = pool;
		pool = this;
	}
	
	public static Rectangle poolRectangle(int x, int y, int w, int h, String texture) { //TODO test String allocation cost
		Rectangle r;
		if(pool != null) {
			r = pool;
			pool = pool.next;
		} else r = new Rectangle();
		r.x = x;
		r.y = y;
		r.w = w;
		r.h = h;
		r.texture = texture;
		r.texX = 0;
		r.texY = 0;
		r.texW = w;
		r.texH = h;
		r.color.set(1, 1, 1);
		return r;
	}
}
