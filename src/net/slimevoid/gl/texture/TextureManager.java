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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTruetype;

import net.slimevoid.utils.Utils;

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
		if(name.startsWith("#font_")) {
			return loadFont(name.substring(6));
		} else {
			InputStream in = TextureManager.class.getResourceAsStream("/"+textureFolder+"/"+name+".png");
			if(in == null) throw new IOException("No such file");
			return allocateImageTexture(ImageIO.read(in));
		}
	}
	
	private Texture loadFont(String name) throws IOException {
		String[] args = name.split("_");
		if(args.length != 2) throw new IOException("Incorrect font name ["+name+"]");
		String fontName = args[0];
		try {
			int size = Integer.parseInt(args[1]);
			int texID = glGenTextures();
			int bitmapSize = 256;
			STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);

			ByteBuffer ttf = Utils.ioResourceToByteBuffer("/"+textureFolder+"/"+fontName+".ttf", 160 * 1024);

			ByteBuffer alphaMap = BufferUtils.createByteBuffer(bitmapSize * bitmapSize);
			ByteBuffer rgba = BufferUtils.createByteBuffer(bitmapSize * bitmapSize * 4);
			STBTruetype.stbtt_BakeFontBitmap(ttf, size, alphaMap, bitmapSize, bitmapSize, 32, cdata);
			rgba.clear();
			for(int i = 0; i < bitmapSize * bitmapSize; i++) {
				rgba.put(4*i + 0, (byte) 0xFF);
				rgba.put(4*i + 1, (byte) 0xFF);
				rgba.put(4*i + 2, (byte) 0xFF);
				rgba.put(4*i + 3, alphaMap.get(i));
			}
			
			glBindTexture(GL_TEXTURE_2D, texID);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bitmapSize, bitmapSize, 0, GL_RGBA, GL_UNSIGNED_BYTE, rgba);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

			return new TextureFont(bitmapSize, bitmapSize, texID, cdata);
		} catch(NumberFormatException e) { throw new IOException("Incorrect font name ["+name+"]");}
	}
	
	private Texture allocateImageTexture(BufferedImage img) {
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
