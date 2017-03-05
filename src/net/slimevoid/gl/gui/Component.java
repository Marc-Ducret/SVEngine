package net.slimevoid.gl.gui;

import net.slimevoid.gl.GLInterface;

public class Component {
	
	public static final int W = 0, S = 1, E = 2, N = 3;
	
	private ConstrainedInteger sX = new ConstrainedInteger();
	private ConstrainedInteger sY = new ConstrainedInteger();
	private ConstrainedInteger eX = new ConstrainedInteger();
	private ConstrainedInteger eY = new ConstrainedInteger();
	private ConstrainedInteger[] coords = new ConstrainedInteger[]{sX, sY, eX, eY};
	
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
	
	public void reset() {
		for(ConstrainedInteger coord : coords) coord.reset();
	}
	
	public void solve() {
		for(ConstrainedInteger coord : coords) coord.solve();
	}
	
	public void draw() {
		GLInterface.addRectangle(Rectangle.poolRectangle(getX(), getY(), getW(), getH(), "tex"));
	}
}
