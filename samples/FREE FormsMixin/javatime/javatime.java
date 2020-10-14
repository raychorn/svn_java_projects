import java.io.*;
import java.awt.*;    
import java.net.*;     
import java.util.Date;
import java.util.StringTokenizer;
import controls.*;
/**		
 *	Country Code Prefix Applet
 *	(c) 1996 Inductive Solutions, Inc.
*/ 
public class javatime  extends java.applet.Applet  { 
	FileMixin	urlFile = null;
	FormMixin 	myInputBox = null;

	Date	td = new Date();
	int	second =	td.getSeconds();
	int	minute =  	td.getMinutes();
	int	hour 	= 	td.getHours();
	int	offset	= 	td.getTimezoneOffset()/60;

	int	databaseSize;
	String	str = null;
	String	myDatabase[] = new String[200];		
	int 	index = 0;
	int 	clearIndicator = 0;

  	public void init() {
	
		addMyDialog();
		BuildDatabase();
  	} 

//	The component is added and the start method is called
    	public void start() {
		
		System.out.println("hour: "+ hour +" minute: "+minute + " second: " +second + " offset: " +offset);	
    	}

	public void BuildDatabase() {
	// Connect to the File
		try {
		urlFile = new FileMixin(this,
					new URL(getDocumentBase(), 
					"javatime.txt"), 
					false);

		} 	catch ( IOException e ) {
			showStatus("Open error " + e);
			return;	// since start does not return a value
		}
	// Build the Database
	    	while (true)	{
			try {
			myDatabase[index] = new String(urlFile.readStringLine());
			} catch ( EndOfFile e ) {
				System.out.println("End of file while reading input");
				break;
			} catch ( IOException e ) {
				System.out.println("IO Error while reading input");
				break;
			}
			StringTokenizer st = new StringTokenizer(myDatabase[index]);
			while (st.hasMoreTokens() )
				System.out.println("index: " + index + st.nextToken() );
			index++;
	    	} // end while(true)
	    	databaseSize = index;
	} // end BuildDatabase
	
//	The stop method removes the component and calls stop
	public void stop() {
		myInputBox.stop();

	}



//	Match method assigns string index
	public int matchToDatabase(String str) {
		int	i = -1;
		for (i = 0; i < index; i++) {
			StringTokenizer st = new StringTokenizer(myDatabase[i], "\t\n\r");
			while (st.hasMoreTokens() )  {
				if (st.nextToken().regionMatches(true,	// ignore case
								0,	// start looking at...
								str,	// the string to compare
								0,	// start looking at...
								str.length()	// how many characters
								)
					)
					return(i);
			}// end while
		} // end for
		return(-1);
	} // end method



//	Tell the Time
	public void TellTime() {
		td 	= 	new Date();
		second =	td.getSeconds();
		minute =  	td.getMinutes();
		hour 	= 	td.getHours();
		
		//myAnswers.restoreToOriginal();
		//myAnswers.getDrawable().drawString("                                             ", 0,20);
		//myAnswers.getDrawable().setColor(myAnswers.setBackgroundColor(this) );	
		//myAnswers.getDrawable().fillRect(0,0,400,20);

		//myAnswers.getDrawable().drawString("Local Time is"+ hour +":"+minute + ":" +second, 0,20);
	} // end TellTime

//	Paint
    	public void paint(Graphics myGraphics) {
		int	key = -1;
		String	mystr;
		if (clearIndicator == 0) { // Clear
			myGraphics.setColor( this.getBackground() );	
			myGraphics.fillRect(0,220,400,60);
			return;
		}
		if (clearIndicator == 2) { // Help
			myGraphics.setColor( this.getBackground() );	
			myGraphics.fillRect(0,220,400,100);
			myGraphics.setColor(Color.black);	
			myGraphics.setFont(new Font("TimesRoman", Font.BOLD, 16));
			myGraphics.drawString("Enter a name in the input box.", 0,220);
			myGraphics.drawString("I will tell you its prefix and local time." , 0,240);
			return;
		}

		//myGraphics.drawString("Local Time is"+ hour +":"+minute + ":" +second, 0,220);
		myGraphics.setColor(Color.black);	
		myGraphics.setFont(new Font("TimesRoman", Font.BOLD, 16));
		myGraphics.drawString("It is now "+td.toString(), 0,220);
		if (str.length() < 4 ) {
			myGraphics.drawString("I need more letters to match on! Please try again.", 0,240);
			return;
		}

		key =  matchToDatabase(str);
		if ( key < 0 )
			myGraphics.drawString("I couldn't find any that matched.  Please try again.", 0,240);
		else {	
			StringTokenizer st = new StringTokenizer(myDatabase[key], "\t\n\r");
			int tokenCount = st.countTokens();	// if 3 => NO city; else City
			String countryStr = new String(st.nextToken());
			String codeStr = new String(st.nextToken());
			String timediffStr = new String(st.nextToken());

			int myOffset = offset - 5 + Integer.parseInt(timediffStr);
			int  myHour = (myOffset + hour);
			if (minute > 30) myHour++;
			String  tomorrow;
			if ( myHour > 24 ) {
				tomorrow = new String("tomorrow"); 
				myHour = myHour - 24;
			}
			else 
				tomorrow = new String("today");

			myGraphics.drawString("The telephone prefix for "+countryStr+" is "+codeStr+".", 0,240);
			myGraphics.drawString("The time difference betweeen here and there is "+myOffset+".", 0,260);
			if (myHour >0)
				if (myHour > 12)
					myGraphics.drawString("There, it is approximately "+(myHour-12)+"o'clock PM " +tomorrow , 0,280);
				else
					myGraphics.drawString("There, it is approximately "+myHour+"o'clock AM " +tomorrow, 0,280);
			else
				if (myHour <-12 )
					myGraphics.drawString("There, it is approximately "+(12+myHour)+"o'clock PM "+tomorrow, 0,280);
				else
					myGraphics.drawString("There, it is approximately "+(24+myHour)+"o'clock AM "+tomorrow, 0,280);

			if (tokenCount > 3) {
				String cityStr = new String(st.nextToken());
				String areaStr = new String(st.nextToken());
				myGraphics.drawString(cityStr +",the city, has area code "+areaStr+".", 0,300);
			}
		}
		// Since it seems to keep repainting, comment out!  And Comment out repaint in action!
		//clearIndicator = 0;
    	}

	
//  Moving the mouse over the applet shows authorship.
	public boolean mouseEnter(java.awt.Event evt, int x, int y) { 
		showStatus("(c) 1996 Inductive Solutions, Inc.");
		TellTime();
		return (true);
  	}

//	This causes the Dialog to appear 	
	public void addMyDialog() { 
      	// Construction		
		myInputBox = new FormMixin(this, 450, 180); // alert or login dont matter; other 
		myInputBox.setAllButtonNames("Do It","Clear","Help");
		myInputBox.setShapeColor(Color.gray);
	        myInputBox.setDialogLayout(1, "Helvetica",Font.PLAIN , 16 , Color.lightGray);	// only for input
					//columns, font family, style, size, color 
		myInputBox.setInput("Enter Country or City","",myInputBox.NORMAL, Label.LEFT,18);
		myInputBox.setMessage(
			"Country Telephone Codes",	// Msg
			new Point (10,40),		// Position
			"TimesRoman",			// font family
			Font.BOLD,			// style
			14,				// size
			Color.red);			// color - blinks alternate with black	
	// Add It
      		myInputBox.CreateUserInput(""); 
                add(myInputBox);
		validate();
	}


        public boolean action(Event evt, Object arg) {
		System.out.println(evt);
		
            	if ("Do It".equals(arg)) {
              		System.out.println("OK button pressed");
			str = new String( myInputBox.getResponse(0) );
			System.out.println("Input is " + str);
			//myAnswers.restoreToOriginal();
			//myAnswers.getDrawable().drawString("                       ", 0,40);
			//myAnswers.getDrawable().drawString(myInputBox.getResponse(0), 0,40);
			TellTime();
			clearIndicator = 1;
			repaint(0);

               	} else if ("Clear".equals(arg)) {
      		   //myInputBox.setText("");
		   //myAnswers.restoreToOriginal();
			clearIndicator = 0;
			System.out.println("Cancel button pressed" + clearIndicator);
			repaint(0);

               	} else if ("Help".equals(arg)) {
                  System.out.println("Help button pressed");
		  //myAnswers.restoreToOriginal();
			clearIndicator = 2;
			repaint(0);
                }
                return ( true );
        }
}
