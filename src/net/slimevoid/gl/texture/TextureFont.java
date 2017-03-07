package net.slimevoid.gl.texture;

import org.lwjgl.stb.STBTTBakedChar;

public class TextureFont extends Texture {

	private STBTTBakedChar.Buffer charData;
	
	public TextureFont(int w, int h, int texId, STBTTBakedChar.Buffer cdata) {
		super(w, h, texId);
		this.charData = cdata;
	}
	
	public STBTTBakedChar.Buffer getCharData() {
		return charData;
	}
}
