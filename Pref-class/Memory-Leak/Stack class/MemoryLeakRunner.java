package iaf.perf.course.day4;

import java.io.IOException;

public class MemoryLeakRunner 
{
	public static void main(String[] args) 
	{
		final int numStacks = 100;
		final int numElementsPerStack = 10_000_000;
		Stack<Integer>[] stacks = (Stack<Integer>[]) new Stack[numStacks];
		
		for(int i=0; i<numStacks; i++)
			stacks[i] = new Stack<Integer>(1000);
		
		for(int i=0; i<numStacks; i++)
		{
			for(int j=0; j<numElementsPerStack; j++)
				stacks[i].put(j);
					
			System.out.println("finished putting " +stacks[i].size() + " elements in stack #"+i);
			
			for(int j=0; j<numElementsPerStack; j++)
				stacks[i].pop();
			
			System.out.println("Stack #"+i+" size after popping "+stacks[i].size());
		}
		
		/*
		 * finished putting 10000001 elements in stack #0
		   Stack #0 size after popping 1
		 * finished putting 10000001 elements in stack #1
		   Stack #1 size after popping 1
		 * finished putting 10000001 elements in stack #2
		   Stack #2 size after popping 1
		 * finished putting 10000001 elements in stack #3
		   Stack #3 size after popping 1
		 * finished putting 10000001 elements in stack #4
		   Stack #4 size after popping 1
		 * finished putting 10000001 elements in stack #5
		   Stack #5 size after popping 1
		 * finished putting 10000001 elements in stack #6
		   Stack #6 size after popping 1
		 * finished putting 10000001 elements in stack #7
		   Stack #7 size after popping 1
		 * finished putting 10000001 elements in stack #8
		   Stack #8 size after popping 1
		 * finished putting 10000001 elements in stack #9
		   Stack #9 size after popping 1
		 * finished putting 10000001 elements in stack #10
		   Stack #10 size after popping 1
		 * finished putting 10000001 elements in stack #11
		   Stack #11 size after popping 1
		 * finished putting 10000001 elements in stack #12
		   Stack #12 size after popping 1
		 * finished putting 10000001 elements in stack #13
		   Stack #13 size after popping 1
		 
		 * Exception in thread "main" java.lang.OutOfMemoryError: GC overhead limit exceeded
		 * at java.lang.Integer.valueOf(Unknown Source)
		 * at iaf.perf.course.day4.Stack.main(Stack.java:56)
		 */
		
		/*** Active HeapDump here (if you want):
		 * 1) open cmd
		 * 2) write "jcmd"
		 * 3) look for our process's id
		 * 4) write "jcmd [id] help"
		 * 5) write "jcmd [id] GC.heap_dump [output_file.hprof]
		 * 6) open output_file.hprof via Java VirtualVM
		***/
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Done!");
	}
}
