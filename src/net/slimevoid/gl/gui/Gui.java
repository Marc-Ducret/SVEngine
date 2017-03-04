package net.slimevoid.gl.gui;

import java.util.ArrayList;
import java.util.List;

import net.slimevoid.gl.GLInterface;

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
		GLInterface.addRectangle(new Rectangle(0, 0, 100, 100));
	}
}
