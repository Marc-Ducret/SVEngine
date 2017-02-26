package net.slimevoid.gl;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.sqrt;

import net.slimevoid.lang.math.Mat4;
import net.slimevoid.lang.math.Vec3;

public class Camera {
	
	public final Mat4 projMat;
	public final Mat4 viewMat;
	private final Vec3 pos;
	private float yaw, pitch, roll;
	
	public Camera() {
		projMat = new Mat4();
		viewMat = new Mat4();
		pos = new Vec3();
	}
	
	public void setupProjection(float aspect, float fov, float near, float far) {
		projMat.setPerspectiveProjectection(aspect, fov, near, far);
	}
	
	public void computePos() {
		pos.setNul();
		pos.mul(viewMat);
		pos.negate();
	}
	
	public void computeMat() {
		viewMat.loadIdentity();
		viewMat.rotate(roll, Vec3.Z);
		viewMat.rotate(pitch, Vec3.X);
		viewMat.rotate(yaw, Vec3.Z);
		viewMat.translate(-pos.x, -pos.y, -pos.z);
	}
	
	public Vec3 getPos() {
		return pos;
	}
	
	public void resetPos() {
		pos.setNul();
	}
	
	public void translate(float x, float y, float z) {
		pos.add(x, y, z);
	}
	
	public void lookAt(float x, float y, float z) {
		float dx = pos.x - x, dy = pos.y - y, dz = pos.z - z;
		yaw = (float) (PI/2 - atan2(dy, dx));
		pitch = (float) atan2(sqrt(dx*dx + dy*dy), dz);
		roll = 0;
	}
}
