public class  SimpleFile {
	public static final char    separatorChar = '>';
	protected           String  path;
	protected           int     fd;

	public  SimpleFile(String s) {
		path = s;
	}	

	public String  getFileName() {
		int  index = path.lastIndexOf(separatorChar);

		return (index < 0) ? path : path.substring(index + 1);
	}

	public String  getPath() {
		return path;
	}

	public native boolean  open();
	public native void     close();
	public native int      read(byte[]  buffer, int  length);
	public native int      write(byte[]  buffer, int  length);

	static {
		System.loadLibrary("simple");  // runs when class first loaded
	}
}
