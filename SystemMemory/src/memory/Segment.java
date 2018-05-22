package memory;

public class Segment {
	private Pointer pointer;
	private int size;
	
	public Segment(Pointer pointer, int size) {
		this.pointer = pointer;
		this.size = size;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public Pointer getPointer() {
		return pointer;
	}
}
