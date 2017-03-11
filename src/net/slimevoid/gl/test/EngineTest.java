package net.slimevoid.gl.test;

import static java.lang.Math.sin;

import net.slimevoid.gl.Camera;
import net.slimevoid.gl.Drawable;
import net.slimevoid.gl.GLInterface;
import net.slimevoid.gl.gui.Component;
import net.slimevoid.gl.gui.Gui;
import net.slimevoid.gl.gui.component.Button;
import net.slimevoid.lang.math.Mat4;
import net.slimevoid.lang.math.Vec3;

public class EngineTest {

	private static final long TICK_LEN = 50000;
	
	static Drawable object;
	public static void main(String[] args) throws InterruptedException {
		GLInterface.start(1280, 720, "Engine Test!", false);
		object = new Drawable(GLInterface.getModelManager().getModel("ico"));
		GLInterface.addDrawable(object);
		
		Gui g = new Gui();
		Button b = new Button("Press!", () -> {run = !run;});
		b.constrain(Component.N, null, Component.N, -25);
		b.constrain(Component.W, null, Component.W, 25 );
		b.setSize(150, 40);
		g.addComponent(b);
		GLInterface.changeGui(g);
		while(GLInterface.isAlive()) {
			long start = GLInterface.getTimeMicro();
			tick();
			GLInterface.provideTickInfo(start+TICK_LEN);
			Thread.sleep(TICK_LEN/1000);
		}
	}
	
	static boolean run = false;
	static int ct = 0;
	private static void tick() {
		if(run) ct +=10;
		Camera cam = GLInterface.getCamera();
		synchronized(cam) {
			cam.resetPos();
			cam.translate(2, 2, 3);
			cam.lookAt(0, 0, 0);
		}
		Mat4 mat = new Mat4();
		mat.setTranslate(0, (float) sin(ct * .05F), 0);
		mat.rotate(ct * .02F, Vec3.Z);
		object.updateMat(mat);
	}
}
