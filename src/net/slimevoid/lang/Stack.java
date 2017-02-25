package net.slimevoid.lang;

public class Stack<T> {
	
	Elem top;
	
	public boolean isEmpty() {
		return top == null;
	}
	
	public void push(T val) {
		Elem e = new Elem(val);
		e.prev = top;
		top = e;
	}
	
	public T pop() {
		Elem e = top;
		if(top != null) top = top.prev;
		return e.value;
	}
	
	public Stack<T> copy() {
		Stack<T> stack = new Stack<>();
		if(!this.isEmpty()) stack.copyFrom(this);
		return stack;
	}
	
	private void copyFrom(Stack<T> stack) {
		copyElem(stack.top);
	}
	
	private void copyElem(Elem e) {
		if(e.prev != null) copyElem(e.prev);
		push(e.value);
	}
	
	public void clear() {
		top = null;
	}

	private class Elem {
		
		T value;
		Elem prev;
		
		public Elem(T value) {
			this.value = value;
		}
	}
}
