package iaf.perf.course.day4;

import java.io.IOException;

public class MemoryLeakRunner 
{
	public static void main(String[] args) 
	{
		final double iterNum = 1E7;	// change it depends on your computer
		final int initSize = 100;
		
		Stack<Object> stack = new Stack<>(initSize);
		Stack<Object> stack2 = new Stack<>(initSize);
		
		for(int i=0; i<iterNum; i++)
		{
			stack.put(new Object());
			stack2.put(new Object());
		}
		
		for(int i=0; i<iterNum; i++)
		{
			stack.pop();
			stack2.pop();
		}
		
		/*** Active HeapDump here (if you want):
		 * 1) open cmd
		 * 2) write "jcmd"
		 * 3) look for our process's id
		 * 4) write "jcmd [id] help"
		 * 5) write "jcmd [id] GC.heap_dump [output_file.hprof]
		 * 6) open output_file.hprof
		***/
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Done!");
	}
}
