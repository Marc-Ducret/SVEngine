package net.slimevoid.gl.test;

import static java.lang.Math.sin;

import net.slimevoid.gl.Camera;
import net.slimevoid.gl.Drawable;
import net.slimevoid.gl.GLInterface;
import net.slimevoid.lang.math.Mat4;
import net.slimevoid.lang.math.Vec3;

public class EngineTest {

	private static final long TICK_LEN = 50000;
	
	static Drawable object;
	public static void main(String[] args) throws InterruptedException {
		GLInterface.start(1280, 720, "Engine Test!", false);
		object = new Drawable(GLInterface.getModelManager().getModel("ico"));
		GLInterface.addDrawable(object);
		
		while(GLInterface.isAlive()) {
			long start = GLInterface.getTimeMicro();
			tick();
			GLInterface.provideTickInfo(start+TICK_LEN);
			Thread.sleep(TICK_LEN/1000);
		}
		System.exit(0);
	}
	
	static int ct = 0;
	private static void tick() {
		ct ++;
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
