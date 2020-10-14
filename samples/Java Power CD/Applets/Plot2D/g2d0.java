import java.awt.*;
import java.applet.*;

public class g2d0 extends Applet {

      Graph2D graph;
      DataSet data1;
      DataSet data2;
      DataSet data3;
      DataSet data4;

      public void init() {
        int i;
        int j;

        graph = new Graph2D();

        setLayout( new BorderLayout() );
        add("Center", graph);

        graph.framecolor = new Color(145,120,255);
        graph.border = 5;

        data1 = gaussian(1.0, 1.0, .5);  
        data1.linecolor = new Color(72,118,255);
        data1.ymax = 1.0;

        data2 = gaussian(0.75, 1.0, .5);
        data2.linecolor   =  new Color(67,110,238);
        data2.ymax = 1.0;

        data3 = gaussian(0.5, 1.0, .5);
        data3.linecolor   =  new Color(58,95,205);
        data3.ymax = 1.0;

        data4 = gaussian(0.25, 1.0, .5);
        data4.linecolor   =  new Color(39,64,139);
        data4.ymax = 1.0;

      }

      public void paint(Graphics g) {
           graph.paint(g);
      }


      public DataSet gaussian(double amplitude, double range, double scale) {
            int i, j;
            int np = 100;
            double data[] = new double[2*np];
            double x, y;

            for(i=j=0; i<np; i++,j+=2) {
                x = (i-np/2)*range/(np/2);
                y = -x*x/(scale*scale);
                data[j] = x;
                data[j+1] = amplitude * Math.exp(y);
            }
            
            return graph.loadDataSet(data,np);


      }
}
