class GlobalValue
{
	public static int val = 0;

	public static synchronized void set(int new_val)
	{
		val = new_val;
	}

	public static int get()
	{
		return(val);
	}
}

		