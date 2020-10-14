import java.awt.*;
import java.applet.*;
import java.net.URL;

public class g2d1 extends Applet {

      Graph2D graph;
      DataSet data1;
      DataSet data2;
      Axis    xaxis;
      Axis    yaxis;
      double data[];
      int np = 100;
      URL markers;


      public void init() {
        int i;
        int j;
        double data[] = new double[2*np];

        graph = new Graph2D();

        setLayout( new BorderLayout() );
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

        for(i=j=0; i<np; i++,j+=2) {
            data[j] = j-np;
            data[j+1] = 60000 * Math.pow( ((double)data[j]/(np-2) ), 3);
        }

        data2 = graph.loadDataSet(data, np);
        data2.linecolor   =  new Color(0,255,0);
        data2.marker      = 3;
        data2.markercolor = new Color(100,100,255);

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

}