package org.sample;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class Run 
{
	public static void main(String[] args) throws RunnerException 
	{
		Options opts = new OptionsBuilder()
				.measurementIterations(15)
				.warmupIterations(10)
				.threads(1)
				.jvmArgs("-Xms1g", "-Xmx1g", "-Xmn800m")
				.shouldDoGC(true)
				.include(MulBenchmark.class.getSimpleName())	// Place your benchmark here..
				.build();
		
		new Runner(opts).run();
	}
}