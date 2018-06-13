package iaf.perf.course.day4;

import java.util.Optional;

/***
 * The problem is in function "Pop".
 * We don't really delete the first value in stack, just only decrease our index.
 * To solve it we will save the first value in a temporary value and put Null in the stack.
 * Check solution below...
 */
public class StackSolution<V> 
{
	// Local Variables
	private int index;
	private V[] data;
	
	// C'tor
	@SuppressWarnings("unchecked")
	public StackSolution(int size) 
	{
		int power2size = 1 << (32 - Integer.numberOfLeadingZeros(size - 1));
		data = (V[]) new Object[power2size];
	}
	
	// Pop value from stack
	public Optional<V> pop() 
	{
		/*** return Optional.ofNullable(data[index--]); ***
		 ***		Solution for memory-leak:			***/
		Optional<V> result = Optional.ofNullable(this.data[index]);
		data[index] = null;
		index--;
		return result;
	}
	
	// Put value in stack
	public void put(V v) 
	{
		ensureCapacity();
		data[index++] = v;
	}

	// Resize array if needed
	@SuppressWarnings("unchecked")
	private void ensureCapacity() 
	{
		if (index == data.length) 
		{
			int newSize = 2 * data.length; 
			Object[] resized = new Object[newSize];
			System.arraycopy(data, 0, resized, 0, data.length);
			data = (V[]) resized;
		}
	}
	
	// Returns stack's size
	public int size() 
	{
		return index + 1;
	}
}
