package arrayBased;

import java.util.concurrent.atomic.AtomicReference;

/** Non-Blocking Stack **/
public class LockFreeStack<E>
{
	// Local variable
	private AtomicReference<Node<E>> top;
	
	// C'tor
	public LockFreeStack() { top = new AtomicReference<Node<E>>(null); }
	
	// Public functions
	public void push(E value)
	{
		Node<E> newNode = new Node<E>(value);
		Node<E> headNode = null;
		
		do
		{
			headNode = top.get();
			newNode.next = headNode;
		}while(!top.compareAndSet(headNode, newNode));
	}
	
	public E pop()
	{
		Node<E> headNode = null;

		do
		{
			headNode = top.get();
			
			if(headNode == null)
				return null;
			
		}while(!top.compareAndSet(headNode, headNode.next));
		
		return headNode.value;
	}
	
	/** Inner Class - Node **/
	final class Node<T>
	{
		// Variables
		T value;
		Node<T> next;
		
		// C'tor
		public Node(T value)
		{
			this.value = value;
			this.next = null;
		}
	}
}
