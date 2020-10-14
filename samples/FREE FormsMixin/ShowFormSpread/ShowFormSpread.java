import controls.*;
import java.applet.Applet;
import java.awt.*;
import java.net.*;
/**
 *	Speadsheet functions and Input Validation with the FormMixin
	(c) 1996 Inductive Solutions, Inc.
 *
 */
public class ShowFormSpread extends Applet {
	FormMixin 	form;
	
	// Display fields
	TextField	A3;
	TextField	A4;
	TextField	A5;
	TextField	A6;
	// For Validation
	int 		fieldStatus[] = new int[8];	
	int 		allStatus;
	double		myMinMax[][] = new double[6][2];
	String Ids[] = {"Number of Widgets","Widget Price"};
	

	public void init () {
		form = new FormMixin(this,size().width,size().height);
		form.setAllButtonNames("Submit","Clear","Help");
		form.setShapeColor(Color.gray);
        	form.setDialogLayout(1, "Helvetica",Font.BOLD , 12 , Color.lightGray);
		form.setMessage("Enter Quantity", new Point(10,40),"Helvetica",Font.BOLD, 18, Color.red);
		form.makeTextBlink(Color.yellow, 5000 );

		try {
			form.setStartupSoundfile(new URL(getCodeBase(),"sounds/beep.au"));
			form.setDescriptionSoundfile(new URL(getCodeBase(),"sounds/moan.au"));
                } catch (MalformedURLException e) {
                        System.out.println("Could not load sound file");
                }

		
		// Component 0;  cell A1	
		int amt = form.setInput("Enter number of Widgets (1-99): ", "1", form.INTEGER,Label.RIGHT,9,100,0); 
		myMinMax[0][0] = 100; myMinMax[0][1] = 0;
		// CUCKOO Sound if amount entered is INVALID
                try {
                        form.setInput(amt,new URL(getCodeBase(),"sounds/cuckoo.au"));
                } catch (MalformedURLException e) {
                        System.out.println("Could not load sound file");
                }

		// Component 1;  cell A2	
		form.setInput("Enter Price ($0.01-$1000.00): ", "1.39", form.DOUBLE,Label.RIGHT,9,1000.1,0); 
		myMinMax[1][0] = 1000; myMinMax[1][1] = 0;
	
		// Make Computed Fields NON-Editable by user
		A3 = new TextField("", 9);
       		form.setInput("Computed Total Price: ", A3);
		A3.setEditable(false);
	
		A4 = new TextField("", 9);
       		form.setInput("Computed Tax at 8.25%: ", A4);
		A4.setEditable(false);

		A5 = new TextField("", 9);
       		form.setInput("Computed Shipping at 10%: ", A5);
		A5.setEditable(false);

		A6 = new TextField("", 9);
       		form.setInput("Computed Grand Total: ", A6);
		A6.setEditable(false);

		form.setWallPaperMode(100,100);
                form.setBackgroundFileName(this,getDocumentBase(),"images/earth7.gif");
		form.CreateUserInput("");
        	add(form);
		validate();
	}
	        public boolean action(Event evt, Object arg) {
		System.out.println("*********************"); 
                System.out.println("evt = " + evt);
                System.out.println("arg = "+arg);

		if ( arg instanceof FormValidation ) {	// CR or Tab fires all inputs	
			int	k;
			FormValidation fv = (FormValidation)arg;
			System.out.println("fv.validationStatus = " + fv.validationStatus);
			System.out.println("fv.whichOne= "+fv.whichOne);
			// Accumulate
			if (fv.whichOne < (form.nComponents() - 1)  ) {  
			//We did not get them all;  record status and return
				fieldStatus[fv.whichOne] = fv.validationStatus;
				return(true);
			}

			// Otherwise: at Last One is Not Valid: Compute allStatus.
			allStatus = 0;
			for ( k = 0; k < form.nComponents(); k++ )
				allStatus = allStatus + fieldStatus[k];
	
			if ( allStatus == 0 ) { 
			// Then Everything fine.  If so, reinitialize and do spreadsheet.
				for ( k = 0; k < form.nComponents(); k++ )
					fieldStatus[k] = 0;
				form.setMessage("Enter Values", new Point(10,40),"Helvetica",Font.BOLD, 24, Color.red);
				// Everything Fine!  Compute Spreadsheet Values
				Integer a1 = new Integer(form.getResponse(0));
				Double a2 = new Double(form.getResponse(1));
				double a3 = (a1.intValue() * a2.doubleValue() );
				double a4 = a3 * 0.0825;
				double a5 = a3 * 0.10;
				double a6 = a3 + a4 + a5;
				System.out.println("a1="+a1+" a2="+a2+" a3="+a3+" a4="+a4+" a5="+a5+" a6="+a6);

				A3.setText(a3 + "");
				A4.setText(a4 + "");
				A5.setText(a5 + "");
				A6.setText(a6 + "");
				
				return(true);
			}

			//  Otherwise everything NOT OK:  Find the First Instance k
			for ( k = 0; k < form.nComponents(); k++ )
				if ( fieldStatus[k] < 0 ) break;
					
			//if ( fv.validationStatus == -1) System.out.println("Input Text Field " +fv.whichOne + " has Wrong Type!");
			if ( fieldStatus[k] == -1) System.out.println("Input Text Field " +k + " has Wrong Type!");
			if ( fieldStatus[k] == -2) System.out.println("Input Text Field " +k + " is Smaller then "+myMinMax[k][1]);
			if ( fieldStatus[k] == -3) System.out.println("Input Text Field " +k + " is Bigger then "+myMinMax[k][0]);
					
			if ( fieldStatus[k] == -1)form.setMessage("ERROR: "+Ids[k]+"  -has Wrong Type!", 
							new Point(10,40),"Helvetica",Font.BOLD, 14, Color.yellow);
			if ( fieldStatus[k] == -2)form.setMessage("ERROR: "+Ids[k]+" -is Smaller then " +myMinMax[k][1], 
							new Point(10,40),"Helvetica",Font.BOLD, 14, Color.yellow);
			if ( fieldStatus[k] == -3)form.setMessage("ERROR: "+Ids[k]+ " -is Bigger then " +myMinMax[k][0], 
							new Point(10,40),"Helvetica",Font.BOLD, 14, Color.yellow);
			return(true);	
		}

                if ("Submit".equals(arg)) {
                        System.out.println("Submit button pressed");
			if ( allStatus < 0 ) return(true);
			for ( int i = 0; i < form.nComponents(); i++ ) {
				System.out.println("User input is " + form.getResponse(i));
			form.setMessage("Enter Quantity", new Point(10,40),"Helvetica",Font.BOLD, 18, Color.red);

			}
                } else if ("Clear".equals(arg)) {
                        System.out.println("Clear button pressed");
			form.setResponse(0, "");
			form.setResponse(1, "");
			A3.setText("");
			A4.setText("");
			A5.setText("");
			A6.setText("");
			form.setMessage("Enter Quantity", new Point(10,40),"Helvetica",Font.BOLD, 18, Color.red);
                } else if ("Help".equals(arg)) {
                        System.out.println("Help button pressed");
			form.setMessage("Enter Quantity", new Point(10,40),"Helvetica",Font.BOLD, 18, Color.red);
                }
		//remove(form);
		
                return ( true );
        }
        /////////////////////////
        // Stop the form thread
        /////////////////////////
        public void stop() {
                form.stop();
        }
	
	public boolean mouseEnter(java.awt.Event evt, int x, int y) { 
		showStatus("(c) 1996 Inductive Solutions, Inc.");
		return (true);
    	}

}
