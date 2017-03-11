package net.slimevoid.network.inputsync;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import net.slimevoid.probot.network.NetworkEngineClient;
import net.slimevoid.probot.network.packet.Packet01Login;
import net.slimevoid.probot.network.packet.Packet04PlayRequest;

public class InputSyncSocket {

	public static enum ClientType {A, B}
	
	private static enum PakID {INPUT, ACK};
	
	private static final int TIMEOUT = 200;
	
	public final ClientType type;
	private final String addr;
	private InetAddress iaddr;
	private final int port;
	private final int localPort;
	private DatagramSocket sok;
	private PacketInterface sendPak;
	private PacketInterface recePak;
	
	private boolean alive;
	
	public InputSyncSocket(String addr, int port, int localPort, ClientType type) {
		this.addr = addr;
		this.port = port;
		this.localPort = localPort;
		this.type = type;
		alive = false;
		sendPak = new PacketInterface(1024);
		recePak = new PacketInterface(1024);
	}
	
	public boolean connect(int tries) {
		byte hello = 0x57, ok = -0x73;
		try {
			sok = new DatagramSocket(localPort);
			sok.setSoTimeout(TIMEOUT);
			iaddr = InetAddress.getByName(addr);
			byte msg = hello;
			for(int i = 0; i < tries; i ++) {
				try {
					sendPak.write(msg);
					sok.send(sendPak.preparePacket(iaddr, port));
					sok.receive(recePak.preparePacket());
					byte recMsg = recePak.read();
					if(recMsg == ok) {
						for(int j = 0; j < tries; j ++) {
							sendPak.write(ok);
							sok.send(sendPak.preparePacket(iaddr, port));
						}
						init();
						return true;
					}
					else if(recMsg == hello) msg = ok;
				} catch(Exception e) {}
			}
		} catch(IOException e) {
			e.printStackTrace(); //TODO rm
		}
		return false;
	}
	
	private void init() {
		alive = true;
		new Thread(() -> {
			while(alive) {
				try {
					sok.receive(recePak.preparePacket());
					System.out.println("read: "+recePak.read());
				} catch(Exception e) {}
			}
		}).start();
		try {
			byte msg = (byte) (System.currentTimeMillis() % 0xFF);
			sendPak.write(msg);
			sok.send(sendPak.preparePacket(iaddr, port));
			System.out.println("write: "+msg);
		} catch(Exception e) {
			System.out.println("SEND fail "+e);
		}
	}
	
	public static void main(String[] args) {
		System.out.println("Fight!");//TODO rm
		NetworkEngineClient net = new NetworkEngineClient();
		if(!net.connect("89.156.241.115", 8004)) {
			System.out.println("Connection unsuccessful :("); //TODO addr?
			return;
		}
		net.sendAll(new Packet01Login(System.getProperty("user.name")+Integer.toHexString((int) (System.currentTimeMillis() % 0xFF))));
		net.sendAll(new Packet04PlayRequest());
		net.startUpdateThread();
	}
}
