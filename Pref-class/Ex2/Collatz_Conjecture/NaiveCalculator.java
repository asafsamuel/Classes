package collatz_conjecture;

/** 
 * a(0) = n
 * a(m) = a(m-1)/2, if a(m-1) is even
 * a(m) = 3*a(m-1)+1 if a(m-1) is odd
 */
public final class NaiveCalculator implements Calculator
{
	@Override
	public boolean calculate(long start) 
	{
		if (start == 1)
			return true;
		
		long next = (start % 2 == 1) ? 3*start+1 : start/2;
		return calculate(next);
	}
}
