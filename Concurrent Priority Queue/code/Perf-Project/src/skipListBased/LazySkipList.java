package skipListBased;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Based on the algorithm from the article: "A Provably Correct Scalable Concurrent Skip List"
 * https://www.cs.tau.ac.il/~shanir/nir-pubs-web/Papers/OPODIS2006-BA.pdf
 */
public class LazySkipList<E>
{
	// Local Variables
	private final int MAX_LEVEL;
	private final Node<E> head;
	private final Node<E> tail;
	
	// C'tor
	public LazySkipList(int maxLevel)
	{
		MAX_LEVEL = maxLevel;
		head = new Node<>(Integer.MIN_VALUE);
		tail = new Node<>(Integer.MAX_VALUE);
		
		for(int i=0; i<head.next.length; i++)
			head.next[i] = tail;
	}
	
	// Private Functions
	private int findNode(E val, Node<E>[] preds, Node<E>[] succs)
	{
		int lFound = -1;
		int key = val.hashCode();
		Node<E> pred = head;
		
		for(int layer = MAX_LEVEL; layer >= 0; layer--) // go down
		{
			Node<E> curr = pred.next[layer];
			while(key > curr.key)	// go right
			{
				pred = curr;
				curr = pred.next[layer];
			}
			
			if(lFound == -1 && key == curr.key)	// found
				lFound = layer;
			
			preds[layer] = pred;
			succs[layer] = curr;
		}
		
		return lFound;
	}
	private int randomLevel()
	{
		int level = 1;
		while(ThreadLocalRandom.current().nextInt(2) < 0.5 && level < MAX_LEVEL)
			level++;
		
		return level;
		//return ThreadLocalRandom.current().nextInt(MAX_LEVEL);
	}
	
	// Public Functions
	public boolean add(E val)
	{
		int topLayer = randomLevel();
		Node<E>[] preds = (Node<E>[]) new Node[MAX_LEVEL+1];
		Node<E>[] succs = (Node<E>[]) new Node[MAX_LEVEL+1];
		
		while(true)
		{
			int lFound = findNode(val, preds, succs);
			if(lFound != -1)	// value already exists
			{
				Node<E> nodeFound = succs[lFound];
				if (!nodeFound.marked)	// if node is not marked return false, otherwise check again..
				{
					while (!nodeFound.fullyLinked);
					return false;
				}
				
				continue;
			}

			int highestLocked = -1;
			try
			{
				Node<E> pred, succ;
				boolean valid = true;
				
				for(int layer = 0; valid && (layer <= topLayer); layer++)	// lock preds' node
				{
					pred = preds[layer];
					succ = succs[layer];
					
					pred.lock();
					highestLocked = layer;
					valid = !pred.marked && !succ.marked && pred.next[layer]==succ;
				}
				
				if(!valid)	// if lock is failed
					continue;
				
				// Create new Node and insert it to set
				Node<E> newNode = new Node<E>(val, topLayer);
				for (int layer=0; layer <= topLayer; layer++)
				{
					newNode.next[layer] = succs[layer];
					preds[layer].next[layer] = newNode;
				}
				
				newNode.fullyLinked = true; // successful add linearization point
				return true;
			}
			
			finally
			{
				for (int layer = 0; layer <= highestLocked; layer++)
					preds[layer].unlock();
			}
		}
	}
	
	public boolean remove(E val)
	{
		Node<E> victim = null;
		boolean isMarked = false;
		int topLayer = -1;
		
		Node<E>[] preds = (Node<E>[]) new Node[MAX_LEVEL + 1];
		Node<E>[] succs = (Node<E>[]) new Node[MAX_LEVEL + 1];
		
		while(true)
		{
			int lFound = findNode(val, preds, succs);
			if (lFound != -1)
				victim = succs[lFound];

			if (isMarked || (lFound != -1 && (victim.fullyLinked && victim.topLayer == lFound && !victim.marked)))
			{
				if (!isMarked) // if we didn't marked it
				{
					topLayer = victim.topLayer;
					victim.lock();
					
					if (victim.marked)	// node already marked
					{
						victim.unlock();
						return false;
					}
					
					victim.marked = true;
					isMarked = true;
				}
				
				int highestLocked = -1;
				try
				{
					Node<E> pred;
					boolean valid = true;
					
					for (int layer = 0; valid && layer <= topLayer; layer++) // lock preds' node
					{
						pred = preds[layer];
						pred.lock();
						highestLocked = layer;
						
						valid = !pred.marked && pred.next[layer] == victim;
					}
					
					if (!valid)
						continue;
					
					for (int layer = topLayer; layer >= 0; layer--) // delete node
						preds[layer].next[layer] = victim.next[layer];
					
					victim.unlock();
					return true;
				}
				
				finally
				{
					for (int i = 0; i <= highestLocked; i++)
						preds[i].unlock();
				}
			}
			
			else
				return false;
		}
	}
	
	public boolean contains(E val)
	{
		Node<E>[] preds = (Node<E>[]) new Node[MAX_LEVEL + 1];
		Node<E>[] succs = (Node<E>[]) new Node[MAX_LEVEL + 1];
		int lFound = findNode(val, preds, succs);
		return (lFound != -1 && succs[lFound].fullyLinked && !succs[lFound].marked);
	}
		
	// Priority Function
	public E findAndRemoveMin()
	{
		Node<E> curr = head.next[0];
		
		while (curr != tail)
		{
			if (!curr.marked && curr.fullyLinked)
			{
				E val = curr.value;
				if(remove(curr.value))
					return val;
					
				else
					curr = head.next[0];
			}
			
			else
				curr = curr.next[0];
		}
		
		return null;	// list is empty
	}
	
	/** Inner Class - Node<T> **/
	final private class Node<T>
	{
		// Variables
		final T value;
		final int key;
		final int topLayer;
		final Node<T>[] next;
		volatile boolean marked;
		volatile boolean fullyLinked;
		final Lock lock;
		
		// C'tors
		public Node(int key)
		{
			this.value = null;
			this.key = key;
			this.next = (Node<T>[]) new Node[MAX_LEVEL+1];
			this.topLayer = MAX_LEVEL;
			this.lock = new ReentrantLock();
		}
		public Node(T val, int height)
		{
			this.value = val;
			this.key = val.hashCode();
			next = (Node<T>[]) new Node[height + 1];
			this.topLayer = height;
			lock = new ReentrantLock();
		}
		
		public void lock() { lock.lock(); }
		public void unlock() { lock.unlock(); }
	}
}
