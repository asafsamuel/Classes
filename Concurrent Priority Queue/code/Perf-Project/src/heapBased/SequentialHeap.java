package heapBased;

import common.PQueue;

/** Sequence Heap implementation **/
public class SequentialHeap<E> implements PQueue<E>
{
	// Local Variables
	private static final int ROOT = 1;
	private int next;
	private final int capacity;
	private HeapNode<E>[] heap;
	
	// C'tor
	public SequentialHeap(int capacity)
	{
		next = ROOT;
		this.capacity = capacity;
		heap = (HeapNode<E>[]) new HeapNode[capacity + 1];
		for(int i = 0; i < capacity + 1; i++)
			heap[i] = new HeapNode<E>();
	}
	
	// Private function
	private void swap(int parent, int child)
	{
		HeapNode<E> temp = new HeapNode<>();
		temp.init(heap[parent].item, heap[parent].score);
		
		heap[parent].init(heap[child].item, heap[child].score);
		heap[child].init(temp.item, temp.score);
	}
	
	// Override functions
	@Override
	public void add(E item)
	{
		int score = item.hashCode();
		
		if(next > capacity)
			return;
		
		int child = next++;
		heap[child].init(item, score);
		
		while(child > ROOT)
		{
			int parent = child / 2;
			if (heap[child].score < heap[parent].score)
			{
				swap(child, parent);
				child = parent;
			}
			
			else
				return;		
		}
	}

	@Override
	public E removeMin()
	{
		int bottom = --next;
		E item =  heap[ROOT].item;
		heap[ROOT] = heap[bottom];
				
		if (bottom < ROOT)
		{
			next++;
			return null;
		}
		
		int child = 0;
		int parent = ROOT;
		while (parent < heap.length / 2)
		{
			int left = parent * 2;
			int right = (parent * 2) + 1;
			
			if (left >= next)
				return item;
			
			else if (right >= next || heap[left].score < heap[right].score)
				child = left;
			
			else
				child = right;
			
			if (heap[child].score < heap[parent].score)
			{
				swap(parent, child);
				parent = child;
			}
			
			else
				return item;
		}
		
		return item;
	}

	/** Inner Class - HeapNode<T> **/
	private final class HeapNode<T>
	{
		// Variables
		int score;
		T item;

		public void init(T item, int score)
		{
			this.score = score;
			this.item = item;
		}
	}
}
