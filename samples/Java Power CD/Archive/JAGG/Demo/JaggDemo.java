/*
 * JaggDemo applet.
 * Simple database console application.
 * Serves as a demo of basic JAGG usage.
 */
import java.applet.*;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Enumeration;

public class JaggDemo extends Applet {
	Frame f;

	public void init() {
		setBackground(Color.lightGray);
		setFont(new Font("TimesRoman", Font.PLAIN, 12));
		//setLayout(null);
		f = new JaggConsoleL0();
		f.pack();
		f.reshape(10, 0, 610, 260);
		f.show();
	}

	public boolean handleEvent(Event e) {
		switch (e.id) {
		case Event.WINDOW_DESTROY:
			System.exit(0);
			return true;
		default:
			return false;
		}
	}

}

/*
 * Screen console layout.
 */
class JaggConsoleL0 extends Frame {
	String		SQL = new String("select name,STR(date),quantity,price from jaggdemo");
	Choice		jDSN;
	TextField	jSQL;
	List		jOUTPUT; // Main output area.
	TextField	jSTATUS; // Status output area.
	Jagg		q;
	//
	// Constructor.
	//
	JaggConsoleL0() {
		super("JAGG Demonstration");
		setFont(new Font("TimesRoman", Font.PLAIN, 12));
		Panel mainPanel 	= new Panel();
		Panel centerPanel 	= new Panel();
		Panel rightmainPanel = new Panel();
		Panel rightPanel 	= new Panel();
		Panel leftPanel 	= new Panel();
		Panel botPanel 		= new Panel();
		Panel outPanel		= new Panel();

		// Specify your own server path...
		q = new Jagg("http://www.bulletproof.com/cgi-bin/jagg.exe");
		
		// Set all the main panel layouts.
		setLayout(new BorderLayout());
		mainPanel.setLayout(new BorderLayout());
		centerPanel.setLayout(new GridLayout(3,1,4,4));
		leftPanel.setLayout(new GridLayout(3,1,4,4));
		rightPanel.setLayout(new GridLayout(3,1,4,4));
		outPanel.setLayout(new GridLayout(1,1,4,4));
		botPanel.setLayout(new BorderLayout());

		// TITLE
		Label titlelabel = new Label("JAGG Demonstration Control Panel", Label.LEFT);
		titlelabel.setFont(new Font("TimesRoman",Font.BOLD,14));

		// RIGHT PANEL, buttons and spacing below
		Button butt1 = new Button("Exec SQL");
		Button butt2 = new Button("Help");
		Button butt3 = new Button("Cancel");
		rightPanel.add(butt1);
		rightPanel.add(butt2);
		rightPanel.add(butt3);
		butt1.setFont(new Font("TimesRoman",Font.PLAIN,12));
		butt2.setFont(new Font("TimesRoman",Font.PLAIN,12));
		butt3.setFont(new Font("TimesRoman",Font.PLAIN,12));


		// Row1
		leftPanel.add(new Label("Data Source Name:", Label.LEFT));

		Panel row1Panel = new Panel();
		row1Panel.setLayout(null);

		jDSN = new Choice();
		jDSN.addItem("JAGG DEMO 1");
		jDSN.addItem("JAGG DEMO 2");
		row1Panel.add(jDSN);
		jDSN.reshape(0,5,200,30);
		centerPanel.add(row1Panel);

		// Row2
		leftPanel.add(new Label("Query:", Label.LEFT));

		Panel row2Panel = new Panel();
		row2Panel.setLayout(null);
		jSQL = new TextField(SQL);
		jSQL.setEditable(false);
		jSQL.setBackground(Color.white);
		row2Panel.add(jSQL);
		jSQL.reshape(0,0,400,20);
		centerPanel.add(row2Panel);

		// Row3
		leftPanel.add(new Label("Status:", Label.LEFT));

		Panel row3Panel = new Panel();
		row3Panel.setLayout(null);
		jSTATUS = new TextField("Ready.");
		jSTATUS.setEditable(false);
		jSTATUS.setBackground(Color.white);
		row3Panel.add(jSTATUS);
		jSTATUS.reshape(0,0,400,20);
		centerPanel.add(row3Panel);

		// Bottom panel
		jOUTPUT = new List(6,false);
		jOUTPUT.setBackground(Color.white);
		jOUTPUT.addItem("Ouput area...");
		outPanel.add(jOUTPUT);
		
		// Setup the panel heirarchy.
		rightmainPanel.add("Center", rightPanel);
		mainPanel.add("North", titlelabel);
		mainPanel.add("Center", centerPanel);
		mainPanel.add("West", leftPanel);
		mainPanel.add("East", rightmainPanel);
		botPanel.add("East", new Panel());
		botPanel.add("West", new Panel());
		botPanel.add("Center", outPanel);
		mainPanel.add("South", botPanel);
		
		add("Center",mainPanel);

	}


