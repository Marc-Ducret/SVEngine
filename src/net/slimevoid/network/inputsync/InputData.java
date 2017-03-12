package net.slimevoid.network.inputsync;

import java.io.IOException;

public class InputData {
	
	
	public final static int INPUT_SIZE = 12; // TODO config file
	
	private static InputDataList pool = new InputDataList();
	
	public final boolean[] states;
	private int tickID;
	
	public InputData next;
	
	private InputData() {
		states = new boolean[INPUT_SIZE];
	}
	
	public void free() {
		this.next = null;
		this.tickID = -42;
		pool.push(this);
	}
	
	public int getTickID() {
		return tickID;
	}
	
	public InputData clone() {
		InputData c = poolInputData(tickID);
		c.next = null;
		for(int i = 0; i < INPUT_SIZE; i ++) c.states[i] = states[i];
		return c;
	}
	
	public void write(PacketInterface pak) throws IOException {
		pak.writeInt(tickID);
		int ct = 0;
		int b = 0;
		for(int i = 0; i < states.length; i ++) {
			if(states[i]) {
				b = b | (0x1 << ct);
			}
			ct++;
			if(ct == 8) {
				pak.write(b);
				b = 0;
				ct = 0;
			}
		}
		if(ct > 0) pak.write(b);
	}
	
	public static InputData read(PacketInterface pak) throws IOException {
		int tickID = pak.readInt();
		InputData indata = poolInputData(tickID);
		int ct = 0;
		int b = pak.read();
		for(int i = 0; i < INPUT_SIZE; i ++) {
			int filter = 0x1 << ct;
			indata.states[i] = (b & filter) == filter;
			ct++;
			if(ct == 8) {
				b = pak.read();
				ct = 0;
			}
		}
		
		return indata;
	}
	
	public static InputData poolInputData(int tickID) {
		InputData indata;
		if(!pool.isEmpty()) {
			indata = pool.pop();
		} else indata = new InputData();
		indata.next = null;
		indata.tickID = tickID;
		return indata;
	}
	
	public static class InputDataList {
		
		private InputData first;
		
		public void push(InputData el) {
			if(el.next != null) throw new RuntimeException("Element in another list");
			el.next = first;
			first = el;
		}
		
		public InputData pop() {
			if(first == null) throw new RuntimeException("Empty list");
			InputData el = first;
			first = first.next;
			return el;
		}
		
		public InputData top() {
			if(first == null) throw new RuntimeException("Empty list");
			return first;
		}
		
		public void append(InputData el) { //TODO opti with keeping track of last
			if(el.next != null) throw new RuntimeException("Element in another list");
			if(first == null) first = el;
			else {
				InputData last = first;
				while(last.next != null) last = last.next;
				last.next = el;
			}
		}
		
		public boolean isEmpty() {
			return first == null;
		}
		
		public int lenght() {
			int l = 0;
			for(InputData el = first; el != null; el = el.next) l ++;
			return l;
		}
	}
}