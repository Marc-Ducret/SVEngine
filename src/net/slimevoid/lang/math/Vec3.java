package net.slimevoid.lang.math;

import static java.lang.Math.sqrt;

import java.io.Serializable;

public class Vec3 implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final Vec3 X = new Vec3(1, 0, 0);
	public static final Vec3 Y = new Vec3(0, 1, 0);
	public static final Vec3 Z = new Vec3(0, 0, 1);
	public static final Vec3 NULL = new Vec3();

	public Vec3() {
		this(0, 0, 0);
	}
	
	public Vec3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Vec3 add(Vec3 v) {
		return add(v.x, v.y, v.z);
	}
	
	public Vec3 add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
	
	public Vec3 subst(Vec3 v) {
		return subst(v.x, v.y, v.z);
	}
	
	public Vec3 subst(float x, float y, float z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}
	
	public Vec3 mul(float a) {
		x *= a;
		y *= a;
		z *= a;
		return this;
	}
	
	public Vec3 mul(Vec3 a) {
		x *= a.x;
		y *= a.y;
		z *= a.z;
		return this;
	}
	
	public Vec3 mul(Mat4 mat) {
		return mul(mat, 1);
	}
	
	public Vec3 mul(Mat4 mat, float w) {
		float nx = x * mat.m[0 * 4 + 0] + y * mat.m[1 * 4 + 0] + z * mat.m[2 * 4 + 0] + w * mat.m[3 * 4 + 0];
		float ny = x * mat.m[0 * 4 + 1] + y * mat.m[1 * 4 + 1] + z * mat.m[2 * 4 + 1] + w * mat.m[3 * 4 + 1];
		float nz = x * mat.m[0 * 4 + 2] + y * mat.m[1 * 4 + 2] + z * mat.m[2 * 4 + 2] + w * mat.m[3 * 4 + 2];
		float nw = x * mat.m[0 * 4 + 3] + y * mat.m[1 * 4 + 3] + z * mat.m[2 * 4 + 3] + w * mat.m[3 * 4 + 3];
		x = nx / nw; y = ny / nw; z = nz / nw;
		return this;
	}
	
//	public Vec3 mul(Matrix4d mat, float w) {
//		float nx = x * mat.m00 + y * mat.m01 + z * mat.m02 + w * mat.m03;
//		float ny = x * mat.m10 + y * mat.m11 + z * mat.m12 + w * mat.m13;
//		float nz = x * mat.m20 + y * mat.m21 + z * mat.m22 + w * mat.m23;
//		x = nx; y = ny; z = nz;
//		return this;
//	}
	
	public Vec3 inverse() {
		x = 1 / x;
		y = 1 / y;
		z = 1 / z;
		return this;
	}
	
	public void set(Vec3 v) {
		set(v.x, v.y, v.z);
	}
	
	public Vec3 set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	public boolean isInside(Vec3 pos, Vec3 size) {
		return x > pos.x - size.x / 2 && x < pos.x + size.x / 2 && y > pos.y - size.y / 2 && y < pos.y + size.y / 2 && z > pos.z - size.z / 2 && z < pos.z + size.z / 2;
	}
	
	public void setNul() {
		set(0, 0, 0);
	}
	
	public Vec3 copy() {
		return new Vec3(x, y, z);
	}
	
	public boolean isNul() {
		return x == 0 && y == 0 && z == 0;
	}
	
	public float getNormeSq() {
		return x*x + y*y + z*z;
	}
		
	public float getNorme() {
		return (float) sqrt(getNormeSq());
	}
	
	public Vec3 normalize() {
		float norme = getNorme();
		x /= norme;
		y /= norme;
		z /= norme;
		return this;
	}
	
	public Vec3 negate() {
		x = -x;
		y = -y;
		z = -z;
		return this;
	}
	
	public Vec2 asScreenCoords() {
		return new Vec2(x / z, y / z);
	}
	
//	public Vector3f asVector3f() {
//		return new Vector3f((float) x, (float) y, (float) z);
//	}
	
	@Override
	public String toString() {
		return "x: "+x+" y: "+y+" z: "+z;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Vec3) {
			Vec3 v = (Vec3) obj;
			return v.x == this.x && v.y == this.y && v.z == this.z;
		}
		return super.equals(obj);
	}
	
	public static Vec3 cross(Vec3 u, Vec3 v) {
		return new Vec3(u.y * v.z - u.z * v.y, u.z * v.x - u.x * v.z, u.x * v.y - u.y * v.x);
	}
	
	public float x, y, z;

	public static float dot(Vec3 u, Vec3 v) {
		return u.x * v.x + u.y * v.y + u.z * v.z;
	}
}
