package net.slimevoid.gl.model;

import static java.lang.Math.abs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.slimevoid.lang.math.Vec3;

public abstract class ModelLoader {
	
	protected static class Triangle {
		final Vertice[] pts = new Vertice[3];
		final Vec3 norm = new Vec3();
		final boolean[] outerEdges = new boolean[3];
		final int style;
		
		Triangle(int style) {
			this.style = style;
		}
		
		boolean containsVert(Vertice v) {
			for(Vertice vert : pts) if(vert == v) return true;
			return false;
		}
	}
	
	protected static class Vertice {
		final Vec3 pos = new Vec3();
		final List<Triangle> adjTrigs = new ArrayList<>();
	}
	
	protected abstract Model loadModel(ModelManager mm, String path) throws IOException;
	
	protected Model buildModel(ModelManager mm, List<Vertice> verts, List<Triangle> trigs) {
		processOuter(verts, trigs);
		float[] geom = new float[trigs.size() * 3 * 2 * 3];
		for(int i = 0; i < trigs.size() * 3 * 2; i ++) {
			Vec3 v = i%2 == 0 ? trigs.get(i/6).pts[(i/2)%3].pos : trigs.get(i/6).norm;
			geom[i * 3 + 0] = v.x; geom[i * 3 + 1] = v.y; geom[i * 3 + 2] = v.z;
		}
		
		int[] styles = new int[trigs.size() * 3];
		for(int i = 0; i < trigs.size() * 3; i ++) styles[i] = trigs.get(i/3).style;
		
		float[] edgeWear = new float[trigs.size() * 3 * 6 * 3];
		for(int i = 0; i < trigs.size(); i ++) {
			Triangle t = trigs.get(i);
			for(int e = 0; e < 3; e++) {
				Vertice vA = t.pts[e], vB = t.pts[(e+1)%3];
				Vec3 point = new Vec3().add(vA.pos).add(vB.pos).mul(.5F);
				Vec3 orth;
				if(t.outerEdges[e]) 
					orth = Vec3.cross(new Vec3().add(vB.pos).subst(vA.pos).normalize(), t.norm);
				else orth = Vec3.NULL;
				for(int v = 0; v < 3; v ++) {
					int base = i*3*6*3 + v*6*3 + e*3;
					edgeWear[base + 0] = point.x; edgeWear[base + 1] = point.y; edgeWear[base + 2] = point.z;
					edgeWear[base + 9] = orth.x;  edgeWear[base + 10] = orth.y; edgeWear[base + 11] = orth.z;
				}
			}
		}
				
		return new Model(mm.createVertexArray(geom, styles, edgeWear), trigs.size() * 3);
	}
	
	private void processOuter(List<Vertice> verts, List<Triangle> trigs) {
		for(Triangle t : trigs) {
			for(int e = 0; e < 3; e++) {
				Vertice vA = t.pts[e], vB = t.pts[(e+1)%3];
				for(Triangle tB : vB.adjTrigs)
					if(tB != t && tB.containsVert(vA) && abs(Vec3.dot(t.norm, tB.norm)) < .8F) {
						t.outerEdges[e] = true;
					}
			}
		}
	}
}
