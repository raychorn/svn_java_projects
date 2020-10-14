package UK.ac.bris.ets.image.hot;

import java.lang.String;
import UK.ac.bris.ets.util.ETSException;

interface URLDialogCallback 
  {
  static final String rcsID="$Id: MappableImageTest.java 1.9 1995/10/27 09:20:27 joel Exp joel $";
  
  public void setURL(String theURL,String tag) throws ETSException;
  }      

