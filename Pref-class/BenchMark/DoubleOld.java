package org.sample;

/*
 * Problems:
 * 	- Watch up for java optimization (for...)
 *  - Math Random take really big time in our calculation
 *  - We in the end check Random and not regular
 *  - Writing to volatile is not measurable if we don't read from it...
*/

public class DoubleOld 
{
	// Variables
	final static int warmTime = (int) Math.pow(2, 16);
	final static double barnchTime = 1E8;
	
	static double reg = 1;
	static volatile double vol = 2;
	static double[] values = new double[warmTime];
	
	// Run functions
	public static void main(String[] args) 
	{
		warmUp();
		System.gc();
		branchMark();
	}
	
	public static void branchMark()
	{
		long time1 = System.nanoTime();
		for(int i=0; i<barnchTime; i++)
			reg = values[i & (1 << 15)];	// values[i & warmTime]
		long time2 = System.nanoTime();
		
		System.gc();
		
		long time3 = System.nanoTime();
		for(int i=0; i<barnchTime; i++)
			vol = values[i & (1 << 15)];	// values[i & warmTime]
		long time4 = System.nanoTime();
		
		System.out.println("Simple time: "+(time2-time1)/1E6 +" ms");
		System.out.println("Volatile time: "+(time4-time3)/1E6 +" ms");
	}
	
	public static void warmUp()
	{
		long time1 = System.nanoTime();
		for(int i=0; i<warmTime; i++)
			values[i] =  Math.random();
		long time2 = System.nanoTime();
		
		System.out.println("warm-up time: "+(time2-time1)/1E6 +" ms");
	}
}
