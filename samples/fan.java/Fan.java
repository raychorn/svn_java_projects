/*
=========
 TEXTFAN 
=========

Author: Leon Cho
Date: May 1996

Description:
  Draws a text string on the screen by "fanning out" from a central point and fades
  in text color as it moves.

Syntax:
  To use, embed the following in your HTML page:

  <applet code=Fan.class width=600 height=30>
  <param name=TEXT VALUE="Insert your text here!">
  <param name=SPEED VALUE="4">
  <param name=SPACING VALUE="4">
  <param name=VSPACING VALUE="20">
  <param name=FONT VALUE="Courier">
  <param name=FONTSIZE VALUE="22">
  </applet>

Parameter description:
  TEXT: specifies the text string to draw
  SPEED: specifies the distance in pixels to move the text on each frame of animation
  SPACING: Specifies the amount of spact to allot for each letter
            (NOTE: this number is a multiple of the SPEED parameter which sets
                   the basic unit of movement.  For example, if the SPEED parameter
                   is set to 3 and the SPACING is set to 4, each letter is alloted 
			 3*4=12 pixels)
  VSPACING: Specifies the number of pixels that space the string from the top of the applet
  FONT: Specifies the font to use for drawing the string
		(NOTE: You must use a fixed-sized font such as Courier for this to work.
		       If the font is not fixed, this effect is not aesthetically pleasing)
  FONTSIZE: Specifies the size of the font
*/

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.lang.String;
import java.awt.Image;
import java.lang.Integer;

public class Fan extends java.applet.Applet implements Runnable {
    Thread runner;
    
    String fontName;
    int fontSize;
    Font textFont;
    int pixelSize;
    
    char[] textArray; 		// Holds the characters of the text string
    int[] positionArray; 	// Specifies the current position of each character
					//   in the text string
    int[] finalPositionArray; // Holds the final position of each
					//   character in the text string
    boolean[] movingArray;    // Specifies whether each character in the text
 					//   string is currently moving
    boolean doneMovingText = false; // Specifies whether all characters are done moving

    Image bufferScreenImg;  	// Buffer screen image (double-buffered animation)
    Graphics bufferScreenGfx; // Buffer screen graphics (double-buffered animation)

    String textString;		// Holds the text string
    int textStringLength;	// Holds the length of the text string
    Color textColor;	      // Holds the color of the text string
    int redVal, greenVal, blueVal;	// Specifies the current color of the text being drawn

    int textStringLeftMidPoint;   	// Holds the index of the character to the left
						//   of the midpoint of the string
    int textStringRightMidPoint;	// Holds the index of the character to the right
						//   of the midpoint of the string

    int textSpacing; 		// Specifes the amount of space alloted for each character
 					//   of text (as a multiple of pixelSize)
    int appletVPadding;	      // Specifies the vertical position in pixels from the top
					//   of the applet from which to draw the text
    int appletHorizCenter;	// Holds the center position of the applet in pixels
    int appletWidth;		// Holds the width of the applet in pixels
    int appletHeight;		// Holds the height of the applet in pixels

