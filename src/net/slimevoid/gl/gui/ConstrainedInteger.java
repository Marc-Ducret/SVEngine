package net.slimevoid.gl.gui;

public class ConstrainedInteger {
	
	private int value;
	private boolean solved;
	
	private ConstrainedInteger parent;
	private int offset;
	private boolean constrained;
	
	public void setConstraint(ConstrainedInteger parent, int offset) {
		this.parent = parent;
		this.offset = offset;
		constrained = true;
	}
	
	public void solve() {
		if(solved) return;
		solved = true;
		if(parent == null) {
			value = offset;
		} else {
			parent.solve();
			value = parent.value + offset;
		}
	}
	
	public void reset() {
		solved = false;
	}

	public int getValue() {
		if(!solved) throw new RuntimeException("Unsolved constraint");
		return value;
	}
	
	public boolean isConstrained() {
		return constrained;
	}
}
