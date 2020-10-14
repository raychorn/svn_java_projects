import controls.*;
import java.awt.*;    
import java.util.Random;
import java.lang.Math;
/**
 *	This applet demonstrates the use of threads and the PlotMixin 
*	 (c) 1996 Inductive Solutions, Inc.
*/ 
public class ShowStockPrices extends java.applet.Applet implements Runnable { 
	PriceGenerator	myStockProcess; // Defined below
	PlotMixin       myStockChart = null;
	Thread		myThread = null;

  	public void init() {
		myStockProcess = new PriceGenerator("ACME Corp.",100,12,10,0.16,0.21);
		myStockChart = new PlotMixin(this,300,300);        
		myStockProcess.configureGraph(myStockChart);
                add(myStockChart);
  	}
    	public void start() {
                if ( myThread == null ) {
                        myThread = new Thread(this);
                        myThread.start();
                }
        }
	public void run() {
                myStockChart.start();
		myStockProcess.run();
	}
	
	public boolean mouseEnter(java.awt.Event evt, int x, int y) { 
		showStatus("(c) 1996 Inductive Solutions, Inc.");
		return (true);
    	}
} // end applet


class PriceGenerator extends Thread {
	// Generate stock prices via random walk statistics
	double	initial_price = 100.0;	// The initial price of the stock
	int	interval = 12;		// The interval
	int	nyears = 12;		// Number of years	
	double	m	= 0.15;		// Expected Return
	double	s	= 0.20;		// Standard Deviation
	Double	M	= null;
	Double	S	= null;
	double	values[] = null;
        PlotMixin       plotter = null;
        PlotData        dataset = null;
	String		symbol = null;	
	public PriceGenerator (	String sy, double initial_price, int interval, 
				int nyears, double m, double s ) {
		symbol = new String(sy);
		this.initial_price = initial_price;
		this.interval = interval;	
		this.nyears = nyears;	
		this.m = m;
		this.s = s;
		values = new double[interval * nyears];
	}
	public void configureGraph(PlotMixin plotter) {
		this.plotter = plotter;
                dataset = new PlotData();
                dataset.type_of_plot = plotter.XY;
                dataset.color = Color.black;
		plotter.setTickMarks(10,10);
		plotter.setXLabel(new String("Time (months)"));
                plotter.setYLabel(new String("Price $"));

		plotter.addDataSet(dataset);
		M = new Double(m);
		S = new Double(s);
                plotter.setTitle(symbol + "Price Chart");
	}
	public void run() {
		Random	myRandom = new Random();
		values[0] = initial_price;
		for ( int ny = 0; ny < nyears; ny++ ) {
			for ( int i = 0; i < interval; i++ ) {
				if ( (ny == 0) & ( i == 0 ) ) 
					continue;
				int	index = (ny * interval) + i;
				double  deltat = 1.0 / (double) interval;
				values[index] = values[index-1] + m * values[index-1] * deltat + s * values[index-1] * Math.sqrt(deltat) * myRandom.nextGaussian();
				System.out.println(values[index]);
				dataset.update(values,index+1);
			}
		}
	}
}
