package net.slimevoid.gl.texture;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class TextureManager {
	
	private final String textureFolder;
	private final Map<String, Texture> textures = new HashMap<>();
	
	public TextureManager(String textureFolder) {
		this.textureFolder = textureFolder;
	}
	
	public Texture getTexture(String name) {
		if(!textures.containsKey(name)) {
			try {
				textures.put(name, loadTexture(name));
			} catch (IOException e) {
				throw new RuntimeException("Can't load texture "+name, e);//TODO better error handleing
			}
		}
		return textures.get(name);
	}
	
	private Texture loadTexture(String name) throws IOException {
		BufferedImage im;
		if(name.startsWith("#font_")) {
			im = loadFont(name.substring(6));
		} else {
			InputStream in = TextureManager.class.getResourceAsStream("/"+textureFolder+"/"+name+".png");
			if(in == null) throw new IOException("No such file");
			im = ImageIO.read(in);
		}
		return allocateTexture(im);
	}
	
	private BufferedImage loadFont(String name) throws IOException {
		String[] args = name.split("_");
		if(args.length != 2) throw new IOException("Incorrect font name ["+name+"]");
		String fontName = args[0];
		try {
			int size = Integer.parseInt(args[1]);
			Font f = new Font(fontName, 0, size);
			int bitmapSize = 256;
			BufferedImage im = new BufferedImage(bitmapSize, bitmapSize, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = im.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setColor(new Color(0, 0, 0, 0));
			g.fillRect(0, 0, bitmapSize, bitmapSize);
			g.setColor(new Color(0xFFFFFFFF));
			g.setFont(f);
			for(int i = 0; i < 256; i++) {
				g.drawString(""+(char) i, (i%16)*16, 16+(i/16)*16);
			}
			return im;
		} catch(NumberFormatException e) { throw new IOException("Incorrect font name ["+name+"]");}
	}
	
	private Texture allocateTexture(BufferedImage img) {
    	int w = img.getWidth();
        int h = img.getHeight();
        int[] pixels = img.getRGB(0, 0, w, h, null, 0, w);
        byte[] rgba = new byte[w * h * 4];
        ByteBuffer pxs = ByteBuffer.allocateDirect(w * h * 4);
        
        for(int y = 0; y < h; y ++) {
        	for(int x = 0; x < w; x ++) {
        		int i = x + y * w;
        		int a = pixels[i] >> 24 & 0xFF;
	            int r = pixels[i] >> 16 & 0xFF;
	            int g = pixels[i] >> 8  & 0xFF;
	            int b = pixels[i] >> 0  & 0xFF;
	
	            i = x + (h - y - 1) * w;
	            rgba[i * 4 + 0] = (byte) r;
	            rgba[i * 4 + 1] = (byte) g;
	            rgba[i * 4 + 2] = (byte) b;
	            rgba[i * 4 + 3] = (byte) a;
        	}
        }

        pxs.clear();
        pxs.put(rgba);
        pxs.flip();

        int texture = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, texture);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, pxs);
        return new Texture(w, h, texture);
    }
}
