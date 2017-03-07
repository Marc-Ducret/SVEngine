package net.slimevoid.gl.gui;

import java.util.ArrayList;
import java.util.List;

public class Gui {

	private Gui parent;
	private final boolean opaque;
	private final List<Component> comps = new ArrayList<>();
	
	public Gui() {
		this(true);
	}
	
	public Gui(boolean opaque) {
		this.opaque = opaque;
	}
	
	public void addComponent(Component comp) {
		synchronized(comps) {
			comps.add(comp);
		}
	}
	
	public void setParent(Gui parent) {
		this.parent = parent;
	}
	
	public Gui getParent() {
		return parent;
	}
	
	public boolean hasParent() {
		return getParent() != null;
	}
	
	public boolean isOpaque() {
		return opaque;
	}
	
	public void draw() {
		for(Component comp : comps) comp.draw();
	}
	
	public void solve() {
		for(Component comp : comps) comp.reset();
		for(Component comp : comps) comp.solve();
	}
	
	public void mouseMoved(int x, int y) {
		for(Component comp : comps) comp.mouseMoved(x, y);
	}
	
	public void mouseButtonChanged(int button, int action) {
		for(Component comp : comps) if(comp.isMouseInside()) comp.mouseButtonChanged(button, action);
	}
	
	public void keyChanged(int keycode, int action) {
		for(Component comp : comps) comp.keyChanged(keycode, action);
	}
}
