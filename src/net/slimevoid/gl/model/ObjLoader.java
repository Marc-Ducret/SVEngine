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
	

	@Override
	protected Model loadModel(ModelManager mm, String path) throws IOException {
		Scanner scan = new Scanner(Utils.readRessource(path+".obj").replaceAll("(#|o ).*[\\n\\t]", "").replaceAll("//", " "));
		scan.useLocale(Locale.UK);
		String err = null;
		try {
			if(!scan.next().equals("mtllib")) err = "Missing mtllib";
			else {
				scan.next();// matlib name is irrelevant 
				List<Vertice> verts = new ArrayList<>();
				List<Vec3> norms = new ArrayList<>();
				int curStyle = -1;
				List<Triangle> trigs = new ArrayList<>();
				while(scan.hasNext()) {
					String tok = scan.next();
					if(tok.equals("v")) {
						Vertice v = new Vertice();
						v.pos.set(scan.nextFloat(), scan.nextFloat(), scan.nextFloat());
						verts.add(v);
					}
					else if(tok.equals("vn")) norms.add(new Vec3(scan.nextFloat(), scan.nextFloat(), scan.nextFloat()));
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
							Vertice v = verts.get(scan.nextInt()-1);
							v.adjTrigs.add(t);
							t.pts[i] = v;
							t.norm.set(norms.get(scan.nextInt()-1));
						}
						trigs.add(t);
					} else {
						err = "Unknown token ["+tok+"]";
						break;
					}
				}
				if(err == null) {
					scan.close();
					return buildModel(mm, verts, trigs);
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
