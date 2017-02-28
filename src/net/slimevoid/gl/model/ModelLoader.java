package net.slimevoid.gl.model;

import java.io.IOException;

public abstract class ModelLoader {
	
	protected abstract Model loadModel(ModelManager mm, String path) throws IOException;
}
