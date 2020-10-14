/*
AnimNav.java
Version 1.0.0
Written by Elijah Dean Meeker 1/4/96
This applet loads a series of URLs, their descriptions and a series of images,
usually an "animation". A translation table links the given URLs to frames(or
sets of frames) of the animation. Clicking on the applet while a given image is
showing navigates to the URL linked to that image.  Optionally, when the mouse
is not over the applet an timer will auto advance through the images. It uses
off-screen buffering to avoid flicker. If you like my commenting thank
Tom Murtag, my C instructor who kept taking points off until I did it right.
If you don't think I commented enough please feel free to take points off.
I would love to see your improvements.
Elijah.

elijah@bga.com
http://www.realtime.net/~elijah/


Here is a valid applet tags:
[Optional params are in brackets][Second brackets contain defaults]
<APPLET
codebase="classes"
CODE="AnimNav.class" WIDTH=75 HEIGHT=75>             SIZE of button images
[<PARAM NAME="spriteX" VALUE="10">]                  LEFT pos. to draw sub-images[0]
[<PARAM NAME="spriteY" VALUE="10">]                  TOP pos. to draw sub-images [0]

[<PARAM NAME="sleeptime" VALUE="500">]               TIME between images[autorun off]

<PARAM NAME="imageCount" VALUE="8">                  Image Count

<PARAM NAME="translation" VALUE="0|0|1|1|2|2|3|3">   Which URL goes with
																	  which frame of animation
<PARAM NAME="URLcount" VALUE="4">                    URL count

<PARAM NAME="dest0" VALUE="http://www.realtime.net/~elijah/"> URL to navigate to
<PARAM NAME="dest1" VALUE="http://www.mel.dit.csiro.au/~brendan/">URL to nav...
<PARAM NAME="dest2" VALUE="http://198.3.117.222">             URL to navigate to
<PARAM NAME="dest3" VALUE="http://www.sun.com/">              URL to navigate to

[<PARAM NAME="desc0" VALUE="Homepage of Elijah Dean Meeker">] URL description
[<PARAM NAME="desc1" VALUE="Homepage of Brendan Hills">]      URL description
[<PARAM NAME="desc2" VALUE="BreakFast Cereal Hall Of Fame">]  URL description
[<PARAM NAME="desc3" VALUE="Sun Microsystems">]               URL description

[<PARAM NAME="target0" VALUE="_jmainview">]       target window for URL[_parent]
[<PARAM NAME="target3" VALUE="_jmainview">]       target window for URL[_parent]

[<PARAM NAME="background" VALUE="images/cub_bg.jpg"> ] BACKGROUND image
																			 (offset from codebase)
<PARAM NAME="prefix" VALUE="images/nav">               Prefix of images
																			 (offset from codebase)
[<PARAM NAME="imagetype" VALUE="jpg">]               Image type-WITHOUT '.'[jpg]
</APPLET>



*/
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.lang.InterruptedException;
import java.net.MalformedURLException;
import java.net.URL;
/******************************************************************************/
/**************************AnimNav.class***************************************/

public class AnimNav extends java.applet.Applet {
		  //Graphics
		  MediaTracker tracker;		//to make sure images load before showing
		  Dimension d;    			//for sizing offscreen buffer
		  Graphics offscreen;
		  Image buf;
		  Image bg;       			//background image
		  Image img[];			   	//array of sprite images


		  NavAutoRun autoRun;		//timer for auto animation

		  int spriteX,spriteY;		//sprite offset on background
		  int imageCount;      		//total number of sprite images

		  int oldx = 0;         	//tested in mouseDrag
		  int downx=0;         		//tested in mouseUp
		  public int frame = 0; 	//current frame of animation
		  public int URLcount=0;

