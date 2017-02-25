package net.slimevoid.lang.math;

import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import net.slimevoid.lang.Stack;

public class Mat4 {
	
	public float m[];
	private Stack<Mat4> backupStack;
	
	public Mat4() {
		m = new float[16];
		backupStack = new Stack<>();
	}
	
	public void push() {
		backupStack.push(this.copy());
	}
	
	public void pop() {
		m = backupStack.pop().m;
	}

	public Mat4 loadIdentity() {
		for(int y = 0; y < 4; y++) {
			for(int x = 0; x < 4; x++) {
				m[y * 4 + x] = y == x ? 1 : 0;
			}
		}
		return this;
	}
	
//	public Mat4 setOrthoProjection(float r, float l, float t, float b, float near, float far) {
//		loadIdentity();
//		m[0 * 4 + 0] = 2 / (r - l);
//		m[1 * 4 + 1] = 2 / (t - b);
//		m[2 * 4 + 2] = - 2 / (far - near);
//		m[0 * 4 + 3] = -(r + l) / (r - l);
//		m[1 * 4 + 3] = -(t + b) / (t - b);
//		m[2 * 4 + 3] = -(far + near) / (far - near);
//		return this;
//	}
	
	public Mat4 setPerspectiveProjectection(float aspect, float fov, float near, float far) {
		m[0 * 4 + 0] = (float) (1 / tan(fov / 2) / aspect);
		m[1 * 4 + 1] = (float) (1 / tan(fov / 2));
		m[2 * 4 + 2] = - (far + near) / (far - near);
		m[2 * 4 + 3] = -1;
		m[2 * 4 + 3] = (float) -(pow(pow(2, far), near) / (far - near));
		m[3 * 4 + 3] = 0.01F;
		return this;
	}
	
	public Mat4 setTranslate(Vec3 v) {
		loadIdentity();
		m[3 * 4 + 0] = v.x;
		m[3 * 4 + 1] = v.y;
		m[3 * 4 + 2] = v.z;
		return this;
	}
	
	public Mat4 setRotate(float angle, Vec3 axis) {
		loadIdentity();
		float cosA = (float) cos(angle);
		float sinA = (float) sin(angle);
		m[0 * 4 + 0] = cosA + axis.x * axis.x * (1 - cosA);
		m[1 * 4 + 0] = axis.x * axis.y * (1 - cosA) - axis.z * sinA;
		m[2 * 4 + 0] = axis.x * axis.z * (1 - cosA) + axis.y * sinA;
		m[0 * 4 + 1] = axis.y * axis.x * (1 - cosA) + axis.z * sinA;
		m[1 * 4 + 1] = cosA + axis.y * axis.y * (1 - cosA);
		m[2 * 4 + 1] = axis.y * axis.z * (1 - cosA) - axis.x * sinA;
		m[0 * 4 + 2] = axis.z * axis.x * (1 - cosA) - axis.y * sinA;
		m[1 * 4 + 2] = axis.z * axis.y * (1 - cosA) + axis.x * sinA;
		m[2 * 4 + 2] = cosA + axis.z * axis.z * (1 - cosA);
		return this;
	}
	
	public Mat4 setScale(Vec3 v) {
		loadIdentity();
		m[0 * 4 + 0] = v.x;
		m[1 * 4 + 1] = v.y;
		m[2 * 4 + 2] = v.z;
		m[3 * 4 + 3] = 1;
		return this;
	}
	
	public Mat4 mul(Mat4 mat) {
		float[] nm = new float[16];
		for(int y = 0; y < 4; y++) {
			for(int x = 0; x < 4; x++) {
				float res = 0;
				for(int i = 0; i < 4; i++) {
					res += this.m[i * 4 + x] * mat.m[y * 4 + i];
				}
				nm[y * 4 + x] = res;
			}
		}
		m = nm;
		return this;
	}
	
	public Mat4 project(float aspect, float fov, float near, float far) {
		return mul(new Mat4().setPerspectiveProjectection(aspect, fov, near, far));
	}
	
	public Mat4 translate(Vec3 v) {
		return mul(new Mat4().setTranslate(v));
	}
	
	public Mat4 rotate(float angle, Vec3 axis) {
		return mul(new Mat4().setRotate(angle, axis));
	}
	
	public Mat4 scale(float s) {
		return scale(new Vec3(s, s, s));
	}
	
	public Mat4 scale(Vec3 v) {
		return mul(new Mat4().setScale(v));
	}
	
