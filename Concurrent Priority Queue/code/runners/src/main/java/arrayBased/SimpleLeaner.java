package arrayBased;

import common.PQueue;

/** Non-blocking based array bounded priority queue **/
public class SimpleLeaner<E> implements PQueue<E>
{
	// Local variable
	final LockFreeStack<E>[] array; //final ConcurrentLinkedDeque<E>[] array;
	
	// C'tor
	public SimpleLeaner(int myRange)
	{
		array = (LockFreeStack<E>[]) new LockFreeStack[myRange]; //array = (ConcurrentLinkedDeque<E>[]) new ConcurrentLinkedDeque[myRange];
		
		for(int i=0; i<array.length; i++)
			array[i] = new LockFreeStack<E>();
	}
	
	@Override
	public void add(E item) { array[item.hashCode()].push(item); }

	@Override
	public E removeMin()
	{
		for(int i=0; i<array.length; i++)
		{
			E item = array[i].pop();
			if(item != null)
				return item;
		}
		
		return null;
	}
}
