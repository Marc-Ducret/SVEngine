package net.slimevoid.gl.gui;

public class Rectangle {
	
	private static Rectangle pool;
	
	public int x, y, w, h;
	public String texture;
	public Rectangle next;
	
	private Rectangle() {}
	
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
		return r;
	}
}
