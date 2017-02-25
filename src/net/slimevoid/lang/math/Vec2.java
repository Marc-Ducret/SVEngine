package net.slimevoid.lang.math;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;


public class Vec2 {
	
	public static final Vec2 NULL = new Vec2();
	
	public double x, y;
	
	public Vec2() {
		this(0, 0);
	}
	
	public Vec2(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public Vec2 add(Vec2 v) {
		return add(v.x, v.y);
	}
	
	public Vec2 add(double x, double y) {
		this.x += x;
		this.y += y;
		return this;
	}
	
	public Vec2 subst(Vec2 v) {
		return subst(v.x, v.y);
	}
	
	public Vec2 subst(double x, double y) {
		this.x -= x;
		this.y -= y;
		return this;
	}
	
	public Vec2 mul(double a) {
		x *= a;
		y *= a;
		return this;
	}
	
	public Vec2 mul(Vec2 a) {
		x *= a.x;
		y *= a.y;
		return this;
	}
	
	public Vec2 inverse() {
		x = 1 / x;
		y = 1 / y;
		return this;
	}
	
	public double getNormeSq() {
		return pow(x, 2) + pow(y, 2);
	}
	
	public double getNorme() {
		return sqrt(getNormeSq());
	}
	
	public Vec2 normalize() {
		double n = getNorme();
		if(n == 0) {
			x = 0;
			y = 0;
		} else {
			x /= n;
			y /= n;
		}
		return this;
	}
	
	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean isInside(Vec2 pos, Vec2 size) {
		return x > pos.x - size.x / 2 && x < pos.x + size.x / 2 && y > pos.y - size.y / 2 && y < pos.y + size.y / 2;
	}
	
	public void setNul() {
		set(0, 0);
	}
	
	public Vec2 copy() {
		return new Vec2(x, y);
	}
	
	public boolean isNul() {
		return x == 0 && y == 0;
	}
	
	public static double dot(Vec2 u, Vec2 v) {
		Vec2 u0 = u.copy().normalize();
		Vec2 v0 = v.copy().normalize();
		return u0.x * v0.x + u0.y * v0.y;
	}
	
	@Override
	public String toString() {
		return "x: "+x+" y: "+y;
	}
}
