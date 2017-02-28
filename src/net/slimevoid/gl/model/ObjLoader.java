package net.slimevoid.gl.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

import net.slimevoid.lang.math.Vec3;
import net.slimevoid.utils.Utils;

public class ObjLoader extends ModelLoader {
	
	private static class Triangle {
		final Vec3[] pts = new Vec3[3];
		final Vec3[] norm = new Vec3[3];
		final int style;
		
		public Triangle(int style) {
			this.style = style;
		}
	}

	@Override
	protected Model loadModel(ModelManager mm, String path) throws IOException {
		Scanner scan = new Scanner(Utils.readRessource(path+".obj").replaceAll("(#|o ).*[\\n\\t]", "").replaceAll("//", " "));
		scan.useLocale(Locale.UK);
		String err = null;
		try {
			if(!scan.next().equals("mtllib")) err = "Missing mtllib";
			else {
				scan.next();// matlib name is irrelevant 
				List<Vec3> vert = new ArrayList<>();
				List<Vec3> norm = new ArrayList<>();
				int curStyle = -1;
				List<Triangle> trigs = new ArrayList<>();
				while(scan.hasNext()) {
					String tok = scan.next();
					if(tok.equals("v")) vert.add(new Vec3(scan.nextFloat(), scan.nextFloat(), scan.nextFloat()));
					else if(tok.equals("vn")) norm.add(new Vec3(scan.nextFloat(), scan.nextFloat(), scan.nextFloat()));
					else if(tok.equals("usemtl")) {
						String matName = scan.next();
						int mat;
						try {
							mat = mm.getMaterialIndex(matName);
						} catch (RuntimeException e) {
							err = "Unknown material "+matName;
							break;
						}
						int colorType;
						if(!scan.hasNextInt() || (colorType = scan.nextInt()) > 1 || colorType < 0) {
							err = "Incorrect color index";
							break;
						}
						curStyle = 2 * mat + colorType;
					}
					else if(tok.equals("f")) {
						Triangle t = new Triangle(curStyle);
						for(int i = 0; i < 3; i ++) {
							t.pts[i] = vert.get(scan.nextInt()-1);
							t.norm[i] = norm.get(scan.nextInt()-1);
						}
						trigs.add(t);
					} else {
						err = "Unknown token ["+tok+"]";
						break;
					}
				}
				float[] buf = new float[trigs.size() * 3 * 2 * 3];
				int[] styles = new int[trigs.size() * 3];
				for(int i = 0; i < trigs.size() * 3 * 2; i ++) {
					Vec3 v = i%2 == 0 ? trigs.get(i/6).pts[(i/2)%3] : trigs.get(i/6).norm[(i/2)%3];
					buf[i * 3 + 0] = v.x; buf[i * 3 + 1] = v.y; buf[i * 3 + 2] = v.z;
				}
				for(int i = 0; i < trigs.size() * 3; i ++) styles[i] = trigs.get(i/3).style;
				if(err == null) {
					scan.close();
					return new Model(mm.createVertexArray(buf, styles), trigs.size() * 3);
				}
			}
		} catch(NoSuchElementException e) {
			err = e.getMessage() != null ? e.getMessage() : "Scan error";
			e.printStackTrace();
		}
		scan.close();
		throw new IOException("Format error while reading "+path+".obj: "+err);
	}
}
