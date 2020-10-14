import controls.*;
import java.applet.Applet;
import java.awt.*;
import java.net.*;
/**
 *	This applet shows the different types of components that the FormMixin supports
 *	(c) 1996 Inductive Solutions, Inc. 	
 *
 */
public class ShowJavaDialogAction extends Applet {
	FormMixin form = null;
	// For The Action Routine
	TextField 	myTextField;
	SliderMixin 	mySlider;

	public void init () {
		int	i;
		form = new FormMixin(this,size().width,size().height);
		form.setAllButtonNames("Submit","Clear","Help");
		form.setShapeColor(Color.gray);
        	form.setDialogLayout(3, "Helvetica",Font.PLAIN , 8 , Color.lightGray);
		form.setMessage("Please Enter Values in the HodgePodge Form!",	// Msg
			new Point (10,40),		// Position
			"TimesRoman",			// font family
			Font.BOLD,			// style
			12,				// size
			Color.red);
		// Start Up Sounds
		try {
			form.setStartupSoundfile(new URL(getCodeBase(),"sounds/cuckoo.au"));
			form.setDescriptionSoundfile(new URL(getCodeBase(),"sounds/joy.au"));
                } catch (MalformedURLException e) {
                        System.out.println("Could not load sound file");
                }
		/////////////////////////////////	
		// Add 6-inputs: #0,1,2,3,4,5
		/////////////////////////////////	
		// First Row 
		form.setInput("0Label.RIGHT ", "First", form.NORMAL,Label.RIGHT,10);
		form.setInput("1Label.CENTER", "Second", form.NORMAL,Label.CENTER,10);
		form.setInput("2Label.LEFT", "Third", form.NORMAL,Label.LEFT,10);

		// Second Row
		form.setInput("3Label.LEFT", "123456", form.INTEGER,Label.LEFT,5);
		form.setInput("4Label.LEFT", "13", form.INTEGER,Label.LEFT,5);
                
		form.setInput("5 Label.RIGHT", "3.14", form.DOUBLE,Label.RIGHT,4);
		// Get and Set with form.getResponse(i); form.setResponse(i, "string")

		/////////////////////////////////	
		// 1.  Add Button
		/////////////////////////////////	
		// Third Row
		Button myButton1 = new Button("Listen !");
		Button myButton2 = new Button("Look !");
		Button myButton3 = new Button("Stop !");
		form.setInput("6 Press Me: ", myButton1);
		form.setInput("7 Press Me Too: ", myButton2);
		form.setInput("8 Press Me Three: ", myButton3);
		/////////////////////////////////	
		// 2.  Add Checkbox
		/////////////////////////////////	
		Checkbox myCheck0 = new Checkbox("myCheck0");
		Checkbox myCheck1 = new Checkbox("myCheck1");
		Checkbox myCheck2 = new Checkbox("myCheck2");
		CheckboxGroup myGroup = new CheckboxGroup();
		Checkbox myRadio0 = new Checkbox("myRadio0",myGroup,true);
		Checkbox myRadio1 = new Checkbox("myRadio1",myGroup,true);
		Checkbox myRadio2 = new Checkbox("myRadio2",myGroup,true);

		// Fourth Row
		form.setInput("9 myCheck0: ", myCheck0);
		form.setInput("10 myCheck1: ", myCheck1);
		form.setInput("11 myCheck2: ", myCheck2);

		// Fifth Row
		form.setInput("12 myRadio0: ", myRadio0);
		form.setInput("13 myRadio1: ", myRadio1);
		form.setInput("14 myRadio2: ", myRadio2);


		// Get with myCheck0.getState(); myCheck0.setState(true)
		// 3.  Add Choice
		Choice myChoice = new Choice();
        		myChoice.addItem("First");
        		myChoice.addItem("Second");
        		myChoice.addItem("3rd");
        		myChoice.addItem("Fourth Choice");

		// Sixth Row
		form.setInput("15 myChoice: ", myChoice);
	
		// 4.  Add List
		List myList = new List(4,false); //Does Not allow multiple selection
        		myList.addItem("First Element");
        		myList.addItem("Second");
        		myList.addItem("Third");
        		myList.addItem("4th Element");
			myList.addItem("Fifth");
			myList.addItem("Sixth Element");
		form.setInput("16 myList: ", myList);
		
		// 5.  Add TextArea
		TextArea myTextArea = new TextArea("This is the first\nThis is the second\n",
							3, 15); // NB.  width in characters
       		form.setInput("17 myTextArea: ", myTextArea);
		// Get with myTextArea.paramString();  Set with myTextArea.replaceText("String", from, to);

		// Seventh Row
		// 6. Add Text Field
		myTextField = new TextField("18 A TextField! ", 15);
       		form.setInput("my Messages: ", myTextField);
		myTextField.setEditable(false);

		// 7.  Add a Slider
		mySlider = new SliderMixin(100, 20, 0, 100, Color.blue, true);
       		form.setInput("19 mySlider: ", mySlider);
		// Get with mySlider.getValue();


		// Add a Ticker
		TickerMixin	myTicker = new TickerMixin(this,100,20);
		myTicker.setBoxMessage("Courier",Font.PLAIN,10 , Color.green, Color.red);
		myTicker.setTickerText("Can You Do This? ");
		form.setInput("20 Last One", myTicker);
		myTicker.start();

		form.setWallPaperMode(100,100);
                form.setBackgroundFileName(this,getDocumentBase(),"images/earth7.gif");
		form.CreateUserInput("HodgePodge Form");
        	add(form);
		validate();
	}
        public boolean action(Event evt, Object arg) {
		System.out.println("In ShowJavaDialogAction.class "); 
                System.out.println(evt);
                System.out.println(arg);

		if ( evt.target == mySlider ) {
			myTextField.setText("mySlider value = "+mySlider.getValue() );
		}
		
                if ("Submit".equals(arg)) {
                        System.out.println("Submit button pressed");
			for ( int i = 0; i < form.nComponents(); i++ ) {
				System.out.println("User input is " + form.getResponse(i));
				myTextField.setText("mySlider value = "+mySlider.getValue() );
			}
                } else if ("Clear".equals(arg)) {
                        System.out.println("Clear button pressed");
			for ( int i = 0; i < form.nComponents(); i++ ) {
				form.setResponse(i, "");
				mySlider.setValue(0);
				myTextField.setText("mySlider value = "+mySlider.getValue() );
			}
                } else if ("Help".equals(arg)) {
                        System.out.println("Help button pressed");
                }
		//remove(form);
                return ( true );
        }

	public void stop() {
		form.stop();
	}

	public boolean mouseEnter(java.awt.Event evt, int x, int y) { 
		showStatus("(c) 1996 Inductive Solutions, Inc.");
		return (true);
    	}


}
