package net.slimevoid.gl.gui.component;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

import net.slimevoid.gl.GLInterface;
import net.slimevoid.gl.gui.Component;
import net.slimevoid.gl.gui.Rectangle;

public class Button extends Component {

	private String text;
	private Runnable effect;
	private int depthUp = 6;
	private int depthDown = 2;
	private boolean down;
	
	public Button(String text, Runnable effect) {
		this.text = text;
		this.effect = effect;
	}
	
	@Override
	public void draw() {
		int d = down ? depthDown : depthUp;
		Rectangle front = Rectangle.poolRectangle(getX(), getY() + d, getW(), getH() - depthUp);
		Rectangle back  = Rectangle.poolRectangle(getX(), getY(), getW(), d);
		front.setColor(0x30a7e4);
		back.setColor(0x1a597a);
		GLInterface.addTextCenter(text, "arial", getH() / 2, getX() + getW() / 2, getY() + d + getH() / 2, isMouseInside() ? 0xFFFFFF : 0xE0E0FF);
		GLInterface.addRectangle(front);
		GLInterface.addRectangle(back);
	}
	
	@Override
	public void mouseButtonChanged(int button, int action) {
		if(button == GLFW_MOUSE_BUTTON_1) {
			if(down && action != GLFW_PRESS) effect.run();
			down = action == GLFW_PRESS;
		}
	}
	
	@Override
	public void mouseLeft() {
		down = false;
	}
}