		  boolean bgP=true;      	//background image loaded flag
		  boolean autorunP=false;	//autorun setup flag
		  URL URLdest[];         	//list of URLs
		  String URLdescription[];	//URLdescriptions
		  String targetWindow[];
		  int translate[];      //Which URLs go with which frames of the animation

/****************************STATE CHANGES*************************************/
		  public void init(){
		  String str;
		  String imageType;

	//offscreen buffer
					 d = size();
					 buf= createImage(d.width,d.height);
					 offscreen = buf.getGraphics();

	//MediaTracker
					 tracker = new MediaTracker(this);

	/************************LOAD spriteX****************************/
					 str =getParameter("spritex");
					 if(str== null){
						spriteX=0;
					 }else{
						spriteX=Integer.parseInt(str);
					 }
					 /************************LOAD spriteY**************************/
					 str =getParameter("spritey");
					 if(str== null){
						spriteY=0;
					 }else{
						spriteY=Integer.parseInt(str);
					 }
					 /*********************LOAD NavAutoRun init*********************/
					 str =getParameter("sleeptime");
					 if(str!= null){
						autorunP=true;
						autoRun=new NavAutoRun(this,Integer.parseInt(str));
					 }
					 /************************LOAD urlcount*************************/

					 str= getParameter("urlcount");
					 if (str == null){
						System.out.println
						("Error Loading: urlcount, Not Optional");
					 }else{
						URLcount= Integer.parseInt(str);
						URLdescription=new String[URLcount];
						targetWindow=new String[URLcount];
						URLdest=new URL[URLcount];
					 }//end if
					 /************************LOAD URLs*****************************/

					 for(int c=0;c<URLcount;c++){
						 URLdescription[c]= getParameter("dest"+c);
						 try{
							if (URLdescription[c] != null) //this is here for development
								URLdest[c] = new URL(URLdescription[c]);
						 }catch(MalformedURLException mal){
							System.out.println("Malformed URL: Check Applet Tag.");
						 }//end try
					 }//end for
					 /************************LOAD URL Descriptions*****************/

					 for(int c=0;c<URLcount;c++){
						str= getParameter("desc"+c);
						if (str != null)
							URLdescription[c]=str;
					 }//end for
					 /************************LOAD URL targetWindow*****************/

					 for(int c=0;c<URLcount;c++){
						str= getParameter("target"+c);
						if (str == null){
							targetWindow[c]=" _parent";
						}else{
							targetWindow[c]=str;
						}//end if
					 }//end for
					 /************************LOAD imageCount***********************/

					 str =getParameter("imagecount");
					 if (str == null){
						System.out.println
						("Error getting Parameter: imagecount, Not Optional.");
					 }else{
						imageCount= Integer.parseInt(str);
						img = new Image[imageCount];
					 }
					 /************************LOAD translate[]*************************/
					 str = getParameter("translation");
					 if (str == null){
						System.out.println
						("Error getting Parameter: translation, Not Optional.");
					 }else{
						translate=new int[imageCount];
						int index=0;
						for (int i = 0; i < str.length(); ) {
							if (index >= imageCount) break;
							int next=str.indexOf('|', i);
							if (next == -1) next = str.length();
							if (i != next) {
								translate[index]=Integer.parseInt(str.substring(i, next));
								index++;
							}//end if
							i = next + 1;
						}//end for
					 }//end if str==null
			 /*
						//this would also work for above
						StringTokenizer st = new StringTokenizer(s,"|");
						while (st.hasMoreTokens()) {
							translate[index]=Integer.parseInt(st.nextToken());
							index++;
						}

			 */
					 /************************LOAD imagetype**************************/

					 str = getParameter("imagetype");
						if (str == null){
							imageType="jpg";
						}else{
							imageType=str;
						}//end if

					 /************************LOAD background image**************************/

					 str = getParameter("background");
						if (str == null){
							bgP=false; //not using background image
							spriteX=0;spriteY=0;
						}else{
							showStatus("Loading Background Image");
							bg =  getImage(getCodeBase(),str);
							tracker.addImage(bg, 0);
							try {
								tracker.waitForAll();
							} catch (InterruptedException e) {
								System.out.println("Error waiting for Background image to load.");
							}//end catch
							showStatus("");
						}//end if

					 /************************LOAD animation images*********************/

					 str = getParameter("prefix");
					 if (str == null){
						System.out.println
						("Error Loading image: "+str+", Not Optional");
					 }else{
						for (int i = 0; i < imageCount; i++) {
							showStatus("AnimNav Loading Image :"+(i+1)+ " of " + imageCount);
							img[i] =  getImage(getCodeBase(),str+i+"."+imageType);
							tracker.addImage(img[i], 1);
							try {
								tracker.waitForAll();
							} catch (InterruptedException e) {
								System.out.println("Error waiting for image"+i+" to load");
							}//end catch
							showStatus("");
						}//end for
					}//end if str == null

		  }//end init
/******************************************************************************/

  public void start(){
		  if(autorunP==true)autoRun.start();	//if it's wanted, start timer
		  frame=downx=oldx=0;			//init state variables
		  repaint();
  }//end start
/******************************************************************************/

  public void stop(){
					 if(autorunP==true)autoRun.stop();

  }//end stop
/******************************************************************************/

  public void destroy(){
  }//end destroy
/****************************END STATE CHANGES********************************/
/*******************************EVENTS****************************************/

  public boolean mouseDown(Event e, int x, int y){
		  downx=oldx=x;

		  return(true);
  }//end mouseDown
/******************************************************************************/

