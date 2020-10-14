import java.net.URL;
import java.net.MalformedURLException;

public class goURL extends java.applet.Applet
{
  URL homepage;
  public void init()
    {
      try {
	homepage=new URL("http://www.edu.isy.liu.se/~d94nilhe/");
      }catch (MalformedURLException e){/*Write your error messages here*/}
      getAppletContext().showDocument(homepage);
    }
}
