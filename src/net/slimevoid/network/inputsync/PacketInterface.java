package net.slimevoid.network.inputsync;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class PacketInterface {
	
	private final byte[] buf;
	private final DatagramPacket pak;
	private int cursor;
	
	public PacketInterface(int bufferSize) {
		buf = new byte[bufferSize];
		pak = new DatagramPacket(buf, bufferSize);
		cursor = 0;
	}
	
	public void write(int b) throws IOException {
		if(cursor >= buf.length) throw new IOException("Buffer overflow");
		buf[cursor] = (byte) b;
		cursor ++;
	}
	
	public int read() throws IOException {
		if(cursor >= buf.length) throw new IOException("Buffer underflow");
		return buf[cursor ++] & 0xFF;
	}
	
	public void writeInt(int i) throws IOException {
		for(int j = 0; j < 4; j ++) {
			write(i & 0xFF);
			i >>= 8;
		}
	}
	
	public int readInt() throws IOException {
		int i = 0;
		for(int j = 0; j < 4; j ++) {
			int r = (int) read();
			i += (r << (j * 8));
		}
		return i;
	}
	
	public void reset() {
		cursor = 0;
	}
	
	public DatagramPacket preparePacket(InetAddress iaddr, int port) {
		pak.setLength(cursor);
		pak.setAddress(iaddr);
		pak.setPort(port);
		reset();
		return pak;
	}

	public DatagramPacket preparePacket() {
		pak.setLength(buf.length);
		reset();
		return pak;
	}
}
