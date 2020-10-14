import java.awt.*;
import java.applet.*;
import java.net.URL;

public class g2d3 extends Applet {

      Graph2D graph;
      DataSet data1;
      DataSet data2;
      Axis    xaxis;
      Axis    yaxis;
      double data[];
      int np = 100;
      URL markers;
      Panel range;
      TextField xmin;
      TextField xmax;
      TextField ymin;
      TextField ymax;




      public void init() {
        int i;
        int j;
        double data[] = new double[2*np];
        Panel p = new Panel();
        

        graph = new Graph2D();
        range = new Panel();
        range.setLayout( new GridLayout(5,2,0,5) );

        range.add(new Label("Xmin"));
        xmin = new TextField(8);
        range.add(xmin);

        range.add(new Label("Xmax"));
        xmax = new TextField(8);
        range.add(xmax);

        range.add(new Label("Ymin"));
        ymin = new TextField(8);
        range.add(ymin);

        range.add(new Label("Ymax"));
        ymax = new TextField(8);
        range.add(ymax);

        range.add(new Button("Redraw"));
        p.add(range);


        setLayout( new BorderLayout() );
        add("East", p);
        add("Center", graph);
        try {
           markers = new URL(getDocumentBase(),"marker.txt");
        } catch(Exception e) {
           System.out.println("Failed to create Marker URL!");
        }
        if( !graph.loadMarkerFile(markers) ) {
           System.out.println("Failed to load Marker file!");
        }


        for(i=j=0; i<np; i++,j+=2) {
            data[j] = j-np;
            data[j+1] = 60000 * Math.pow( ((double)data[j]/(np-2) ), 2);
        }

        data1 = graph.loadDataSet(data,np);
        data1.linestyle = 0;
        data1.marker    = 1;
        data1.markerscale = 1.5;
        data1.markercolor = new Color(0,0,255);
        data1.dataLegend(200,20,"Data1");

        for(i=j=0; i<np; i++,j+=2) {
            data[j] = j-np;
            data[j+1] = 60000 * Math.pow( ((double)data[j]/(np-2) ), 3);
        }

        data2 = graph.loadDataSet(data, np);
        data2.linecolor   =  new Color(0,255,0);
        data2.marker      = 3;
        data2.markercolor = new Color(100,100,255);
        data2.dataLegend(200, 30,"Data2");

        xaxis = graph.createAxis(Axis.BOTTOM);
        xaxis.attachDataSet(data1);
        xaxis.attachDataSet(data2);
        xaxis.title = new String("Xaxis");
//        xaxis.title_color = Color.red;
//        xaxis.label_color = Color.red;


        yaxis = graph.createAxis(Axis.LEFT);
        yaxis.attachDataSet(data1);
        yaxis.attachDataSet(data2);
        yaxis.title = new String("Yaxis");
//        yaxis.title_color = Color.green; 
//        yaxis.label_color = Color.green;

      }

      public void paint(Graphics g) {
           graph.paint(g);
      }


      public boolean action(Event ev, Object arg) {
           if ("Redraw".equals(arg)) {
              

              if( xmin.getText() != null ) {
                  try {
                     xaxis.minimum = 
                     Double.valueOf(xmin.getText()).doubleValue();
                  } catch(Exception e) { }
              }
              if( xmax.getText() != null ) {
                  try {
                     xaxis.maximum = 
                     Double.valueOf(xmax.getText()).doubleValue();
                  } catch(Exception e) { }
              }
              if( ymin.getText() != null ) {
                  try {
                     yaxis.minimum = 
                     Double.valueOf(ymin.getText()).doubleValue();
                  } catch(Exception e) { }
              }
              if( ymax.getText() != null ) {
                  try {
                     yaxis.maximum = 
                     Double.valueOf(ymax.getText()).doubleValue();
                  } catch(Exception e) { }
              }

              graph.repaint();
              return true;
           } 
           return false;
      }

}