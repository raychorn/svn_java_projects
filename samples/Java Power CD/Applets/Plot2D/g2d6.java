import java.awt.*;
import java.applet.*;
import java.net.URL;

public class g2d6 extends Applet {
      Controls control;
      Button   show;
      Button   redraw;

      String base = "http://www-igpp.llnl.gov/leigh-cgi/driver";

      String   material = "stone";
      String   plot     = "velocity";
      String   height   = "100";
      String   radius   = "30";
      String   velocity = "15";
      String   trajectory = "45";

      G2Dint   graph;
      Axis     xaxis;
      Axis     yaxis;
      DataSet  data1;


      public void init() {
         Rectangle r = bounds();
         Panel p = new Panel();
         control = new Controls();
         control.resize(550,150);
         control.setTitle("Controls");
         control.setHeight(height);
         control.setRadius(radius);
         control.setVelocity(velocity);
         control.setTrajectory(trajectory);
         show = new Button("Show Controls");
         redraw =  new Button("Calculate");
         p.add(show);
         p.add(redraw);
      


         graph = new G2Dint();

         setLayout( new BorderLayout() );
         add("South", p);
         add("Center", graph);

         graph.setFont(new Font("TimesRoman",Font.PLAIN,16));

         xaxis = graph.createXAxis();
         xaxis.title_color = Color.magenta;
         xaxis.minor_tic_count = 4;

         yaxis = graph.createYAxis();
         yaxis.title = new String("Altitude (km)");
         yaxis.title_offset = new Dimension(-10,-10);
         yaxis.title_color = Color.magenta;
         yaxis.minor_tic_count = 4;


         getData();


      }

      public URL buildURL() {
            URL url;
            String t;

            StringBuffer s = new StringBuffer(base);
            s.append("?material=");
            s.append(control.getMaterial());
            s.append("&plot=");
            s.append(control.getPlot());
            s.append("&velocity=");
            s.append(control.getVelocity());
            s.append("&radius=");
            s.append(control.getRadius());
            s.append("&height=");
            s.append(control.getHeight());
            s.append("&trajectory=");
            s.append(control.getTrajectory());


            try {
                  url = new URL(s.toString());
            } catch (Exception e) {
                  url = null;
            }

            return url;
      }

      public void getData() {
              String s = control.getPlot();

              graph.deleteDataSet(data1);
              data1 = graph.loadFile( buildURL() );
              xaxis.attachDataSet(data1);
              yaxis.attachDataSet(data1);
              data1.linecolor = new Color(255,255,0);


              if( s.equals("velocity") ) {
                           xaxis.title = "Velocity (km/sec)";
              }
              else
              if( s.equals("mass") ) {
                           xaxis.title = "Mass (kg)";
              }
              else
              if( s.equals("trajectory") ) {                                  
                           xaxis.title = "Trajectory (Degrees)";
              }
              else
              if( s.equals("radius") ) {
                           xaxis.title = "Radius (m)"; 
              }
              else
              if( s.equals("energy1") ) {
                           xaxis.title = "Cumulative Energy Deposition (MT)"; 

              }
              else
              if( s.equals("energy2") ){
                           xaxis.title = "Energy Deposition (MT/km)"; 
          }





      }

      public boolean action(Event ev, Object arg) {
           if ("Calculate".equals(arg)) {
              getData();              
              graph.repaint();
              return true;
           } else
           if ("Show Controls".equals(arg)) {
                control.show();
                show.setLabel("Hide Controls");
                return true;
           } else
           if ("Hide Controls".equals(arg)) {
                control.hide();
                show.setLabel("Show Controls");
                return true;
           }

           return false;
      }


}



class Controls extends Frame {
 
       CheckboxGroup group1    = new CheckboxGroup();
       CheckboxGroup group2    = new CheckboxGroup();
       TextField TFheight     = new TextField(6);
       TextField TFradius     = new TextField(6);
       TextField TFvelocity   = new TextField(6);
       TextField TFtrajectory = new TextField(6);

