import java.awt.*;    
import controls.*;
import java.util.Random;

/**
 *	This applet demonstrates the use of the PlotMixin 
 *	(c) 1996 Inductive Solutions, Inc. *
*/ 
public class ShowAllCharts extends java.applet.Applet { 
	PlotMixin	plotter;
	
        double  	x[] = new double[100];	// Temporary array for example of x and y values
        double  	y[] = new double[100];
	double  	z[] = new double[100];

	double		pieCategories[] = new double[4];
	PlotData	dataset, datasetToo;

	String		HTMLType = null;
	
	public void init() {
		plotter = new PlotMixin(this,size().width,size().height);
                int     i;

		HTMLType = getParameter("ChartType");

		if ( HTMLType.equalsIgnoreCase("THREEDPIE")) {
			/////////////////////////////////////
               	 	// Generate data for a 3d pie Chart
			/////////////////////////////////////
			
			plotter.setTitle("My Pie Chart");

		// CONVENTION: y[] contains the category numbers (0, 1, ...)
		//  x[] contains the POSITIVE VALUES, normalized as proportions for the Pie
		
			// A Four Category Pie Chart, all equally weighted
                	for ( i = 0; i < 4; i++ ) {
                        	x[i] = 25;
                        	pieCategories[i] = i;
                	}
                	dataset = new PlotData(x, pieCategories, 4);
                	dataset.type_of_plot = plotter.THREEDPIE;
			
                	// Category Colors:
			dataset.cv[0] = Color.yellow;
                	dataset.cv[1] = Color.blue;
                	dataset.cv[2] = Color.red;
                	dataset.cv[3] = Color.cyan;
			plotter.setXLabel(new String(""));
                        plotter.setYLabel(new String(""));
			plotter.addDataSet(dataset);

		}

		if ( HTMLType.equalsIgnoreCase("SCATTER")) {
			/////////////////////////////////////
                	// Generate data for a scatter plot
			/////////////////////////////////////
			plotter.setXLabel(new String("X-Axis"));
                        plotter.setYLabel(new String("Y-Axis"));
			plotter.setTickMarks(10,10,0,3,0,3);	
                        plotter.setTitle(new String("My Scatter Chart"));
		
		// CONVENTION: x[] and y[] contain values of horizontal and vertical axes

			// Generate 100 Gaussian Points that Should cluster around (0,0)
			// What about negative values?

			Random	myRandom = new Random();

                	for ( i = 0; i < 100; i++ ) {
                       	 	x[i] = myRandom.nextGaussian();
                        	y[i] = myRandom.nextGaussian();
                	}
			
                	dataset = new PlotData(x,y,100);
                	dataset.type_of_plot = plotter.SCATTER;

			// Color the Scatter Dots Red
			dataset.color = Color.red;
			dataset.label = new String ( "100 Gaussian X-Y Points" );
			plotter.addDataSet(dataset);
		}

		if ( HTMLType.equalsIgnoreCase("THREEDBAR")) {
			/////////////////////////////////////
                	// Generate data for a 3d bar chart
			/////////////////////////////////////
			plotter.setXLabel(new String("X-Axis"));
                        plotter.setYLabel(new String("Y-Axis"));

			String barCategories[] = {"1Q95","2Q95","3Q95","4Q95","1Q96","2Q96","3Q96","4Q96","1Q97*","2Q97*","3Q97*"};
        		plotter.setTickMarks(10,10, barCategories);

                        plotter.setTitle(new String("My 3D Bar Chart"));
		               
		// CONVENTION: x[] are categories; and y = f(x)

			// A Ten Category Bar Chart, ascending linearly: NB Negative Values
			for ( i = 0; i < 10; i++ ) {
                   	     	x[i] = i;
                     	   	y[i] = (2 * i ) - 10 ;
                	}
		
                	dataset = new PlotData(x,y,10);
                	dataset.type_of_plot = plotter.THREEDBAR;


                	dataset.cv[0] = Color.green;
                	dataset.cv[1] = Color.red;
               	 	dataset.cv[2] = Color.blue;
                	dataset.cv[3] = Color.yellow;
                	dataset.cv[4] = Color.green;
                	dataset.cv[5] = Color.white;
                	dataset.cv[6] = Color.red;
                	dataset.cv[7] = Color.blue;
                	dataset.cv[8] = Color.green;
                	dataset.cv[9] = Color.yellow;

                	dataset.cvl[0] = "Color.green";
                	dataset.cvl[1] = "Color.red";
               	 	dataset.cvl[2] = "Color.blue";
                	dataset.cvl[3] = "Color.yellow";
                	dataset.cvl[4] = "Color.green";
                	dataset.cvl[5] = "Color.white";
                	dataset.cvl[6] = "Color.red";
                	dataset.cvl[7] = "Color.blue";
                	dataset.cvl[8] = "Color.green";
                	dataset.cvl[9] = "Color.yellow";

			plotter.addDataSet(dataset);
		}

		if ( HTMLType.equalsIgnoreCase("LINE")) {
			/////////////////////////////////////
                	// Generate data for a line plot
			/////////////////////////////////////
			plotter.setXLabel(new String("X-Axis"));
                        plotter.setYLabel(new String("Y-Axis"));
			plotter.setTickMarks(10,10,0,2,0,2);	
                        plotter.setTitle(new String("My Line Chart"));

		// CONVENTION: x[] are real-valued categories and y[] =f(x).  
		// We interpolate a line between y values.
		
			// A 100-point XY Chart
		      	for ( i = 0; i < 100; i++ ) {
                        	x[i] = 0.1 * ((double)i);
                        	y[i] = Math.sin( x[i] );
                       		z[i] = Math.cos( x[i] );
			}                	
			dataset = new PlotData(x,y,100);
			dataset.type_of_plot = plotter.XY;
			dataset.color = Color.black;	// Color to draw the Interpolated Line
			dataset.label = new String ( "The Sin Function" );
			plotter.addDataSet(dataset);

			datasetToo = new PlotData(x,z,100);
			datasetToo.type_of_plot = plotter.XY;
			datasetToo.color = Color.red;	// Color to draw the Interpolated Line
			datasetToo.label = new String ( "The Cos Function" );
			plotter.addDataSet(datasetToo);


		}
	}  // end init

	
        public void start () {
		add(plotter);
		plotter.start();
        }

        public void stop () {
                plotter.stop();
        }
	public boolean mouseEnter(java.awt.Event evt, int x, int y) { 
		showStatus("(c) 1996 Inductive Solutions, Inc.");
		return (true);
    	}


}
