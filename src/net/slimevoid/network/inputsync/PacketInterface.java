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
	
	public void write(byte b) throws IOException {
		if(cursor >= buf.length) throw new IOException("Buffer overflow");
		buf[cursor] = b;
		cursor ++;
	}
	
	public byte read() throws IOException {
		if(cursor >= buf.length) throw new IOException("Buffer underflow");
		return buf[cursor ++];
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
