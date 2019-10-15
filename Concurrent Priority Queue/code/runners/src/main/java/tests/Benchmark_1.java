package tests;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.GroupThreads;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import arrayBased.SimpleLeaner;
import common.PQueue;

@State(Scope.Benchmark)
@BenchmarkMode({Mode.Throughput, Mode.SampleTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class Benchmark_1
{
	private static PQueue<Integer> queue;
	
	@Setup
	public void setup()
	{
		queue = new SimpleLeaner<>(100);
		//queue = new LazySkipQueue<>(5);
		//queue = new FineGrainedHeap<>(100);
		//queue = new SimpleTree<>(7);
		//queue = new ConcurrentSkipQueue<>();
	}
	
	@Group("test")
	@GroupThreads(1)
	@Benchmark
	public void addValue() { queue.add(ThreadLocalRandom.current().nextInt(100)); }
	
	@Group("test")
	@GroupThreads(1)
	@Benchmark
	public void removeMin() { queue.removeMin(); }
}
