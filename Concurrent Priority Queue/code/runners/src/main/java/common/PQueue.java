package common;

/** Priority queue interface **/
public interface PQueue<T>
{
	void add(T item);
	T removeMin();
}