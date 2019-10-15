package treeBased;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import arrayBased.LockFreeStack;
import common.PQueue;

/** Non-blocking based tree priority queue
 * based on the book: The art of multiprocessor programming */
public class SimpleTree<E> implements PQueue<E>
{
	// Local variables
	private final TreeNode<E> root;
	private final List<TreeNode<E>> leaves;
	private final int range;
	
	// C'tor
	public SimpleTree(int logRange)
	{
		range = (1 << logRange);
		leaves = new ArrayList<TreeNode<E>>(range);
		root = buildTree(logRange, range);
	}
	
	// Private function
	private TreeNode<E> buildTree(int logRange, int range)
	{
		List<TreeNode<E>> layer = leaves;
		
		for(int i=0; i<range; i++)
			leaves.add(new TreeNode<>());
		
		for(int i=0; i<logRange; i++)
		{
			List<TreeNode<E>> list = new ArrayList<>();
			for(int j=0; j<layer.size(); j+=2)
			{
				TreeNode<E> temp = new TreeNode<>();
				temp.left = layer.get(j);
				temp.right = layer.get(j+1);
				layer.get(j).parent = temp;
				layer.get(j+1).parent = temp;
				list.add(temp);
			}
			layer = list;
		}
		
		return layer.get(0);
	}
	
	// Public functions
	@Override
	public void add(E item)
	{
		TreeNode<E> node = leaves.get(item.hashCode());
		node.bin.push(item);
		
		while(node != root)
		{
			TreeNode<E> parent = node.parent;
			if (node == parent.left)
				parent.counter.getAndIncrement();
		
			node = parent;
		}
	}

	@Override
	public E removeMin()
	{
		TreeNode<E> node = root;
		while(!node.isLeaf())
		{
			if(node.counter.get() == 0)
				node = node.right;
			
			else if (node.counter.getAndDecrement() > 0)
				node = node.left;
			
			else
			{
				node.counter.incrementAndGet();
				node = node.right;
			}
		}
			
		return node.bin.pop();
	}
	
	/** Inner Class - TreeNode **/
	public class TreeNode<T>
	{
		// Variables
		final AtomicInteger counter;
		TreeNode<T> parent, right, left;
		final LockFreeStack<T> bin;
		
		// C'tor
		public TreeNode()
		{
			counter = new AtomicInteger(0);
			parent = right = left = null;
			bin = new LockFreeStack<>();
		}
		
		public boolean isLeaf() { return right == null; }
	}
}