  public boolean mouseUp(Event e, int x, int y){
       //below is all this program really does. i.e. "Go to the URL associated with the frame of the 
      //animation and put it in the window associated with that URL"

        if(downx<x+2 && downx>x-2)			//set in mouseDown
            getAppletContext().showDocument(URLdest[translate[frame]],targetWindow[translate[frame]]);

        return(true);
  }//end mouseUp
/******************************************************************************/

  public boolean mouseEnter(Event e, int x, int y){

		  if(autorunP==true)autoRun.stop();
		  //put URL description in browser status window
		  showStatus(URLdescription[translate[frame]]);

        return(true);
  }//end mouseEnter
/******************************************************************************/

  public boolean mouseExit(Event e, int x, int y){
        if(autorunP==true)autoRun.start();
        showStatus("");

        return(true);
  }//end mouseExit
/******************************************************************************/

  public boolean mouseDrag(Event e,int x, int y){

  if(x>=(oldx + 20)){					//if you drag far enough to the right
	oldx=x;									//update where you are
	frame--;									//show a new image
	if (frame<0){frame=imageCount-1;}//wrap around
  }else if(x<=(oldx - 20)){  			//if you drag far enough to the left
	oldx=x;									//update where you are
	frame++;									//show a new image
	if (frame>=imageCount){frame=0;}	//wrap around
  }
  //put URL description in browser status window
  showStatus(URLdescription[translate[frame]]);
  repaint();

  return(true);
  }//end mouseMove
/*******************************END EVENTS*************************************/
/*******************************METHODS****************************************/

		  public  void update(Graphics g){
					 paint(g);
		  }//end update
/******************************************************************************/

		  public void paint(Graphics g){
					 if (offscreen != null) {
						paintApplet(offscreen);
						g.drawImage(buf, 0, 0, this);
					 } else {
						paintApplet(g);
					 }
		  }//end paint
/******************************************************************************/

			public void paintApplet(Graphics g) {
					 if(bgP==true)
						g.drawImage(bg,0,0,null);

					 g.drawImage(img[frame],spriteX,spriteY,null);

			}//end paintApplet
/******************************************************************************/
		  // Applet info
		  public String getAppletInfo(){
					 return "AnimNav.class By Elijah Meeker 1/5/96";
		  }//end getAppletInfo
/******************************************************************************/
		  public String[][] getParameterInfo() {
						String[][] info = {
						  {"[spriteX]",
							"int",
							"X offset of images on background [0]"},
						  {"[spriteY]",
							"int",
							"Y offset of images on background[0]"},
						  {"[sleeptime]",
							"int",
							"pause between images in AutoRun[AutoRun off]"},
						  {"imageCount",
							"int",
							"Number of animation images"},
						  {"translation",
							"parsed string",
							"which URLs go with which images,form:0|0|1|1"},
						  {"URLcount",
							"int",
							"number of URLs"},
						  {"dest+int",
							"url",
							"URL to navigate to(int min=0, max=URLcount-1"},
						  {"[desc+int]",
							"string",
							"URL description[string of dest+int]"},
						  {"[target+int]",
							"string",
							"target Window for URL[_parent]"},
						  {"[imagetype]",
							"string",
							"image type suffix W/O '.'[jpg]"},
						  {"[background]",
							"string",
							"Background image(offset from CodeBase)[no background, spriteX=spriteY=0]"},
						  {"prefix",
							"string",
							"Prefix to sprites(offset from CodeBase)"}

					  };
						return info;
		  }//end getParameterInfo
/*****************************END METHODS**************************************/

}//end class AnimNav.class
/******************************************************************************/
/******************************************************************************/
/*****************************AutoRun.class************************************/

class NavAutoRun implements Runnable {
	Thread T = null;
	AnimNav applet;
	int howLong;
/*****************************Constructors*************************************/

	NavAutoRun(AnimNav applet,int time){
		this.applet = applet;
		howLong=time;
	}//end constructor
/****************************State Changes*************************************/

  public void start(){
	if (T == null) {
		T = new Thread(this,"T");
		T.start();
		T.setPriority(Thread.MIN_PRIORITY+1);
	}
  }//end start
/******************************************************************************/

  public void run(){
	while(T!=null){
		applet.repaint();
		applet.frame++;
		if (applet.frame>=applet.imageCount){applet.frame=0;}
		applet.showStatus(applet.URLdescription[applet.translate[applet.frame]]);
		try {Thread.sleep(howLong);} catch (InterruptedException e){}
	}//end while
  }//end run
/******************************************************************************/

  public void stop(){
	if (T != null) {
		T.stop();
		T=null;
	}
  }//end stop
/******************************************************************************/
}//end AutoRun.class 