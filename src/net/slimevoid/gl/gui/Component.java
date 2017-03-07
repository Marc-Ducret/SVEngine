package net.slimevoid.gl.gui;

import net.slimevoid.gl.GLInterface;

public class Component {
	
	public static final int W = 0, S = 1, E = 2, N = 3;
	
	private ConstrainedInteger sX = new ConstrainedInteger();
	private ConstrainedInteger sY = new ConstrainedInteger();
	private ConstrainedInteger eX = new ConstrainedInteger();
	private ConstrainedInteger eY = new ConstrainedInteger();
	private ConstrainedInteger[] coords = new ConstrainedInteger[]{sX, sY, eX, eY};
	private boolean mouseInside;
	
	public int getX() {
		return sX.getValue();
	}
	
	public int getY() {
		return sY.getValue();
	}
	
	public int getW() {
		return eX.getValue() - sX.getValue();
	}
	
	public int getH() {
		return eY.getValue() - sY.getValue();
	}
	
	public void constrain(int localDirection, Component parent, int parentDirection, int offset) {
		if(parentDirection % 2 != localDirection % 2) throw new IllegalArgumentException("Incompatible directions");
		if(parent == null) 
			switch(parentDirection) {
			case E: offset += GLInterface.windowWidth; break;
			case N: offset += GLInterface.windowHeight; break;
			}
		coords[localDirection].setConstraint(parent == null ? null : parent.coords[parentDirection], offset);
	}
	
	public void setSize(int w, int h) {
		setWidth(w); setHeight(h);
	}
	
	public void setWidth(int w) {
		if(sX.isConstrained()) 		eX.setConstraint(sX, +w);
		else if(eX.isConstrained())	sX.setConstraint(eX, -w);
		else throw new RuntimeException("No horizontal constraint on "+this);
	}
	
	public void setHeight(int h) {
		if(sY.isConstrained()) 		eY.setConstraint(sY, +h);
		else if(eY.isConstrained())	sY.setConstraint(eY, -h);
		else throw new RuntimeException("No vertical constraint on "+this);
	}
	
	public void reset() {
		for(ConstrainedInteger coord : coords) coord.reset();
	}
	
	public void solve() {
		for(ConstrainedInteger coord : coords) coord.solve();
	}
	
	public void draw() {}
	public void mouseEntered() {}
	public void mouseLeft() {}
	public void mouseMovedInside(int x, int y) {}
	public void mouseButtonChanged(int button, int action) {}
	public void keyChanged(int keycode, int action) {}
	
	public final void mouseMoved(int x, int y) {
		boolean inside = x >= getX() && x <= getX() + getW() && y >= getY() && y <= getY() + getH();
		if(inside && !mouseInside) mouseEntered();
		if(!inside && mouseInside) mouseLeft();
		mouseInside = inside;
		if(mouseInside) mouseMovedInside(x, y);
	}
	
	public boolean isMouseInside() {
		return mouseInside;
	}
}
