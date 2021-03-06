package memory;

import java.util.LinkedList;

/**
 * This memory model allocates memory cells based on the best-fit method.
 * 
 * @author "Johan Holmberg, Malmö university"
 * @since 1.0
 */
public class BestFit extends Memory {
	private LinkedList<Segment> freeList = new LinkedList<Segment>();
	private LinkedList<Segment> allocList = new LinkedList<Segment>();
	private int size;
	/**
	 * Initializes an instance of a best fit-based memory.
	 * 
	 * @param size The number of cells.
	 */
	public BestFit(int size) {
		super(size);
		this.size=size;
		freeList.add(new Segment(new Pointer(0,this), size));
	}

	/**
	 * Allocates a number of memory cells. 
	 * 
	 * @param size the number of cells to allocate.
	 * @return The address of the first cell.
	 */
	@Override
	public Pointer alloc(int size) {
		int bestIndex = -1;
		int gap = this.size;
		for(int i = 0; i<freeList.size();i++) {
			if(freeList.get(i).getSize()>=size) {
				if (freeList.get(i).getSize()-size<gap) {
					gap=freeList.get(i).getSize()-size;
					bestIndex = i;
				}
			}
		}
		if(!(bestIndex<0)) {
			Segment temp = freeList.get(bestIndex);
			Segment seg = new Segment(new Pointer(temp.getPointer().pointsAt(), this), size);
			insertSorted(allocList, seg);
			freeList.remove(bestIndex);
			if(temp.getSize()-size!=0) {
				temp.getPointer().pointAt(temp.getPointer().pointsAt()+seg.getSize());
				temp.setSize(temp.getSize()-size);
				insertSorted(freeList, temp);
			}
			return seg.getPointer();
		}
		return null;
	}
	
	/**
	 * Releases a number of data cells
	 * 
	 * @param p The pointer to release.
	 */
	@Override
	public void release(Pointer p) {
		for (int i = 0; i<allocList.size();i++) {
			if (allocList.get(i).getPointer().equals(p)) {
				Segment seg = allocList.remove(i);
				insertSorted(freeList, seg);
				releaseBounded();
			}
		}
	}
	/**
	 * Removes segments located next to each other and puts them together
	 * 
	 */
	public void releaseBounded() {
		Segment segOne, segTwo;
		for (int i = 0; i<freeList.size()-1;i++) {
			segOne = freeList.get(i);
			segTwo = freeList.get(i+1);
			if((segOne.getPointer().pointsAt()+segOne.getSize())==segTwo.getPointer().pointsAt()) {
				freeList.get(i).setSize(segOne.getSize()+segTwo.getSize());
				freeList.remove(i+1);
				i--;
			}
		}
	}
	/**
	 * Inserts the segment int the list at the right location according to the pointer
	 * @param list list to insert into
	 * @param seg segment to insert
	 * @return always true
	 */
	private boolean insertSorted(LinkedList<Segment> list, Segment seg) {
		for(int i=0; i<list.size();i++) {
			if(list.get(i).getPointer().pointsAt()>seg.getPointer().pointsAt()) {
				list.add(i, seg);
				return true;
			}
		}
		list.add(seg);
		return true;
	}
	
	/**
	 * Prints a simple model of the memory. Example:
	 * 
	 * |    0 -  110 | Allocated
	 * |  111 -  150 | Free
	 * |  151 -  999 | Allocated
	 * | 1000 - 1024 | Free
	 */
	@Override
	public void printLayout() {
		System.out.println("");
		//Kollar om tom då bara allocated
		if (freeList.isEmpty()) {
			for(int i=0;i<allocList.size();i++) {
				System.out.println(allocList.get(i).getPointer().pointsAt() + " - " + 
						(allocList.get(i).getPointer().pointsAt()+allocList.get(i).getSize()) + " Allocated");
			}
		} else {
			//första platsen inte free
			if (freeList.getFirst().getPointer().pointsAt()>0) {
				System.out.println("0 - " + freeList.getFirst().getPointer().pointsAt() + " Allocated");
			}
			for(int i=0; i<freeList.size();i++) {
				int size = freeList.get(i).getSize();
				int starts = freeList.get(i).getPointer().pointsAt();
				System.out.println(starts + " - " + (starts + size) + " Free");
				//nästa plats är allokerad
				if (i+1<freeList.size() && size+starts<freeList.get(i+1).getPointer().pointsAt()) {
					System.out.println((starts + size) + " - " + freeList.get(i+1).getPointer().pointsAt() + " Allocated");
					//sista är allokerad
				} else if (i==freeList.size()-1 && starts+size<this.size) {
					System.out.println((starts + size) + " - " + this.size + " Allocated");
				}
			}
		}
	}
}
