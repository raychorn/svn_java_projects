/*---------------------------------------------------------------------------------

	Applet: User
	Author: Scott Clark
	Date: 02/11/96
	Version: 1.1Alpha

	Description:

	A applet to greet the user with their login name host and port

	Variables:		Usages:

	fgColor		Foreground color of the applet
	bgColor		Background color of the applet
	text		Message to Display before the user login name

-----------------------------------------------------------------------------------------------*/
import java.awt.*;
import java.applet.*;
import java.awt.image.*;
import java.awt.Color;
import java.net.*;

public class User extends Applet
{
	Color fg;
	Color bg;
	String text;
	String m1;
	private InetAddress myAddress = null;
	private final int xdim =  450;
	private final int ydim = 60;

	/**---------------------------------------------------
	----------------------------------------------------*/
	public Color readColor(String aColor, Color aDefualt)
	{
		if((aColor == null) || (aColor.charAt(0) != '#') || (aColor.length() != 7))
		{
			return aDefualt;
		}
	
		try
		{
			Integer rgbValue = new Integer(0);
			rgbValue = Integer.valueOf(aColor.substring(1,7), 16);
			return new Color(rgbValue.intValue());
		}
		catch (Exception e)
		{
			return aDefualt;
		}
	}

	/**---------------------------------------------------
	----------------------------------------------------*/

	public void init()
	{

		Color fg = readColor(getParameter("fgColor"), Color.black);		//This all here need 
		Color bg = readColor(getParameter("bgColor"), getBackground());	//to be in the init()
		setForeground(fg);							//to initialise it
		setBackground(bg);						
		m1 =  getParameter("text");
		if(m1 == null)							
		{								
			m1 = "Hello";						
		}								

		resize(xdim,ydim);
		
		try
		{
			myAddress = InetAddress.getLocalHost();
		}
		catch (UnknownHostException e)
		{
			System.out.println("Failed to getLocalHost");
			return;
		}
	}

	/**---------------------------------------------------
	----------------------------------------------------*/

	public void paint (Graphics g)
	{
		g.drawString(m1 + "" + myAddress.toString(), 10, 15);
	}
}
