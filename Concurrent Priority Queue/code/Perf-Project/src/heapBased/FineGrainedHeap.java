package heapBased;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import common.PQueue;

/** Blocking Heap Based Priority Queue **/
public class FineGrainedHeap<E> implements PQueue<E>
{
	// Local variables
	private static final int ROOT = 1;
	private static final long NO_ONE = -1;
	private final int capacity;
	private Lock heapLock;
	private int next;
	private HeapNode<E>[] heap;
	
	// C'tor
	public FineGrainedHeap(int capacity)
	{
		this.capacity = capacity;
		heapLock = new ReentrantLock();
		next = ROOT;
		heap = (HeapNode<E>[]) new HeapNode[capacity + 1];
		
		for (int i = 0; i < capacity + 1; i++)
			heap[i] = new HeapNode<E>();
	}
	
	// Private function
	private void swap(int parent, int child)
	{
		HeapNode<E> temp = new HeapNode<>(heap[parent]);
		temp.init(heap[parent].item, heap[parent].score);
		temp.tag = heap[parent].tag;
		
		heap[parent].init(heap[child].item, heap[child].score);
		heap[parent].tag = heap[child].tag;
		
		heap[child].init(temp.item, temp.score);
		heap[child].tag = temp.tag;
	}
	
	// Override functions
	@Override
	public void add(E item)
	{
		heapLock.lock();
		int key = item.hashCode();
		
		if(next > capacity)
		{
			heapLock.unlock();
			return;
		}
		
		int child = next++;
		
		heap[child].lock();
		heap[child].init(item, key);
		heapLock.unlock();
		heap[child].unlock();
		
		while(child > ROOT)
		{
			int parent = child / 2;
			heap[parent].lock();
			heap[child].lock();
			int oldChild = child;

			try
			{
				if (heap[parent].tag == Status.AVAILABLE && heap[child].amOwner())
				{
					if (heap[child].score < heap[parent].score)
					{
						swap(child, parent);
						child = parent;
					}
					
					else
					{
						heap[child].tag = Status.AVAILABLE;
						heap[child].owner = NO_ONE;
						return;
					}
				}
				
				else if (!heap[child].amOwner())
					child = parent;
			}
			
			finally 
			{
				heap[oldChild].unlock();
				heap[parent].unlock();
			}
		}
		
		if(child == ROOT)
		{
			heap[ROOT].lock();
			if (heap[ROOT].amOwner())
			{
				heap[ROOT].tag = Status.AVAILABLE;
				heap[child].owner = NO_ONE;
			}
			
			heap[ROOT].unlock();
		}
	}

	@Override
	public E removeMin()
	{
		heapLock.lock();
		int bottom = --next;
		
		if (bottom < ROOT)
		{
			next++;
			heapLock.unlock();
			return null;
		}
		
		heap[ROOT].lock();
		heap[bottom].lock();
		heapLock.unlock();
		
		E item = heap[ROOT].item;		
		heap[ROOT].tag = Status.EMPTY;
		heap[ROOT].owner = NO_ONE;
		
		swap(bottom, ROOT);
		heap[bottom].unlock();
		
		if (heap[ROOT].tag == Status.EMPTY)
		{
			heap[ROOT].unlock();
			return item;
		}
		
		int child = 0;
		int parent = ROOT;

		while (parent < heap.length / 2)
		{
			int left = parent * 2;
			int right = (parent * 2) + 1;
			
			heap[left].lock();
			heap[right].lock();
			
			if (heap[left].tag == Status.EMPTY)
			{
				heap[right].unlock();
				heap[left].unlock();
				break;
			}
			
			else if (heap[right].tag == Status.EMPTY || heap[left].score < heap[right].score)
			{
				heap[right].unlock();
				child = left;
			}
			
			else
			{
				heap[left].unlock();
				child = right;
			}
			
			if (heap[child].score < heap[parent].score) 
			{
				swap(parent, child);
				heap[parent].unlock();
				parent = child;
			}
			
			else
			{
				heap[child].unlock();
				break;
			}
		}
		
		heap[parent].unlock();
		return item;
	}

	/** enum Status **/
	private static enum Status {EMPTY, AVAILABLE, BUSY};
	
	/** Inner Class - HeapNode<T> **/
	private static class HeapNode<T>
	{
		// Variables
		Status tag;
		Lock lock;
		int score;
		long owner;
		T item;
		
		// C'tors
		public HeapNode()
		{
			tag = Status.EMPTY;
			lock = new ReentrantLock();
		}
		public HeapNode(HeapNode<T> copy)
		{
			tag = copy.tag;
			score = copy.score;
			owner = copy.owner;
			item = copy.item;
		}

		public boolean amOwner()
		{
			return owner == Thread.currentThread().getId();
		}

		public void init(T myItem, int myScore)
		{
			item = myItem;
			score = myScore;
			tag = Status.BUSY;
			owner = Thread.currentThread().getId();
		}
		
		public void lock() {lock.lock();}
		public void unlock() {lock.unlock();}
	}
}
