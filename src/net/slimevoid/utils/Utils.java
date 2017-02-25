package net.slimevoid.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils {
	
	public static String readRessource(String path) throws IOException {
		InputStream stream = Utils.class.getResourceAsStream(path);
		if(stream == null) throw new IOException("No such ressource "+path);
		BufferedReader read = new BufferedReader(new InputStreamReader(stream));
		StringBuilder buf = new StringBuilder();
		String line;
		while((line = read.readLine()) != null) buf.append(line).append('\n');
		read.close();
		return buf.toString();
	}
}
