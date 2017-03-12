package net.slimevoid.network.inputsync;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import net.slimevoid.gl.GLInterface;
import net.slimevoid.network.inputsync.InputData.InputDataList;

public class InputSyncSocket {

	public static enum ClientType {A, B}
	
	private static final int PACKET_ID = 0x25;
	
	private static final int TIMEOUT = 200;
	private static final int BUFFER_SIZE = 2048; //TODO config file ;)
	private static final int PACKET_TIMEOUT = 500000;
	
	public final ClientType type;
	private final String addr;
	private InetAddress iaddr;
	private final int port;
	private final int localPort;
	private DatagramSocket sok;
	private PacketInterface sendPak;
	private PacketInterface recePak;
	
	private InputDataList localList = new InputDataList();
	private InputDataList distantList = new InputDataList();
	private InputDataList toSend = new InputDataList();
	
	private int localAck = -1;
	private int distantAck = -1;
	
	private boolean update = false;
	
	private boolean alive;
	private long lastContact;
	
	public InputSyncSocket(String addr, int port, int localPort, ClientType type) {
		this.addr = addr;
		this.port = port;
		this.localPort = localPort;
		this.type = type;
		alive = false;
		sendPak = new PacketInterface(BUFFER_SIZE);
		recePak = new PacketInterface(BUFFER_SIZE);
	}
	
	public boolean connect(int tries) {
		int hello = 0x57, ok = 0xD3;
		try {
			sok = new DatagramSocket(localPort);
			sok.setSoTimeout(TIMEOUT);
			iaddr = InetAddress.getByName(addr);
			int msg = hello;
			for(int i = 0; i < tries; i ++) {
				try {
					sendPak.write(msg);
					sok.send(sendPak.preparePacket(iaddr, port));
					sok.receive(recePak.preparePacket());
					int recMsg = recePak.read();
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
		lastContact = GLInterface.getTimeMicro();
		new Thread(() -> {
			while(alive) {
				try {
					sok.receive(recePak.preparePacket());
					if(recePak.read() != PACKET_ID) continue;
					synchronized(this) {
						lastContact = GLInterface.getTimeMicro();
						int ack = recePak.readInt();
						assertSync(ack >= distantAck);
						if(ack != distantAck) {
							distantAck = ack;
							cleenUpSendList();
						}
						int nb = recePak.read();
						for(int i = 0; i < nb; i ++) {
							InputData indata = InputData.read(recePak);
							if(indata.getTickID() <= localAck) indata.free();
							else {
								assertSync(indata.getTickID() == localAck + 1);
								distantList.append(indata);
								localAck++;
								update = true;
							}
						}
					}
					Thread.sleep(1);
				} catch(Exception e) {}
				if(GLInterface.getTimeMicro() - lastContact > PACKET_TIMEOUT) {
					System.out.println("TIMEOUT!");//TODO rm
					alive = false;
				}
			}
		}).start();
		new Thread(() -> {
			while(alive) {
				try {
					if(update) {
						update = false;
						synchronized(this) {
							sendPak.write(PACKET_ID);
							sendPak.writeInt(localAck);
							sendPak.write(toSend.lenght());
							for(InputData el = toSend.top(); el != null; el = el.next)
								el.write(sendPak);
							sok.send(sendPak.preparePacket(iaddr, port));
						}
					}
					Thread.sleep(1);
				} catch(Exception e) {}
			}
		}).start();
	}
	
	private void assertSync(boolean assertion) {
		if(!assertion) throw new RuntimeException("De-synchronized");;
	}
	
	private synchronized void cleenUpSendList() {
		while(!toSend.isEmpty() && toSend.top().getTickID() <= distantAck) toSend.pop().free();
	}
	
	public synchronized void provideLocalInputData(InputData data) {
		localList.append(data);
		toSend.append(data.clone());
		update = true;
	}
	
	public synchronized boolean isInputDataReady() {
		return !localList.isEmpty() && !distantList.isEmpty();
	}
	
	public InputData getLocalInputData() {
		return localList.top();
	}
	
	public InputData getDistantInputData() {
		return distantList.top();
	}
	
	public synchronized void freeOldestData() {
		localList.pop().free();
		distantList.pop().free();
	}
	
	public boolean isAlive() {
		return alive;
	}
}
