package tests;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BenchmarksRunner
{
	public static void main(String[] args) throws RunnerException
	{
        Options opt = new OptionsBuilder()
                .include(Benchmark_1.class.getSimpleName())
                .forks(1)
                .jvmArgsAppend("-Xms2048m")
                .warmupIterations(0)
                .measurementIterations(3)
                .build();

        new Runner(opt).run();
	}
}