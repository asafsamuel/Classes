package iaf.perf.course.day3.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import iaf.perf.course.day3.map.Ex1.TimedSizableMap;

public class TimeSizableMap<K,V> implements TimedSizableMap<K, V>
{
	// Local Variables
	private AtomicInteger count;
	private Map<K,StampedObject<V>> map;
	private ScheduledExecutorService service;
	private Map<K,Object> lockMap;	// we don't wont to lock outside objects, therefore we will create an Object
									// for each Key and lock the new Object
	
	// C'tor
	public TimeSizableMap() 
	{
		count = new AtomicInteger(1);
		map = new HashMap<>();
		service = Executors.newScheduledThreadPool(100);
		lockMap = new HashMap<>();
	}
	
	// Functions
	@Override
	public void put(K key, V value, int duration, TimeUnit unit) 
	{
		int mark = count.incrementAndGet();
		
		synchronized(lockMap.get(key))
		{
			map.put(key, new StampedObject<V>(value, mark));
			service.schedule(()->tryRemove(key,mark), duration, unit);
		}
	}

	private void tryRemove(K key, int mark) 
	{
		synchronized(lockMap.get(key))
		{
			if (markEquals(key, mark)) 
			{
				map.remove(key);
				lockMap.remove(key);
			}
		}
	}

	private boolean markEquals(K key, long mark)
	{
		return Optional.ofNullable(map.get(key)).map(stamped -> stamped.stamp == mark).orElse(false);
	}
	

	@Override
	public Optional<V> get(K key) 
	{
		return Optional.ofNullable(map.get(key).obj);
	}

	@Override
	public Optional<V> remove(K key) 
	{
		lockMap.remove(key);
		return Optional.ofNullable(map.remove(key).obj);
	}

	@Override
	public long size() 
	{
		return map.size();
	}

	// Class StampedObject
	private static final class StampedObject<T> 
	{
		// Local variables
		final T obj;
		final long stamp;
		
		// C'tor
		private StampedObject(T obj, long stamp) 
		{
			this.obj = obj; 
			this.stamp = stamp;
		}
		
		// Generic function <R> called of and get R and long
		public static <R> StampedObject<R> of(R obj, long stamp)
		{
			return new StampedObject<R>(obj, stamp);
		}
	}
}