	public Mat4 inverse() {
	    float inv[], det;
	    int i;
	    inv = new float[4 * 4];
	    
	    inv[0] = m[5]  * m[10] * m[15] - 
	             m[5]  * m[11] * m[14] - 
	             m[9]  * m[6]  * m[15] + 
	             m[9]  * m[7]  * m[14] +
	             m[13] * m[6]  * m[11] - 
	             m[13] * m[7]  * m[10];

	    inv[4] = -m[4]  * m[10] * m[15] + 
	              m[4]  * m[11] * m[14] + 
	              m[8]  * m[6]  * m[15] - 
	              m[8]  * m[7]  * m[14] - 
	              m[12] * m[6]  * m[11] + 
	              m[12] * m[7]  * m[10];

	    inv[8] = m[4]  * m[9] * m[15] - 
	             m[4]  * m[11] * m[13] - 
	             m[8]  * m[5] * m[15] + 
	             m[8]  * m[7] * m[13] + 
	             m[12] * m[5] * m[11] - 
	             m[12] * m[7] * m[9];

	    inv[12] = -m[4]  * m[9] * m[14] + 
	               m[4]  * m[10] * m[13] +
	               m[8]  * m[5] * m[14] - 
	               m[8]  * m[6] * m[13] - 
	               m[12] * m[5] * m[10] + 
	               m[12] * m[6] * m[9];

	    inv[1] = -m[1]  * m[10] * m[15] + 
	              m[1]  * m[11] * m[14] + 
	              m[9]  * m[2] * m[15] - 
	              m[9]  * m[3] * m[14] - 
	              m[13] * m[2] * m[11] + 
	              m[13] * m[3] * m[10];

	    inv[5] = m[0]  * m[10] * m[15] - 
	             m[0]  * m[11] * m[14] - 
	             m[8]  * m[2] * m[15] + 
	             m[8]  * m[3] * m[14] + 
	             m[12] * m[2] * m[11] - 
	             m[12] * m[3] * m[10];

	    inv[9] = -m[0]  * m[9] * m[15] + 
	              m[0]  * m[11] * m[13] + 
	              m[8]  * m[1] * m[15] - 
	              m[8]  * m[3] * m[13] - 
	              m[12] * m[1] * m[11] + 
	              m[12] * m[3] * m[9];

	    inv[13] = m[0]  * m[9] * m[14] - 
	              m[0]  * m[10] * m[13] - 
	              m[8]  * m[1] * m[14] + 
	              m[8]  * m[2] * m[13] + 
	              m[12] * m[1] * m[10] - 
	              m[12] * m[2] * m[9];

	    inv[2] = m[1]  * m[6] * m[15] - 
	             m[1]  * m[7] * m[14] - 
	             m[5]  * m[2] * m[15] + 
	             m[5]  * m[3] * m[14] + 
	             m[13] * m[2] * m[7] - 
	             m[13] * m[3] * m[6];

	    inv[6] = -m[0]  * m[6] * m[15] + 
	              m[0]  * m[7] * m[14] + 
	              m[4]  * m[2] * m[15] - 
	              m[4]  * m[3] * m[14] - 
	              m[12] * m[2] * m[7] + 
	              m[12] * m[3] * m[6];

	    inv[10] = m[0]  * m[5] * m[15] - 
	              m[0]  * m[7] * m[13] - 
	              m[4]  * m[1] * m[15] + 
	              m[4]  * m[3] * m[13] + 
	              m[12] * m[1] * m[7] - 
	              m[12] * m[3] * m[5];

	    inv[14] = -m[0]  * m[5] * m[14] + 
	               m[0]  * m[6] * m[13] + 
	               m[4]  * m[1] * m[14] - 
	               m[4]  * m[2] * m[13] - 
	               m[12] * m[1] * m[6] + 
	               m[12] * m[2] * m[5];

	    inv[3] = -m[1] * m[6] * m[11] + 
	              m[1] * m[7] * m[10] + 
	              m[5] * m[2] * m[11] - 
	              m[5] * m[3] * m[10] - 
	              m[9] * m[2] * m[7] + 
	              m[9] * m[3] * m[6];

	    inv[7] = m[0] * m[6] * m[11] - 
	             m[0] * m[7] * m[10] - 
	             m[4] * m[2] * m[11] + 
	             m[4] * m[3] * m[10] + 
	             m[8] * m[2] * m[7] - 
	             m[8] * m[3] * m[6];

	    inv[11] = -m[0] * m[5] * m[11] + 
	               m[0] * m[7] * m[9] + 
	               m[4] * m[1] * m[11] - 
	               m[4] * m[3] * m[9] - 
	               m[8] * m[1] * m[7] + 
	               m[8] * m[3] * m[5];

	    inv[15] = m[0] * m[5] * m[10] - 
	              m[0] * m[6] * m[9] - 
	              m[4] * m[1] * m[10] + 
	              m[4] * m[2] * m[9] + 
	              m[8] * m[1] * m[6] - 
	              m[8] * m[2] * m[5];

	    det = m[0] * inv[0] + m[1] * inv[4] + m[2] * inv[8] + m[3] * inv[12];

	    if (det == 0)
	        return this;

	    det = 1.0F / det;

	    for (i = 0; i < 16; i++)
	        m[i] = inv[i] * det;

	    return this;
	}
	
//	public Mat4 set(Transform trans) {
//		float[] m = new float[16];
//		trans.getOpenGLMatrix(m);
//		for(int i = 0; i < 16; i ++) {
//			this.m[i] = m[i];
//		}
//		return this;
//	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int y = 0; y < 4; y ++) {
			for(int x = 0; x < 4; x ++) {
				int max = 0;
				for(int y2 = 0; y2 < 4; y2 ++) {
					if((m[y2 * 4 + x]+" | ").length() > max) {
						max = (m[y2 * 4 + x]+" | ").length();
					}
				}
				StringBuilder num = new StringBuilder(" | "+ m[y * 4 + x]);
				while(num.length() < max) {
					num.append(" ");
				}
				sb.append(num);
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
//	public Transform asBulletTransform() {
//        Transform trans = new Transform();
//        trans.setIdentity();
//        float[] mat = new float[16];
//        for(int i  = 0; i < 16; i ++) {
//            int x = i % 4;
//            int y = i / 4;
//            mat[y * 4 + x] = (float) m[y * 4 + x];
//        }
//        trans.setFromOpenGLMatrix(mat);
//        return trans;
//    }

	public Mat4 copy() {
		Mat4 mat = new Mat4();
		for(int i = 0; i < 16; i ++) {
			mat.m[i] = this.m[i];
		}
		return mat;
  	}
}
