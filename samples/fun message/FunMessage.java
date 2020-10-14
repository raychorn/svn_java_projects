
import java.awt.*;
import java.applet.Applet;
import java.applet.AppletContext; 
import java.applet.AudioClip;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Vector;
import java.awt.Toolkit;




public class FunMessage extends Applet implements Runnable {
    
	/*Internal font*/
	Font mFont;


	/*Font color values*/
	int rr=-1,gg=-1,bb=-1;
	/*status*/
	boolean m_puja=true,m_canvi=false;
	/*repaint in black...*/
	boolean m_pintaNegre=true; /*Fa que es repinti en negre...*/ 
	/*messages to be displayed*/
	Vector m_messages;
    /*current message index*/
	int m_valorActual=-1; /*Valor de l'index del missatge actual*/
	/*my thread*/
    Thread engine = null;
	/*pause within messages*/
	int m_pause;
	/*Es aleatori o sequencial...*/
	boolean m_random;

    public String getAppletInfo() {
	return "FunMessage by DJL";
    }

    

	 



    /**
     * Parameter Info
     */
    public String[][] getParameterInfo() {
	String[][] info = {
	    {"messsages", 	"string", 		"messages to be displayed"},
		{"pause", 	"int", 		"pause within messages"},
		{"random", 	"string", 		"Is random or not"}

	   
	};
	return info;
    }



    


    
    public void init() {


       String param = getParameter("messages");
	   m_messages=parseStrings( param );	


	   param= getParameter("pause");
	   if (param==null)
	   {
			m_pause=2000; /*2 seconds by default*/
	   }
	   else
	   {
			m_pause = Integer.parseInt(param);
	   }

	   param= getParameter("random");
	   if (param==null)
	   {
			m_random=true; /*true by default*/
	   }
	   else
	   {
		 if (param.equals("true") )
		 {
			m_random=true;
		 }
		 else
		 {
			m_random=false;
		 }
		 
	   }


	   mFont=new Font( "Arial",Font.BOLD,(int)(bounds().height*0.7));


	
	


    } /*fi init*/

  

    

    /**
     * Run the animation. This method is called by class Thread.
     * @see java.lang.Thread
     */
    public void run() {
	Thread me = Thread.currentThread();

	me.setPriority(Thread.MIN_PRIORITY);

/******/
	while ( engine == me ) {
	
			
		
		    
		
            try
			{
			/*Possa el thread a dormir x ms */
			
			
			if (m_canvi==true)
			{
				
				me.sleep(m_pause); /*pausa entre els missatges*/
			}
			else
			{
				
				me.sleep(50);
			}



			}catch ( InterruptedException e )
			{

			}
			m_pintaNegre=false;
			repaint();
			m_pintaNegre=true;
			/*Fa que només es pinti negre quan es repinta 
			de manera No forçada per aquest thread*/

				

	
	    }

		/****/



	
    }


    /**
     * Paint the current frame.
     */
    public void paint(Graphics g) {
        


		m_canvi=false;


		Color tmpColor=new Color(rr,gg,bb);
		if (m_puja==true)
		{
			rr=rr+20;
			gg=gg+20;
			bb=bb+20;
		}
		else
		{
			rr=rr-20;
			gg=gg-20;
			bb=bb-20;

		}

		if (rr>230) m_puja=false;
		if (rr<20)
		{
			m_puja=true;
			m_canvi=true; /*canviar de missatge...*/
			if (m_random)
			{
				int ant=m_valorActual;
				while( m_valorActual== ant )
				{
					m_valorActual=(int)(Math.random()*m_messages.size());
				}
			}
			else
			{
				m_valorActual=m_valorActual+1;
				if ( m_valorActual==m_messages.size())
				{
					m_valorActual=0;
				}

			}
		}

		/*getAppletContext().showStatus( str );*/
        g.setFont( mFont );
		

		FontMetrics fm=g.getFontMetrics(mFont);
		
		/*Amplada del text en pixels*/

		int ample=fm.stringWidth( (String)m_messages.elementAt(m_valorActual) );
		
		if (m_pintaNegre==true )
		{
			g.setColor(Color.black);
			g.fillRect(0,0,bounds().width,bounds().height);
			return;
		}

		if (m_canvi==true )
		{
			g.setColor(Color.black);
			g.fillRect(0,0,bounds().width,bounds().height);
			/*getAppletContext().showStatus( "Pintant en negre" );*/

		}
		
		
        if (m_canvi==false)
		{

			g.setColor(tmpColor);

			/*just centrat al mig*/
			g.drawString((String)m_messages.elementAt(m_valorActual),(bounds().width-ample)/2,(bounds().height)/2 +8);
        }
		
		
    }

    /**
     * Start the applet by forking an animation thread.
     */
    public void start() {
	if (engine == null) {
	    engine = new Thread(this);
	    engine.start();
	}
    }


    /*Sobrecargo el update
	no es borra abans de dibuixar...
	per evitar el flickering*/

	public void update(Graphics g) {
		/*g.clearRect(0, 0, width, height);*/
		paint(g);
    }


    /**
     * Stop the insanity, um, applet.
     */
    public void stop() {
	if (engine != null && engine.isAlive()) {


	    engine.stop();
	}
	
	engine = null;
    }
    
	/*Funcio per parsear un string amb |...*/

    Vector parseStrings(String attr) {
	Vector result = new Vector(10);
	for (int i = 0; i < attr.length(); ) {
	    int next = attr.indexOf('|', i);
	    if (next == -1) next = attr.length();
	    String file = attr.substring(i, next);
	    result.addElement(file);
	    i = next + 1;
	}
	return result;
    }

	void showParseError(Exception e) {
	String errorMsg = "SimpleSound: Parse error: "+e;
	showStatus(errorMsg);
	System.err.println(errorMsg);
    }



   
    
}

class ParseException extends Exception {
    ParseException(String s) {
	super(s);
    }
}







