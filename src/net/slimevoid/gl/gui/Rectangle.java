package net.slimevoid.gl.gui;

public class Rectangle {
	
	public final int x, y, w, h;
	public Rectangle next;
	
	public Rectangle(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
}
