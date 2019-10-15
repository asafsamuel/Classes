package skipListBased;

import common.PQueue;

/** Blocking skip list priority queue **/
public class LazySkipQueue<E> implements PQueue<E>
{
	// Local variables
	private LazySkipList<E> lazySkipList;
	
	// C'tor
	public LazySkipQueue(int maxLevels) { lazySkipList = new LazySkipList<>(maxLevels); }
	
	// Override functions
	@Override
	public void add(E item) { lazySkipList.add(item); }

	@Override
	public E removeMin() { return lazySkipList.findAndRemoveMin(); }

	public boolean contains(E value) { return lazySkipList.contains(value); }
}
