package collatz_conjecture; 

public class CalculatorBenchmark 
{
	public static void main(String[] args)
	{
		CalculatorBenchmark bench = new CalculatorBenchmark();
		bench.measure(new NaiveCalculator());
		bench.measure(new BetterCalculator());
		bench.measure(new NoRecursionCalculator());
		bench.measure(new Java8Calculator());
		System.out.println("Done!");
	}
	
	public void measure(Calculator calc) 
	{
		System.gc();
		warmup(calc);
		System.gc();
		benchmark(calc);
	}
	
	public void warmup(Calculator calc) 
	{
		long st = System.nanoTime();
		for (int i = 2; i < 1_000_000; i++) 
		{
			boolean result = calc.calculate(i);
			if(!result) 
				System.out.println("false: " + i);
		}
		long en = System.nanoTime();
		
		System.out.println("Warmup time: " + (en-st)/1E6+"ms");
	}
	
	public void benchmark(Calculator calc) 
	{
		long st = System.nanoTime();
		for (int i = 2; i < 10_000_000; i++) 
		{
			boolean result = calc.calculate(i);
			if(!result) 
				System.out.println("false: "+ i);
		}
		long en = System.nanoTime();
		
		System.out.println("Time: " + (en-st)/1E6+"ms");
	}
}

