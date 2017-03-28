package net.slimevoid.utils;

import static org.lwjgl.BufferUtils.createByteBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lwjgl.BufferUtils;

public class Utils {
	
	/**
	 * Start with / for inside jar?
	 */
	public static String readRessource(String path) throws IOException {
		if(!path.startsWith("/")) path = "/"+path;
		InputStream stream = Utils.class.getResourceAsStream(path);
		if(stream == null) throw new IOException("No such ressource "+path);
		BufferedReader read = new BufferedReader(new InputStreamReader(stream));
		StringBuilder buf = new StringBuilder();
		String line;
		while((line = read.readLine()) != null) buf.append(line).append('\n');
		read.close();
		return buf.toString();
	}
	
	//LWJGL demo code...
	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
		ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}

	/**
	 * Reads the specified resource and returns the raw data as a ByteBuffer.
	 *
	 * @param resource   the resource to read
	 * @param bufferSize the initial buffer size
	 *
	 * @return the resource data
	 *
	 * @throws IOException if an IO error occurs
	 */
	public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
		ByteBuffer buffer;

		Path path = Paths.get(resource);
		if ( Files.isReadable(path) ) {
			try (SeekableByteChannel fc = Files.newByteChannel(path)) {
				buffer = BufferUtils.createByteBuffer((int)fc.size() + 1);
				while ( fc.read(buffer) != -1 ) ;
			}
		} else {
			InputStream source = Utils.class.getResourceAsStream(resource);
			if(source == null) throw new IOException("No such ressource "+resource);
			try (
				ReadableByteChannel rbc = Channels.newChannel(source)
			) {
				buffer = createByteBuffer(bufferSize);

				while ( true ) {
					int bytes = rbc.read(buffer);
					if ( bytes == -1 )
						break;
					if ( buffer.remaining() == 0 )
						buffer = resizeBuffer(buffer, buffer.capacity() * 2);
				}
			}
		}

		buffer.flip();
		return buffer;
	}
}