    public void init() {
        String textString = getParameter("TEXT");
	  if (textString == null) 
          textString = "INSERT YOUR TEXT HERE"; 
        textStringLength = textString.length();

	  String paramSpeed = getParameter("SPEED");
        if (paramSpeed == null) 
           pixelSize = 4;
        else
           pixelSize =  Integer.parseInt(paramSpeed);

        String paramTextSpacing = getParameter("SPACING");
        if (paramTextSpacing == null) 
           textSpacing = 4 * pixelSize;
        else
          textSpacing = Integer.parseInt(paramTextSpacing) * pixelSize;

        String paramVSpacing = getParameter("VSPACING");
        if (paramTextSpacing == null)
           appletVPadding = 20;
        else
           appletVPadding = Integer.parseInt(paramVSpacing);

        String paramFont = getParameter("FONT");
        if (paramFont == null)
           fontName = "Courier";
        else
           fontName = paramFont;

        String paramFontSize = getParameter("FONTSIZE");
        if (paramFontSize == null)
          fontSize = 22;
        else
          fontSize = Integer.parseInt(paramFontSize);

        textFont = new Font(fontName,Font.BOLD,fontSize);  // Must use fixed font

        appletWidth = this.size().width;
        appletHeight = this.size().height;
	  appletHorizCenter = appletWidth / 2;

        if ((textStringLength % 2) == 0) {
           textStringLeftMidPoint = (textStringLength / 2);
           textStringRightMidPoint = (textStringLength / 2) + 1;
        } else {
           textStringLeftMidPoint = (textStringLength / 2);
           textStringRightMidPoint = (this.textStringLength / 2) + 2;
        }

	  textArray = new char[textStringLength];

	  positionArray = new int[textStringLength];
	  for (int i = 0; i < textStringLength; i++) {
          positionArray[i] = this.appletHorizCenter - (textSpacing / 2);
        }

	  finalPositionArray = new int[textStringLength];
        int leftPadding = (appletWidth - (textStringLength * textSpacing)) / 2;
        for (int i = 0; i < textStringLength; i++) {
           finalPositionArray[i] = (i * textSpacing) + leftPadding;
        }
        
        movingArray = new boolean[textStringLength];
        for (int i = 1; i < textStringLength - 1; i++) {
          movingArray[i] = false;
        }
        movingArray[0] = true;
        movingArray[textStringLength - 1] = true;        

        textString.getChars(0,textStringLength,textArray,0);

	  //Make hidden screen for double buffering of animation
	  bufferScreenImg = createImage(appletWidth,appletHeight);
	  bufferScreenGfx = bufferScreenImg.getGraphics();
    }


    public void start() {
        if (runner == null); {
            runner = new Thread(this);
            runner.start();
        }
    }

    public void stop() {
        if (runner != null) {
            runner.stop();
            runner = null;
        }
    }

    public void run() {
	  int appletHorizMax = 500;
        int stepCounter = 0;

        setBackground(Color.black);
     
        while (! doneMovingText) {

		for (int i = 1; i < this.textStringLeftMidPoint; i++) {
              if (i * this.textSpacing == stepCounter) {
			movingArray[i] = true;
  		  }
            }

            for (int i = this.textStringRightMidPoint - 1; i < (this.textStringLength - 1); i++) {
              int tempIndex = this.textStringLength - i - 1;
              if (tempIndex * this.textSpacing == stepCounter) {
                  movingArray[i] = true;
              }
            }
            stepCounter += this.pixelSize;

      	for (int i = 0; i < this.textStringLength; i++) {
      	   if (positionArray[i] == finalPositionArray[i]) {
			 movingArray[i] = false;
               }
            }

 	 	if ((movingArray[0] == false) && (movingArray[this.textStringLength - 1] == false)) {
 		   this.doneMovingText = true;
            }

		for (int i = 0; i < this.textStringLeftMidPoint; i++) {
              if (movingArray[i] == true) {
                positionArray[i] -= this.pixelSize;
              }
            }
	      
		for (int i = this.textStringRightMidPoint - 1; i < this.textStringLength; i++) {
              if (movingArray[i] == true) {
		    positionArray[i] += this.pixelSize;
              }
	      }

	      repaint();
            try{ Thread.sleep(100); }
            catch (InterruptedException e) { }
           
        }
    }

    public void update(Graphics g) {
	  paint(g);
    }

    public void paint(Graphics screenGfx) {
        bufferScreenGfx.setColor(Color.black);
        bufferScreenGfx.fillRect(0,0,this.appletWidth,this.appletHeight);

	  bufferScreenGfx.setFont(textFont);

	  for (int i=0; i < this.textStringLength; i++) {
	    //Draw shadow for text
          bufferScreenGfx.setColor(Color.white);
          bufferScreenGfx.drawChars(textArray,i,1,positionArray[i] - 1,this.appletVPadding + 1);

	    //Draw foreground text
	    //  Set color of text to fade in (as % of total distance from center that text will move)
          float leftPadding = (float)(appletWidth - (textStringLength * textSpacing)) / 2;
          redVal = (int)(((Math.abs((float)this.appletHorizCenter - (float)positionArray[i])) / ((float)this.appletHorizCenter - leftPadding)) * 255.0);
          
	    textColor = new Color(redVal,50,0);
          
 	    bufferScreenGfx.setColor(textColor);
          bufferScreenGfx.drawChars(textArray,i,1,positionArray[i],this.appletVPadding);
	  }  

	  // Draw buffer screen to screen (double-buffered animation)
        screenGfx.drawImage(bufferScreenImg,0,0,this);
    }
}
