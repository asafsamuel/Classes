package collatz_conjecture;

/** 
 * a(0) = n
 * a(m) = a(m-1)/2, if a(m-1) is even
 * a(m) = 3*a(m-1)+1 if a(m-1) is odd
 */
public final class NoRecursionCalculator implements Calculator 
{
	@Override
	public boolean calculate(long start) 
	{
		int steps = 0;
		long next = start;
		
		while (steps++ < 100_000) 
		{
			if (next == 1)
				return true;
			
			next = (next&1) == 1 ? 3*next+1 : next >> 1;
		}
		
		return false;
	}
}
