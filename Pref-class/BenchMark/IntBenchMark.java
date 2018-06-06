package org.sample;

import java.util.concurrent.ThreadLocalRandom;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class IntBenchMark 
{
	static int x;
	static volatile int y;
	
	@Setup(Level.Iteration)
	public void setup() 
	{
		x = ThreadLocalRandom.current().nextInt();
		y = ThreadLocalRandom.current().nextInt();
	}
	
	@Benchmark
	public void volatileFunc(Blackhole b) 
    {
		Blackhole.consumeCPU(100);
		y = ThreadLocalRandom.current().nextInt();
    }
	
	@Benchmark
	public void regularFunc(Blackhole b) 
    {
		Blackhole.consumeCPU(100);
		x = ThreadLocalRandom.current().nextInt();
    }

	public int readVolatile(Blackhole b)
	{
		Blackhole.consumeCPU(200);
		return y;
	}
	
	public int readRegular(Blackhole b)
	{
		Blackhole.consumeCPU(200);
		return x;
	}
}
