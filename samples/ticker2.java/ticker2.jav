import java.awt.Graphics;
import java.applet.Applet;
import java.awt.Font;
import java.awt.Color;
import java.awt.Event;
import java.awt.FontMetrics;
import java.awt.Image;

public class ticker extends Applet implements Runnable{

String input_text[] = new String[4];
char mesg[][] = new char[4][] ;
int xpos=400;
int ypos=50;
int realLength;
int realHeight;
int width=400;
int height=50;
boolean suspended = false;
Color color = new Color(0,255,255);
Thread killme=null;
Image im;
Graphics osGraphics;
Color foregroundCol;
Color DefaultforegroundCol = new Color(0, 0, 0);
Color backgroundCol; 
Color DefaultbackgroundCol = new Color(192, 192, 192); 
Color TextColor[] = new Color[4] ;
Font showfont; 
int speed = 5 ;
int s_time[] = new int[10] ;
int seq = 0 ;
FontMetrics fmetrics;
boolean nervous = false ;


  private int getColorFromParam(String parmName, Color defaultCol){
    Integer IntColor;
    String parmValue = this.getParameter(parmName);
    if (parmValue != null)
    {
     	Integer intColor = Integer.valueOf(parmValue, 16);
    	return(intColor.intValue());
    }
    return (defaultCol.getRGB());
  }

  public void init(){
    String FontFamily = new String("TimesRoman");
    int FontSize = 12;
    int FontWeight = Font.PLAIN;
    Integer tmp ;
    backgroundCol = new Color(this.getColorFromParam("BGCol", DefaultbackgroundCol));
    foregroundCol = new Color(this.getColorFromParam("FGCol", DefaultforegroundCol));
    this.setBackground(backgroundCol);

    TextColor[0] = new Color(this.getColorFromParam("C1",(new Color(0,0,255))));
    TextColor[1] = new Color(this.getColorFromParam("C2",(new Color(0,255,0))));
    TextColor[2] = new Color(this.getColorFromParam("C3",(new Color(255,255,0))));
    TextColor[3] = new Color(this.getColorFromParam("C4",(new Color(255,0,255))));
 

    s_time[0] = 200 ;
    s_time[1] = 100 ;
    s_time[2] = 75 ;
    s_time[3] = 50 ;
    s_time[4] = 32 ;
    s_time[5] = 16 ;
    s_time[6] = 8 ;
    s_time[7] = 4;
    s_time[8] = 2 ;
    s_time[9] = 1 ;
 
    input_text[0]=getParameter("text1");
    input_text[1]=getParameter("text2");
    input_text[2]=getParameter("text3");
    input_text[3]=getParameter("text4");

    mesg[0] = new char[input_text[0].length()] ;
    mesg[1] = new char[input_text[1].length()] ;
    mesg[2] = new char[input_text[2].length()] ;
    mesg[3] = new char[input_text[3].length()] ;
	
    input_text[0].getChars(0,input_text[0].length(),mesg[0],0);
    input_text[1].getChars(0,input_text[1].length(),mesg[1],0);
    input_text[2].getChars(0,input_text[2].length(),mesg[2],0);
    input_text[3].getChars(0,input_text[3].length(),mesg[3],0);

    if (this.getParameter("Speed") != null){ 
	tmp = new Integer(this.getParameter("Speed"));
	speed = tmp.intValue();
	if((speed >=0) && (speed <10)){
	}else{
		speed = 5 ;
	}	
    }

    if (this.getParameter("Nervous") != null){ 
	tmp = new Integer(this.getParameter("Nervous"));
	nervous =  (tmp.intValue() == 1);	
    }



    if (this.getParameter("FontFamily") != null)
    {
    	FontFamily = this.getParameter("FontFamily");
    }

    if (this.getParameter("FontSize") != null)
    {
    	Integer IntFontSize = new Integer(this.getParameter("FontSize"));
    	FontSize = IntFontSize.intValue();
    }

    if (this.getParameter("FontWeight") != null)
    {
    	String SFontWeight = new String(this.getParameter("FontWeight"));
    	SFontWeight = SFontWeight.toLowerCase();
    	if (SFontWeight.equals("bold")){ FontWeight = Font.BOLD; }
    	if (SFontWeight.equals("italic")){ FontWeight = Font.ITALIC;}
    	if (SFontWeight.equals("plain")){ FontWeight = Font.PLAIN;}
    }
    showfont = new Font(FontFamily, FontWeight, FontSize);  

    im=createImage(size().width,size().height);
    osGraphics=im.getGraphics();
  }

  public void paint(Graphics g){
    paintText(osGraphics);
    g.drawImage(im,0,0,null);
  }

  public void paintText(Graphics g){
    int i,x_coord,y_coord;
    int char_width;

    g.setColor(backgroundCol);
    g.fillRect(0,0,width,height);
    g.clipRect(0,0,width,height);
    g.setFont(showfont);
    g.setColor(TextColor[seq]);
    fmetrics=g.getFontMetrics();
    char_width = fmetrics.charsWidth(mesg[seq],0,1);
    realLength=fmetrics.stringWidth(input_text[seq]);
    realHeight=fmetrics.getHeight();
	
    if(nervous){
      for(i=0;i<input_text[seq].length();i++)
      {
	x_coord = (int) (Math.random()*6+char_width*i+xpos);
	y_coord = (int) (Math.random()*6+ypos);
	g.drawChars(mesg[seq], i,1,x_coord,y_coord);
      }
    }else{
  	g.drawString(input_text[seq],xpos,ypos);
    }
  }

 public void start(){
   if(killme==null){
  	killme=new Thread(this);
   	killme.start();
   }
 }

 public void setcoord(){
     switch(seq){
	case 0: 
   		xpos = xpos-2;
   		ypos = (height+realHeight)/2 ;
   		if(xpos<-realLength){
  			ypos = height;
			realLength=fmetrics.charsWidth(mesg[1],0,input_text[1].length());
			xpos = (width - realLength)/2 ;
			seq = 1;
   		}
		break ;
	case 1:
		ypos = ypos - 1;
   		if(ypos<-realHeight){
  			xpos = -fmetrics.stringWidth(input_text[2]);
			ypos = (height+realHeight)/2 ;
			seq = 2 ;
   		}
		break ;
	case 2:
		xpos = xpos+2;
   		if(xpos > realLength){
			realLength=fmetrics.charsWidth(mesg[3],0,input_text[3].length());
  			xpos = (width - realLength)/2 ;
			ypos = 0 ;
			seq = 3;
   		}
		break ;
	case 3 :
		ypos = ypos + 1;
   		if(ypos > (realHeight + height)){
  			xpos = width;
			ypos = (height+realHeight)/2 ;
			seq = 0 ;
   		}
		break ;
    };
 }



 public void run(){

 while(killme != null){
   try {Thread.sleep(s_time[speed]);}
	catch(InterruptedException e){}
   setcoord();
   repaint();
   }
 }

 public void update(Graphics g){
   paint(g);
 }

 public boolean handleEvent(Event evt) {
   if (evt.id == Event.MOUSE_DOWN) {
   	if (suspended) {
              killme.resume();
        } else {
              killme.suspend();
        }
        suspended = !suspended;
   }
   return true;
 }

 public void stop(){
   if(killme != null)
 	 killme.stop();
	 killme=null;
 }

}
