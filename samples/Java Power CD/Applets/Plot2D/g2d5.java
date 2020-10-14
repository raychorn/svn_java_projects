import java.awt.*;
import java.applet.*;
import java.net.URL;

public class g2d5 extends Applet {

      Graph2D graph;
      DataSet data1;
      Axis    xaxis;
      Axis    yaxis;
      TextField period;
      TextField amplitude;
      int  period_new    = 1;
      int  amplitude_new = 1;
      Panel range;
      String base = "http://www-igpp.llnl.gov/leigh-cgi/g2d5.pl";

 

      public void init() {
        int i;
        int j;
        Panel p = new Panel();
        graph = new Graph2D();

        range = new Panel();
        range.setLayout( new GridLayout(3,2,0,5) );

        range.add(new Label("Period"));
        period = new TextField(8);
        range.add(period);

        range.add(new Label("Amplitude"));
        amplitude = new TextField(8);
        range.add(amplitude);
        range.add(new Button("Redraw"));
        p.add(range);


        setLayout( new BorderLayout() );
        add("East", p);
        add("Center", graph);

        graph.setFont(new Font("TimesRoman",Font.PLAIN,16));
        

        try {
        data1 = graph.loadFile(buildURL());
        } catch (Exception e) {
          System.out.println("Failed to load data file!");
        }

        data1.linecolor = new Color(255,255,0);

        xaxis = graph.createAxis(Axis.BOTTOM);
        xaxis.attachDataSet(data1);
        xaxis.title = new String("Period");
        xaxis.title_color = Color.magenta;


        yaxis = graph.createAxis(Axis.LEFT);
        yaxis.attachDataSet(data1);
        yaxis.title = new String("Amplitude");
        yaxis.title_offset = new Dimension(-10,-10);
        yaxis.title_color = Color.magenta; 

      }

      public void paint(Graphics g) {
           graph.paint(g);
      }


      public URL buildURL() {
            URL url;

            StringBuffer s = new StringBuffer(base);
            s.append("?amplitude=");
            s.append(amplitude_new);
            s.append("&period=");
            s.append(period_new);

            try {
                  url = new URL(s.toString());
            } catch (Exception e) {
                  url = null;
            }

            return url;
      }

      public boolean action(Event ev, Object arg) {
           int i;

           if ("Redraw".equals(arg)) {



              if( amplitude.getText() == null &&
                  period.getText() == null ) { return true; }

             if( amplitude.getText() != null ) {
                  try {
                     amplitude_new = Integer.parseInt(amplitude.getText());
                  } catch(Exception e) { }
              }
             if( period.getText() != null ) {
                  try {
                     period_new = Integer.parseInt(period.getText());
                  } catch(Exception e) { }
              }


              graph.deleteDataSet(data1);
              data1 = graph.loadFile( buildURL() );
              xaxis.attachDataSet(data1);
              yaxis.attachDataSet(data1);
              data1.linecolor = new Color(255,255,0);


              graph.repaint();
              return true;
           } 

           return false;
     }



}