	public boolean handleEvent(Event evt) {

		if (evt.target instanceof Button ) {
			String choice = (String)evt.arg;
			if (choice.compareTo("Cancel") == 0) {
				dispose();
				return true;
			}
			if (choice.compareTo("Exec SQL") == 0) {
				SQL = jSQL.getText();
				Vector results = new Vector();
				int records;

				consoleStatus("Executing query...");
				consoleClear();

				q.setDSN((String)jDSN.getSelectedItem());
				
				records = q.execSQL(SQL, results);
				if( records == -1 ) {
					consoleOutput(q.getError());
					consoleStatus("Error...");
					return true;
				}

				consoleStatus("Found "+records+" records.");

				//
				// For the example, we will load the columns into arrays of appropriate type.
				//
				String name[]		= new String[records];
				String date[]		= new String[records];
				int quantity[]		= new int[records];
				float price[]		= new float[records];
				String row			= new String();
				int i				= 0;
				String sep			= new String(q.getSEP());
				
				for (Enumeration e = results.elements(); e.hasMoreElements(); i++) {
					row = (String)e.nextElement();

					StringTokenizer st = new StringTokenizer(row);
					name[i]		= st.nextToken(sep);
					date[i]		= st.nextToken(sep);
					quantity[i] = Float.valueOf((String)st.nextToken(sep)).intValue();
					price[i]	= Float.valueOf((String)st.nextToken(sep)).floatValue();

					consoleOutput("Order on "+date[i]+": "+quantity[i]+" of "+name[i]+" at $"+price[i]);
					st = null;
				}

				return true;
			}

			if (choice.compareTo("Help") == 0) {
				DispTextWindow dspmsg1 = new DispTextWindow(1, "JAGG Demonstration Help");
				consoleStatus("Show HELP.");
				return true;
			}
		}

		// Change the query to match the selected Data Source
		if (evt.target instanceof Choice) {
			if (jDSN.getSelectedIndex() == 0) {
				SQL = "select name,STR(date),quantity,price from jaggdemo";
			} else {
				SQL = "select name,STR(date),quantity,price from jaggdemo.csv";
			}
			jSQL.setText(SQL);
			return true;
		}

		// Handle a click in the consoleOutput List?
		if (evt.target instanceof List) {
			return true;
		}
		
		if (evt.id == Event.WINDOW_DESTROY) {
			dispose();
		} 
		return false;
	}

	/**
	 * Output string to the console's main output area.
	 */
	public void consoleOutput(String s) {
		jOUTPUT.addItem(s);
	}

	/**
	 * Output string to the console's status output area.
	 */
	public void consoleStatus(String s) {
		jSTATUS.setText(s);
	}

	/**
	 * Clear the console's main output area.
	 */
	public void consoleClear() {
		// clear() works under the AppletViewer only.  Not under Netscape 2.0b5.
		// Hence we workaround this with delItems().
		jOUTPUT.delItems(0, jOUTPUT.countItems() -1);
		jOUTPUT.clear();
	}

}

/*
 * Help display window.
 */
class DispTextWindow extends Frame {

	int msgindex;

	DispTextWindow(int msgindex, String msgHeader) {
		super(msgHeader);

		this.msgindex = msgindex;
		Panel centerPanel = new Panel();
		Panel buttonPanel = new Panel();
		Panel buttonmainPanel = new Panel();
		setLayout(new BorderLayout());
		centerPanel.setLayout(new GridLayout(1,1,20,20));
		buttonPanel.setLayout(new GridLayout(11,1,8,2));
		add("Center", centerPanel);
		add("North", new Panel());
		add("South", new Panel());
		add("West", new Panel());
		buttonmainPanel.add(buttonPanel);
		add("East", buttonmainPanel);

		List msgtext = new List(13, false);
		msgtext.setForeground(Color.black);
		msgtext.setBackground(Color.white);

		int bcount=0;

		if (msgindex == 1) {
			buttonPanel.add(new Button("     OK     "));
			bcount++;
			msgtext.addItem("JAGG Demonstration Quick Help");
			msgtext.addItem("");
			msgtext.addItem("The main function of this demonstration is for you to see a basic");
			msgtext.addItem("JAGG query in action in the context of a Java applet.");
			msgtext.addItem("");
			msgtext.addItem("The simple data in the demo database is retrieved and pulled into");
			msgtext.addItem("appropriate Java objects, such as Float and Date.");
			msgtext.addItem("");
			msgtext.addItem("The data source 'JAGG DEMO 1' is a dbf table and 'JAGG DEMO 2' is");
			msgtext.addItem("a csv (comma separated variable) text file. The data source could");
			msgtext.addItem("be any ODBC data source.");
			msgtext.addItem("");
			msgtext.addItem("Simply hit the Exec SQL button to run the query, then check the");
			msgtext.addItem("source code JaggDemo.java to see how this works.");
			while (bcount++ < 7) {
				buttonPanel.add(new Label("",Label.LEFT));
			}
			centerPanel.add(msgtext);
			msgtext.reshape(0,0,450,200);
			pack();
			reshape(10, 10, 580, 280);
			show();
		}
    }

    public boolean handleEvent(Event evt) {
		String choice = (String)evt.arg;
        if (evt.target instanceof Button) {
			if (choice.compareTo("     OK     ") == 0) {
				dispose();
				return true;
			}
		}
 		if (evt.id == Event.WINDOW_DESTROY) {
			dispose();
		    return true;
		} 
		return false;
    }

}
