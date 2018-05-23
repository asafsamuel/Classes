package collatz_conjecture;

import java.util.stream.LongStream;

/** 
 * a(0) = n
 * a(m) = a(m-1)/2, if a(m-1) is even
 * a(m) = 3*a(m-1)+1 if a(m-1) is odd
 */
public final class Java8Calculator implements Calculator 
{
	@Override
	public boolean calculate(long start) 
	{
		return LongStream.iterate(start, val -> (val & 1) == 1 ? 3*val+1 : val >> 1).anyMatch(val -> val == 1);
	}
}