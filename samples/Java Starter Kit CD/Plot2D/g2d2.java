import java.awt.*;
import java.applet.*;
import java.net.URL;

public class g2d2 extends Applet {

      Graph2D graph;
      Label title;
      DataSet data1;
      Axis    xaxis;
      Axis    yaxis;


      public void init() {
        int i;
        int j;

        graph = new Graph2D();

        graph.setFont(new Font("TimesRoman",Font.PLAIN,16));
        
        title = new Label(
        "Spectrum of a giant elliptical Galaxy in the Virgo cluster",
        Label.CENTER);

        title.setFont(new Font("TimesRoman",Font.PLAIN,16));

        setLayout( new BorderLayout() );
        add("North",  title);
        add("Center", graph);


        try {
        data1 = graph.loadFile(new URL(getDocumentBase(),"elliptical.data"));
        } catch (Exception e) {
          System.out.println("Failed to load data file!");
        }

        data1.linecolor = new Color(255,255,0);

        xaxis = graph.createAxis(Axis.BOTTOM);
        xaxis.attachDataSet(data1);
        xaxis.title = new String("Wavelength (angstroms)");
        xaxis.title_color = Color.magenta;


        yaxis = graph.createAxis(Axis.LEFT);
        yaxis.attachDataSet(data1);
        yaxis.title = new String("Flux");
        yaxis.title_color = Color.magenta; 

      }

      public void paint(Graphics g) {
           graph.paint(g);
      }

}
