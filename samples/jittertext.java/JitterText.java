/*  JitterText

    revised by W.Giel 2 Mar 96
    Revised init(), revised paint(),added update() w/buffered image,
    added color and speed params, added code to optimize font height.

    revised by W.Giel 8 Mar 96
    Added random colors

    based on NervousText Applet
    by  Daniel Wyszynski 
        Center for Applied Large-Scale Computing (CALC) 
        04-12-95
        
    revised by kwalrath 5-9-95

 
    Parameters: TEXTCOLOR - string - Hex RGB triplet - default: #000000 (black)
                BGCOLOR   - string - Hex RGB triplet - default: #C0C0C0 (light gray)
                TEXT      - string - message - degault: "Java Power!"
                SPEED     - int    - delay in millisecond - default: 200
                RANDOMCOLOR    - int    - non-zero=true;

 
*/

import java.awt.*;
import java.applet.*;
import java.lang.*;

public class JitterText extends java.applet.Applet implements Runnable
{
    static final int MAXCLORS=10;
    String s = null;
    Thread jitterThread = null;

    boolean threadSuspended = false; //added by kwalrath

    ////////////////////
    // Added by W.Giel
    ////////////////////
    int fontHeight;
    Color textColor=null;
    Color bgColor;
    int speed=200;
    FontMetrics fm;
    int baseline;
    Image offScrImage;
    Graphics offScrGC;
    boolean normal=false;
    Font font;
    Color randomColors[]=new Color[10];
    boolean randomColor=false;

    /////////////////////
    // Revised by W.Giel
    /////////////////////
    public void init()
    {
        String param;
        Graphics g=getGraphics();
        fontHeight=size().height - 10;

        offScrImage = createImage(size().width,size().height);
        offScrGC = offScrImage.getGraphics();

        s = getParameter("text");
        if (s == null) s = "Java Power!";

        int maxlen=size().width - (s.length()+1)* 5 - 10;
        do{
            g.setFont(new Font("TimesRoman",Font.BOLD,fontHeight));
            fm=g.getFontMetrics();
            if(fm.stringWidth(s)>maxlen)fontHeight--;
        }while(fm.stringWidth(s)>maxlen);
        baseline= size().height - fm.getMaxDescent();
        font=new Font("TimesRoman",Font.BOLD,fontHeight);

        if(null == (param=getParameter("TEXTCOLOR")))
            textColor=Color.black;
        else textColor=parseColorString(param);

        if(null == (param=getParameter("BGCOLOR")))
            bgColor=Color.lightGray;
        else bgColor=parseColorString(param);
        setBackground(bgColor);

        if(null != (param=getParameter("SPEED")))
            speed=Integer.valueOf(param).intValue();
        if(0 == speed)speed=200;
    
        if(null != (param=getParameter("RANDOMCOLOR")))
            randomColor=(0==Integer.valueOf(param).intValue())? false : true;
    
    
        randomColors[0]=Color.magenta;
        randomColors[1]=Color.orange;
        randomColors[2]=Color.red;
        randomColors[3]=Color.white;
        randomColors[4]=Color.yellow;
        randomColors[5]=Color.blue;
        randomColors[6]=Color.cyan;
        randomColors[7]=Color.green;
        randomColors[8]=Color.pink;
        randomColors[9]=Color.gray;

        jitterThread=new Thread(this);
    }

    public void start()
    {
        if(null != jitterThread) jitterThread.start();
    }

    public void stop()
    {
        if(null != jitterThread) jitterThread.stop();
        jitterThread = null;
    }

    public void run()
    {
        while (jitterThread != null) {
            try {Thread.sleep(200);} catch (InterruptedException e){}
                repaint();
        }
        System.exit(0);  
     }


    public boolean mouseDown(java.awt.Event evt, int x, int y)
    {
        if (threadSuspended) {
            jitterThread.resume();
            normal=false;
        }
        else {
            normal=true;
            repaint();
            jitterThread.suspend();
        }
        threadSuspended = !threadSuspended;
        return true;
    }


    ///////////////////
    // Added by W.Giel
    ///////////////////
    public void paint(Graphics g)
    {
        if(normal){
            g.setColor(bgColor);
            g.fillRect(0,0,size().width,size().height);
            g.setColor(textColor);
               g.setFont(font);
            g.drawString(s,(size().width-fm.stringWidth(s))/2, baseline);               
        }
    }
            
    
    public void update(Graphics g)
    {
      Color color;
    
        offScrGC.setColor(bgColor);
        offScrGC.fillRect(0,0,size().width,size().height);
        offScrGC.setColor(textColor);
        offScrGC.setFont(font);
        if(!normal){
            int x_coord=0;
            for(int i=0;i<s.length();i++){
                if(randomColor){
                    while(bgColor==(color=randomColors[Math.min(9,(int)(Math.random()*10))]));
                    offScrGC.setColor(color);
                }
                x_coord += (int)(Math.random()*10);
                int y_coord = baseline - (int)(Math.random()*10);
                String substr=s.substring(i,i+1);
                offScrGC.drawString(substr,x_coord,y_coord);
                x_coord += fm.stringWidth(substr);
            }
        }
        else offScrGC.drawString(s,(size().width-fm.stringWidth(s))/2, baseline);
        
        g.drawImage(offScrImage,0,0,this);
    }

    private Color parseColorString(String colorString)
    {
        if(colorString.length()==6){
            int R = Integer.valueOf(colorString.substring(0,2),16).intValue();
            int G = Integer.valueOf(colorString.substring(2,4),16).intValue();
            int B = Integer.valueOf(colorString.substring(4,6),16).intValue();
            return new Color(R,G,B);
        }
        else return Color.lightGray;
    }
}

