package skipListBased;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

import common.PQueue;

/** Free lock skip list priority queue **/
public class ConcurrentSkipQueue<E> implements PQueue<E>
{
	// Local variables
	private ConcurrentSkipListMap<E, Boolean> skipMap;
	
	// C'tor
	public ConcurrentSkipQueue() { skipMap = new ConcurrentSkipListMap<>(); }
	
	// Override functions
	@Override
	public void add(E item) { skipMap.putIfAbsent(item, true); }

	@Override
	public E removeMin()
	{
		Entry<E,Boolean> e =  skipMap.pollFirstEntry();
		return (e == null) ? null : e.getKey();
	}

	public boolean contains(E value) { return skipMap.containsKey(value); }

	public int size() { return skipMap.size(); }
}