       String Lvelocity    = "Velocity";     
       String Lmass        = "Mass";     
       String Ltrajectory  = "Trajectory";
       String Lradius      = "Radius";    
       String Lenergy1     = "Energy";     
       String Lenergy2     = "Energy/km";     

       String Liron        = "Iron";
       String Lstone       = "Stone";
       String Lchondrite   = "Chondrite";
       String Lice         = "Ice";

       public Controls() {
             setFont(new Font("TimesRoman", Font.PLAIN, 14));

             Panel p1 = new Panel();
             p1.setLayout( new GridLayout(4,1,0,0) );
             p1.add( new Checkbox(Liron,      group1, false) );
             p1.add( new Checkbox(Lstone,     group1, true)  );
             p1.add( new Checkbox(Lchondrite, group1, false) );
             p1.add( new Checkbox(Lice,       group1, false) );

             Panel p3 = new Panel();
             p3.setLayout( new GridLayout(6,1,0,0) );

             p3.add( new Checkbox(Lvelocity,           group2, false) );
             p3.add( new Checkbox(Lmass,               group2, false) );
             p3.add( new Checkbox(Ltrajectory,         group2, false) );
             p3.add( new Checkbox(Lradius,             group2, false) );
             p3.add( new Checkbox(Lenergy1,            group2, false) );
             p3.add( new Checkbox(Lenergy2,            group2, true) );

             Panel p2 = new Panel();
             p2.setLayout( new GridLayout(4,2,0,0) );
      
             p2.add( TFheight ); 
             p2.add(new Label("Height (km)"));
   
             p2.add( TFradius );
             p2.add(new Label("Radius (m)"));       
    
             p2.add( TFvelocity );
             p2.add(new Label("Velocity (km/sec)"));       
    
             p2.add( TFtrajectory );
             p2.add(new Label("Trajectory (Degrees)"));       


             setLayout( new FlowLayout() );
             add(p1);
             add(p2);
             add(p3);

       }


       public String getMaterial() {
          String s = group1.getCurrent().getLabel();
          if( s.equals(Liron) ) {
                                 return "iron";
          }
          else
          if( s.equals(Lstone) ) {
                                 return "stone";
          }
          else
          if( s.equals(Lchondrite) ) {
                                 return "chondrite";
          }
          else
          if( s.equals(Lice) ) {
                                 return "ice";
          }
          else {
                                 return "stone";
          }
       }

       public String getPlot() {
          String s = group2.getCurrent().getLabel();
          if( s.equals(Lvelocity) ) {
                                  return "velocity";
          }
          else
          if( s.equals(Lmass) ) {
                                  return "mass";
          }
          else
          if( s.equals(Ltrajectory) ) {                                  
                                  return "trajectory";
          }
          else
          if( s.equals(Lradius) ) {
                                  return "radius";
          }
          else
          if( s.equals(Lenergy1) ) {
                                  return "energy1";
          }
          else
          if( s.equals(Lenergy2) ) {
                                  return "energy2";
          }
          else {
                                  return "energy2";
          }
       }

       public void setHeight(String s) {
            TFheight.setText(s);
       }

       public void setRadius(String s) {
            TFradius.setText(s);
       }

       public void setVelocity(String s) {
            TFvelocity.setText(s);
       }

       public void setTrajectory(String s) {
            TFtrajectory.setText(s);
       }

       public String getHeight() {
          double d;
          String s = TFheight.getText();
          try {
             d = Double.valueOf(s).doubleValue();
          } catch(Exception e) {
             s = null;
          }
          return s;
       }

       public String getRadius() {
          double d;
          String s = TFradius.getText();
          try {
             d = Double.valueOf(s).doubleValue();
          } catch(Exception e) {
             s = null;
          }
          return s;
       } 
       public String getVelocity() {
          double d;
          String s = TFvelocity.getText();
          try {
             d = Double.valueOf(s).doubleValue();
          } catch(Exception e) {
             s = null;
          }
          return s;
       }
       public String getTrajectory() {
          double d;
          String s = TFtrajectory.getText();
          try {
             d = Double.valueOf(s).doubleValue();
          } catch(Exception e) {
             s = null;
          }
          return s;
       } 



}