package constance;

public class ConstantFold {

	public static void main(String[] args) 
	{
		int x = 100;
		int y = x+x*x;
		double z = y/x;
		
		System.out.println(z);
	}
}
