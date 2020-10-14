import java.applet.*;
import java.awt.*;

public class CommissionsAppBeta extends Applet {
	final int number_of_brokers = 33;
	final int THRESHOLD = 32000;
	TextField price_obj;
	TextField shares_obj;
	TextField[] values;
	Label[] brokers;
	Label[] phones;
	Checkbox is_stock;
	Checkbox is_option;
	CheckboxGroup stock_option;
	String[] names;
	int[] names_index;
	double[] commisions;
	public void init() {
		int i, j, k;
		String label_name1, label_name2;
		values = new TextField[number_of_brokers];
		brokers = new Label[number_of_brokers];
		phones = new Label[number_of_brokers];
		names = new String[number_of_brokers];
		names_index = new int[number_of_brokers];
		commisions = new double[number_of_brokers];
		stock_option = new CheckboxGroup();
		names[0]  = "Andrew Peck            1-800-221-5873";
		names[1]  = "Aufhauser              1-800-368-3668";
		names[2]  = "Barry Murphy           1-800-221-2111";
		names[3]  = "Brown                  1-800-822-2021";
		names[4]  = "ETrade                 1-800-786-2575";
		names[5]  = "Jack White             1-800-909-6777";
		names[6]  = "Kennedy Cabot          1-800-252-0090";
		names[7]  = "Lombard                1-800-688-3462";
		names[8]  = "Muriel Siebert         1-800-995-7880";
		names[9]  = "National Discount      1-800-888-3999";
		names[10] = "Pacific Brokerage      1-800-421-8395";
		names[11] = "Scottsdale             1-800-888-1980";
		names[12] = "Stock Cross            1-800-225-6196";
		names[13] = "York Securities        1-800-221-3154";
		names[14] = "Ceres Securities       1-800-669-3900";
		names[15] = "Washington             1-800-843-9838";
		names[16] = "Midwood                1-800-643-9663";
		names[17] = "RJ Forbes              1-800-754-7687";
		names[18] = "RTG Richards           1-800-285-3500";
		names[19] = "Regal                  1-800-786-9000";
		names[20] = "Wall Street            1-800-487-2339";
                names[21] = "Accutrade              1-800-882-4887"; 
                names[22] = "WhiteHall              1-800-223-5023"; 
                names[23] = "J Boxford              1-800-656-1776"; 
                names[24] = "Newport                1-800-999-3278"; 
                names[25] = "Bidwell                1-800-547-6337"; 
                names[26] = "Charles Schwab         1-800-442-5111";
                names[27] = "Fidelity               1-800-544-7272"; 
                names[28] = "Fleet                  1-800-221-8210"; 
                names[29] = "Olde                   1-800-872-6533";
		names[30] = "Marsh Block            1-800-366-1500";
		names[31] = "Quick & Reilly         1-800-456-4049";
		names[32] = "Vanguard               1-800-662-7447";
		add("Is_stock", is_stock = new Checkbox("Stock or..."));
		add("Is_option", is_option = new Checkbox("Option trade?"));
		add("PriceLabel", new Label("How much is the price per unit? (in $.c)"));
		add("Price", price_obj = new TextField("", 10));
		add("SharesLabel", new Label("How many units to trade?"));
		add("Shares", shares_obj = new TextField("", 10));
		is_stock.setCheckboxGroup(stock_option);
		is_option.setCheckboxGroup(stock_option);
		stock_option.setCurrent(is_stock);
		is_stock.setState(true);
		for(i = 0; i < number_of_brokers; i++) {
			label_name1 = "broker".concat(new Integer(i).toString());
			add(label_name1, brokers[i] = new Label(""));
			label_name2 = label_name1.concat("phone");
			add(label_name2, phones[i] = new Label(""));
			label_name2 = label_name1.concat("values");
			add(label_name2, values[i] = new TextField("", 10));
		}
                for(i = 0; i < number_of_brokers; i++) {
                        names_index[i] = i;
                        commisions[i] = -1.0f;
                }
                for(i = 0; i < number_of_brokers; i++)
                        for(j = number_of_brokers - 1; i < j; j--)
                       		if(names[names_index[j]].compareTo(names[names_index[i]]) < 0) {
                                        k = names_index[i];
                                        names_index[i] = names_index[j];
                                        names_index[j] = k;
                                }
                for(i = 0; i < number_of_brokers; i++)
                        values[i].setText(" ");
                for(i = 0; i < number_of_brokers; i++) {
                        brokers[i].setText(names[names_index[i]].substring(0, 18));
                        phones[i].setText(names[names_index[i]].substring(23, 37));
		}
	}
	public synchronized void layout() {
		int i;
		resize(500, 110 + 30 * number_of_brokers);
		if(0 < countComponents()) {
			getComponent(0).reshape(150, 10, 100, 30);
			getComponent(1).reshape(250, 10, 110, 30);
			getComponent(2).reshape(75, 40, 200, 30);
			getComponent(3).reshape(330, 40, 80, 30);
			getComponent(4).reshape(75, 70, 300, 30);
			getComponent(5).reshape(330, 70, 80, 30);
			for(i = 0; i < number_of_brokers; i++) {
				getComponent(3 * i + 6).reshape(75, 110 + 30 * i, 125, 30);
				getComponent(3 * i + 7).reshape(200, 110 + 30 * i, 150, 30);
				getComponent(3 * i + 8).reshape(330, 110 + 30 * i, 80, 30);
			}
		}
	}
	public void compute() {
		int i, j, k;
		for(i = 0; i < number_of_brokers; i++) {
			names_index[i] = i;
			commisions[i] = -1.0;
		}
		if(stock_option.getCurrent() == is_stock)
			compute_stock();
		if(stock_option.getCurrent() == is_option)
			compute_options();
		for(i = 0; i < number_of_brokers; i++)
			for(j = number_of_brokers - 1; i < j; j--)
				if(names[names_index[j]].compareTo(names[names_index[i]]) < 0) {
					k = names_index[i];
					names_index[i] = names_index[j];
					names_index[j] = k;
				}
		for(i = 0; i < number_of_brokers; i++)
			if(commisions[names_index[i]] < 0.0f)
				values[i].setText(" ");
			else
				values[i].setText(new Double(commisions[names_index[i]]).toString());
	}
	public void compute_stock() {
        	double price, shares;
		double fee, princ, min, max;
		int i;
		try {
			price_obj.setText(price_obj.getText().replace(',', '.'));
			shares_obj.setText(shares_obj.getText().replace(',', '.'));
			price = Double.valueOf(price_obj.getText()).doubleValue();
			shares = Double.valueOf(shares_obj.getText()).doubleValue();

			{
			  princ = shares * price;
			  if(princ < THRESHOLD) {
			    fee = 40.0f;
			    if(shares < 500.0f)
			      fee = fee + 0.08f * shares;
			    if((500.0f <= shares) && (shares < 1000.0f))
			      fee = fee + 0.065f * shares;
			    if((1000.0f <= shares) && (shares < 2500.0f))
			      fee = fee + 0.05f * shares;
			    if(2500.0f <= shares)
			      fee = fee + 0.04f * shares;
			  } else {
			    fee = 0.03f * shares;
			    if(fee < 100.0f)
			      fee = 100.0f;
			  };
			  commisions[0]= fee;
			}
			
			{
			  princ = shares * price;
			  if(princ < THRESHOLD) {
			    if(price < 1.0f)
			      fee = 25.0f + 0.03f * shares;
			    else {
			      if(shares < 400.0f)
				fee = 24.99f;
			      if((400.0f <= shares) && (shares < 500.0f))
				fee = 34.99f;
			      if((500.0f <= shares) && (shares < 600.0f)) {
				fee = 40.99f;
				if(30.0f < price)
				  fee = fee - 1.0f;
			      };
			      if((600.0f <= shares) && (shares < 700.0f)) {
				fee = 49.0f;
				if(price < 10.0f)
				  fee = fee - 3.0f;
				if(price < 5.0f)
				  fee = fee - 4.01f;
			      };
			      if((700.0f <= shares) && (shares < 800)) {
				fee = 52.99f;
				if(price < 50.0f)
				  fee = fee - 2.0f;
				if(price < 20.0f)
				  fee = fee - 1.0f;
				if(price < 10.0f)
				  fee = fee - 2.0f;
				if(price < 5.0f)
				  fee = fee - 6.0f;
			      };
			      if((800 <= shares) && (shares < 900.0f)) {
				fee = 59.0f;
				if(price < 10.0f)
				  fee = fee - 6.0f;
				if(price < 5.0f)
				  fee = fee - 11.01f;
			      };
			      if((900.0f <= shares) && (shares < 1000.0f)) {
				fee = 63.0f;
				if(price < 20.0f)
				  fee = fee - 4.0f;
				if(price < 10.0f)
				  fee = fee - 6.0f;
				if(price < 5.0f)
				  fee = fee - 11.0f;
			      };
			      if((1000.0f <= shares) && (shares <= 5000.0f)) {
				fee = 70.0f;
				if(price < 30.0f)
				  fee = fee - 2.0f;
				if(price < 20.0f)
				  fee = fee - 9.0f;
				if(price < 10.0f)
				  fee = fee - 6.0f;
				if(price < 5.0f)
				  fee = fee - 11.0f;
				fee = fee + 0.066667f * (shares - 1000.0f);
			      };
			      if(5000.0f < shares)
				fee = 0.03f * shares;
			    };
			  } else {
			    fee = 0.02f * shares;
			    if(fee < 34.0f)
			      fee = 34.0f;
			  };
			  fee = fee + 2.5f;
			  commisions[1]= fee;
			}
			
			{
			  double[] prices = {1.0f, 
						3.0f,
						10.0f,
						20.0f,
						25.0f,
						30.0f,
						99999999999.9f
					    };
			  double[] costs = {0.01f,
					       0.02f,
					       0.035f,
					       0.045f,
					       0.055f,
					       0.065f,
					       0.075f
					   };
			  for(i = 0; i < 7; i++)
			    if(price < prices[i])
			      break;
			  fee = 25.0f + shares * costs[i];
			  fee = fee + 2.0f;
			  commisions[2]= fee;
			}
			
			{
			  fee = 29.0f;
			  if (5000.0f < shares)
			    fee = fee + 0.01f * shares;
			  commisions[3]= fee;
			}
			
			{
			  fee = 25.0f;
			  if(5000.0f < shares)
			    fee = fee + 0.0075f * shares;
			  commisions[4]= fee;
			}
			
			{
			  fee = 33.0f;
			  if(shares < 2000.0f)
			    fee = fee + 0.03f * shares;
			  else 
			    fee = fee + 0.02f * shares;
			  commisions[5]= fee;
			}
			
			{
			  if(shares < 100.0f)
			    fee = 20.0f;
			  if((100.0f <= shares) && (shares < 2000.0f))
			    fee = 0.05f * shares;
			  if((2000.0f <= shares) && (shares < 5000.0f))
			    fee = 0.03f * shares;
			  if(5000.0f <= shares)
			    fee = 0.02f * shares;
			  if((fee < 30.0f) && (100.0f <= shares))
			    fee = 30.0f;
			  fee = fee + 3.0f;
			  commisions[6]= fee;
			}
			
			{
			  if(price < 1.0f)
			    fee = 25.0f + 0.0275f * shares * price;
			  else {
			    fee = 0.02f * shares;
			    if(fee < 34.0f)
			      fee = 34.0f;
			  };
			  fee = fee + 2.5f;
			  commisions[7]= fee;
			}
			
			{
			  double base, rate;
			  double[] principles = {2500.0f,
						    6000.0f,
						    22000.0f,
						    50000.0f,
						    500000.0f,
						    9999999999999.9f
						};
			  double[] bases = {21.0f,
					       36.0f,
					       56.0f,
					       73.0f,
					       114.0f,
					       194.0f
					   };
			  double[] perc = {0.0132f,
					      0.0042f,
					      0.0023f,
					      0.0016f,
					      0.0008f,
					      0.0006f
					  };
			  princ = shares * price;
			  if(princ < THRESHOLD) {
			    for(i = 0; i < 6; i++)
			      if(principles[i] < princ)
				continue;
			      else
				break;
			    base = bases[i] + perc[i] * princ;
			    if(shares <= 1000.0f)
			      min = 0.057f * shares;
			    else
			      min = 0.057f * 1000.0f + (shares - 1000.0f) * 0.028f;
			    if(shares <= 100.0f)
			      max = 0.45f * shares;
			    else
			      max = 0.45f * 100.0f + (shares - 100.0f) * 0.47f;
			    fee = base;
			    if(fee < min)
			      fee = min;
			    if(max < fee)
			      fee = max;
			    if(fee < 37.5f)
			      fee = 37.5f;
			  } else {
			    fee = shares * 0.03f;
			    if(fee < 75.0f)
			      fee = 75.0f;
			  };
			  commisions[8]= fee;
			}
			
			{
			  fee = 30.0f;
			  if(5000.0f < shares) 
			    fee = fee + 0.01f * shares;
			  fee = fee + 3.0f;
			  commisions[9]= fee;
			}
			
			{
			  fee = 25.0f;
			  if(1000.0f < shares)
			    fee = fee + 0.01f * (shares - 1000.0f);
			  fee = fee + 4.0f;
			  commisions[10]= fee;
			}
			
			{
			  princ = shares * price;
			  if(princ < THRESHOLD) {
			    fee = 35.0f;
			    if(price < 1.0f)
			      fee = fee + 0.025f * price * shares;
			    else {
			      if(1000.0f < shares) {
				fee = fee + 0.04f * (shares - 1000.0f);
				shares = 1000.0f;
			      };
			      if(price <= 10.0f) {
				if(300.0f < shares)
				  fee = fee + 0.05f * (shares - 300.0f);
			      } else {
				if(100.0f < shares)
				  fee = fee + 0.05f * (shares - 100.0f);
			      };
			    };
			    fee = fee * 0.90f;
			  } else {
			    fee = 50.0f;
			    if(1000.0f < shares)
			      fee = fee + 0.025f * (shares - 1000.0f);
			  };
			  commisions[11]= fee;
			}
			
			{
			  fee = 25.0f + 0.085f * shares;
			  commisions[12]= fee;
			}
			
			{
			  fee = 33.0f + 0.02f * shares;
			  commisions[13]= fee;
			}
			
			{
			  fee = 18.0f;
			  commisions[14]= fee;
			}
			
			{
			  fee = 25.0f;
			  if(5000.0f < shares)
			    fee = fee + 0.01f * shares;
			  fee = fee + 3.0f;
			  commisions[15]= fee;
			}
			
			{
			  fee = 36.5f;
			  if(5000.0f < shares)
			    fee = 11.0f + 0.15f * shares;
			  commisions[16]= fee;
			}
			
			{
			  fee = 0.015f * shares;
			  if(fee < 35.0f)
			    fee = 35.0f;
			  commisions[17]= fee;
			}
			
			{
			  fee = 0.015f * shares;
			  if(fee < 33.0f)
			    fee = 33.0f;
			  commisions[18]= fee;
			}
			
			{
			  if(shares < 1000.0f)
			    fee = 25.0f;
			  else
			    fee = 20.0f;
			  commisions[19]= fee;
			}
			
			{
			  fee = 40.0f + 0.03f * shares;
			  commisions[20]= fee;
			}
			
			{
			  fee = 0.03f * shares;
			  if(fee < 48.0f)
			    fee = 48.0f;
			  commisions[21]= fee;
			}
			
			{
			  fee = 0.125f * shares;
			  if(fee < 50.0f)
			    fee = 50.0f;
			  commisions[22]= fee;
			}
			
			{
			  fee = 20.0f;
			  if(5000.0f < shares)
			    fee = fee + 0.01f * shares;
			  fee = fee + 3.0f;
			  commisions[23]= fee;
			}
			
			{
			  double[] sharess = {100.0f, 
						200.0f, 
						300.0f, 
						800.0f, 
						900.0f,
						100.00f,
						1100.0f,
						1200.0f,
						1300.0f,
						1400.0f,
						1500.0f,
						1600.0f,
						1700.0f,
						1800.0f,
						1900.0f,
						2000.00f, 
						999999999.0f
					      };
			  double[] prices = {30.0f,  
						 35.0f,  
						 35.0f,  
						 40.0f,  
						 45.0f,
						 50.0f,  
						 51.0f,  
						 52.0f,  
						 53.0f,  
						 54.0f,  
						 55.0f,  
						 56.0f,  
						 57.0f,  
						 58.0f,  
						 59.0f, 
						 60.0f, 
						 -99.99f
					     };
			  for(i = 0; i < 17; i++)
			    if(sharess[i] < shares)
			      continue;
			    else
			      break;
			  fee = prices[i];
			  commisions[24]= fee;
			}
			
			{
			  double[] prices = {10.0f,
						20.0f,
						30.0f,
						999999.9f
					    };
			  double[] costs = {0.05f,
						0.06f,
					       0.07f,
					       0.08f
					   };
			  if(price < 1.0f) {
			    fee = 0.05f * shares * price;
			    if(fee < 20.0f)
			      fee = 20.0f;
			  } else {
			    fee = 20.0f;
			    if(500.0f < shares) {
			      fee = fee + 0.03f * (shares - 500.0f);
			      shares = 500.0f;
			    };
			    for(i = 0; i < 4; i++)
			      if(prices[i] < price)
				continue;
			      else
				break;
			    fee = fee + shares * costs[i];
			    fee = fee + 1.25f;
			  };
			  commisions[25]= fee;
			}
			
			{
			  double[] principles = { 2500.0f,
						     6250.0f,
						     20000.0f,
						     50000.0f,
						     500000.0f,
						     9999999999.9f
						 };
			  double[] bases = { 30.0f,
						56.0f,
						76.0f,
						100.0f,
						155.0f,
						255.0f
					   };
			  double[] perc = {0.017f,
					      0.0066f,
					      0.0034f,
					      0.0022f,
					      0.0011f,
					      0.0009f
					    };
			  princ = shares * price;
			  for(i = 0; i < 6; i++)
			    if(principles[i] < princ)
			      continue;
			    else
			      break;
			  fee = bases[i] + perc[i] * princ;
			  if(1000.0f < shares)
			    if(price < 5.0f)
			      min = 90.0f + 0.04f * (shares - 1000.0f);
			    else
			      min = 90.0f + 0.05f * (shares - 1000.0f);
			  else
			    min = 0.09f * shares;
			  if(shares < 100.0f)
			    max = 55.0f;
			  else
			    max = 55.0f + 0.55f * (shares - 100.0f);
			  if(max < fee)
			    fee = max;
			  if(fee < min)
			    fee = min;
			  if(fee < 39.0f)
			    fee = 39.0f;
			  commisions[26]= fee;
			}
			
			{
			  double[] principles = { 2500.0f,
						     6000.0f,
						     22000.0f,
						     50000.0f,
						     500000.0f,
						     9999999999.9f
						 };
			  double[] bases = { 29.5f,
						55.5f,
						75.5f,
						99.5f,
						154.5f,
						254.5f
					   };
			  double[] perc = {0.017f,
					      0.0066f,
					      0.0034f,
					      0.0022f,
					      0.0011f,
					      0.0009f
					    };
			  princ = shares * price;
			  for(i = 0; i < 6; i++)
			    if(principles[i] < princ)
			      continue;
			    else
			      break;
			  fee = bases[i] + perc[i] * princ;
			  if(1000.0f < shares)
			    min = 85.0f + 0.04f * (shares - 1000.0f);
			  else
			    min = 0.085f * shares;
			  if(shares < 100.0f)
			    max = 54.0f;
			  else
			    max = 54.0f + 0.55f * (shares - 100.0f);
			  if(0.5f * princ < max)
			    max = 0.5f * princ;
			  if(max < fee)
			    fee = max;
			  if(fee < min)
			    fee = min;
			  if(fee < 38.0f)
			    fee = 38.0f;
			  commisions[27]= fee;
			}
			
			{
			  princ = shares * price;
			  if(price < 1.0f)
			    fee = 34.87f + .0412056f * princ;
			  else {
			    if(princ <= 3000.0f)
			      fee = 18.48f + .01232f * princ;
			    if((3000.0f < princ) && (princ < 7000.0f))
			      fee = 36.96f + 0.0061611f * princ;
			    if((7000.0f < princ) && (princ < 27000.0f))
			      fee = 58.52f + 0.0030811f * princ;
			    if(27000.0f < princ)
			      fee = 58.52f + 0.0030811f * princ;
			  }
			  if(shares <= 600.0f)
			    min = 0.08217f * shares;
			  else
			    min = (0.08217f * 600) + .0410256f * (shares - 600);
			  max = Math.floor((shares + 99.0f) / 100.0f) * 46.2f;
			  if(max < fee)
			    fee = max;
			  if(fee < min)
			    fee = min;
			  if(fee < 37.0f)
			    fee = 37.0f;
			  fee = fee + 1.50f;
			  commisions[28]= fee;
			}
			
			{
			  double lots;
			  lots = Math.floor((shares + 99.0f) / 100.0f);
			  if(price < 1.0f) {
			    fee = 15.00f + 0.03f * shares;
			    if(fee < 20.00f) 
			      fee = 20.0f;
			  };
			  if((1.0f<= price) && (price <= 5.0f))
			    if(shares < 500)
			      fee = 15.0f + 5.0f * lots;
			    else
			      fee = 40.0f + 0.025f * (shares - 500);
			  if(5.0f<= price) {
			    fee = 0.00f;
			    if(500.0f <= shares) {
			      fee  = 0.05f * (shares - 500);
			      lots = 5.0f;
			    };
			    if(price <= 20.0f)
			      fee = fee + 30.00f + 10.00f * lots;
			    else
			      if(3.0f <= lots)
				fee = fee + 80.0f + 10.0f * (lots - 3);
			      else
				fee = fee + 20.0f + 20.0f * lots;
			  }
			  commisions[29]= fee;
			}
			
			{
			  double[] principles = { 2500.0f,
						     20000.0f,
						     30000.0f,
						     300000.0f,
						     9999999999.9f
						 };
			  double[] bases = { 12.0f,
						22.0f,
						82.0f,
						142.0f,
						-1.0f
					   };
			  double[] perc = {0.013f,
					      0.009f,
					      0.006f,
					      0.004f,
					      0.004f
					    };
			  double lots;
			  princ = shares * price;
			  if(price < 1.0f) {
			    if(princ < 1000.0f)
			      fee = 0.084f * princ;
			    if((1000.0f <= princ) && (princ < 10000.0f))
			      fee = 34.0f + 0.05f * princ;
			    if(10000.0f <= princ)
			      fee = 134.0f + 0.04f * princ;
			  } else {
			    for(i = 0; i < 6; i++)
			      if(principles[i] < princ)
				continue;
			      else
				break;
			    fee = bases[i] + perc[i] * princ;
			    lots = Math.floor(shares / 100.0f);
			    if(lots < 11.0f)
			      fee = fee + 6.0f * lots;
			    else
			      fee = fee + 60.0f + 4.0f * (lots - 10.0f);
			  };
			  if(princ < 5000.0f)
			    fee = fee * 1.3022f;
			  else
			    fee = fee * 1.4705f;
			  fee = fee * 0.4f;
			  if(fee < 25.0f)
			    fee = 25.0f;
			  commisions[30]= fee;
			}
			
			{
			  double[] principles = { 2500.0f,
						     6000.0f,
						     22000.0f,
						     50000.0f,
						     500000.0f,
						     9999999999.9f
						 };
			  double[] bases = { 22.0f,
						38.0f,
						59.0f,
						77.0f,
						120.0f,
						205.0f
					   };
			  double[] perc = {0.014f,
					      0.0045f,
					      0.0025f,
					      0.0017f,
					      0.00085f,
					      0.00068f
					    };
			  princ = shares * price;
			  if(price < 1.0f)
			    fee = 37.0f + 0.03f * princ;
			  else {
			    for(i = 0; i < 6; i++)
			      if(principles[i] < princ)
				continue;
			      else
				break;
			    fee = bases[i] + perc[i] * princ;
			    if(1000.0f < shares)
			      min = 60.0f + 0.03f * (shares - 1000.0f);
			    else
			      min = 0.06f * shares;
			    if(shares < 100.0f)
			      max = 49.0f;
			    else
			      max = 49.0f + 0.5f * (shares - 100.0f);
			    if(max < fee)
			      fee = max;
			    if(fee < min)
			      fee = min;
			  };
			  if(fee < 37.5f)
			    fee = 37.5f;
			  commisions[31]= fee;
			}
			
			{
			  double[] principles = { 2500.0f,
						     5000.0f,
						     15000.0f,
						     50000.0f,
						     250000.0f,
						     9999999999.9f
						 };
			  double[] bases = { 25.0f,
						35.0f,
						50.0f,
						60.0f,
						100.0f,
						125.0f
					   };
			  double[] perc = {0.016f,
					      0.0084f,
					      0.004f,
					      0.003f,
					      0.00125f,
					      0.0011f
					    };
			  princ = shares * price;
			  for(i = 0; i < 6; i++)
			    if(principles[i] < princ)
			      continue;
			    else
			      break;
			  fee = bases[i] + perc[i] * princ;
			  max = 0.48f * shares;
			  if(max < fee)
			    fee = max;
			  if(fee < 40.0f)
			    fee = 40.0f;
			  commisions[32]= fee;
			}
		} catch(NumberFormatException e) {
			for(i = 0; i < number_of_brokers; i++)
				commisions[i] = -1.0f;
		}		
	}
	public void compute_options() {
        	double price, shares;
		double fee, princ, min, max;
		int i;
		try {
			price_obj.setText(price_obj.getText().replace(',', '.'));
			shares_obj.setText(shares_obj.getText().replace(',', '.'));
			price = Double.valueOf(price_obj.getText()).doubleValue();
			shares = Double.valueOf(shares_obj.getText()).doubleValue();

			{
			  if(shares < 10.0f)
			    fee = 25.0f + 2.5f * shares;
			  else
			    fee = 47.5f + 2.0f * (shares - 10.0f);
			  fee = fee + 2.50f;
			  commisions[1] = fee;
			}
			
			{
			  if(price < 1.00f) {
			    if(shares <= 10.0f)
			      fee = 3.00f * shares;
			    if((10.0f < shares) && (shares <= 30.0f)) 
			      fee = 2.00f * (shares - 10) + 30.00f;
			    if(30.0f < shares)
			      fee = 1.50f * (shares - 30) + 70.00f;
			  }
			  if((1.00f <= price) && (price < 2.00f)) {
			    if(shares <= 10.0f)
			      fee = 3.50f * shares;
			    if((10.0f < shares) && (shares <= 30.0f))
			      fee = 2.00f * (shares - 10) + 35.00f;
			    if(30.0f < shares)
			      fee = 1.50f * (shares - 30) + 75.00f;
			  }
			  if((2.00f <= price) && (price < 3.00f)) {
			    if(shares <= 10.0f) 
			      fee = 4.50f * shares;
			    if((10.0f < shares) && (shares <= 30.0f))
			      fee = 2.50f * (shares - 10) + 45.00f;
			    if(30.0f < shares)
			      fee = 2.00f * (shares - 30) + 95.00f;
			  }
			  if((3.00f <= price) && (price < 4.00f)) {
			    if(shares <= 10.0f)
			      fee = 5.50f * shares;
			    if((10.0f < shares) && (shares <= 30.0f))
			      fee = 3.00f * (shares - 10) +  55.00f;
			    if(30.0f < shares)
			      fee = 2.50f * (shares - 30) + 115.00f;
			  }
			  if((4.00f <= price) && (price < 5.00f)) {
			    if(shares <= 10.0f)
			      fee = 6.50f * shares;
			    if((10.0f < shares) && (shares <= 30.0f))
			      fee = 3.50f * (shares - 10) +  65.00f;
			    if(30.0f < shares)
			      fee = 3.00f * (shares - 30) + 135.00f;
			  }
			  if(5.00f <= price) {
			    if(shares <= 10.0f)
			      fee = 7.50f * shares;
			    if((10.0f < shares) && (shares <= 30.0f))
			      fee = 4.50f * (shares - 10) +  75.00f;
			    if(30.0f < shares)
			      fee = 3.50f * (shares - 30) + 165.00f;
			  }
			  if(fee < 21.00f)
			    fee = 21.00f;
			  commisions[3] = fee;
			}
			
			{
			  fee = 1.75f * shares;
			  if(1.00f < price) 
			    fee = 3.00f * shares;
			  if(fee < 25.00f) 
			    fee = 25.00f;
			  fee = fee + 2.50f;      
			  commisions[7] = fee;
			}
			
			{
			  fee = 25.00f + 2.50f * shares;
			  commisions[9] = fee;
			}
			
			{
			  if(price < 0.50f) {
			    if(shares <= 10.0f) 
			      fee = 2.50f * shares;
			    if((10.0f < shares) && (shares < 50.0f))
			      fee = 2.00f * shares;
			    if(50.0f < shares)
			      fee = 1.50f * shares;
			  }
			  if((0.50f <= price) && (price < 2.00f)) {
			    if(shares <= 10.0f) 
			      fee = 3.50f * shares;
			    if((10.0f < shares) && (shares < 50.0f))
			      fee = 2.50f * shares;
			    if(50.0f < shares)
			      fee = 1.75f * shares;
			  }
			  if((2.00f <= price) && (price < 4.00f)) {
			    if(shares <= 10.0f) 
			      fee = 3.75f * shares;
			    if((10.0f < shares) && (shares < 50.0f))
			      fee = 3.00f * shares;
			    if(50.0f < shares)
			      fee = 2.50f * shares;
			  }
			  if((4.00f <= price) && (price < 8.00f)) {
			    if(shares <= 10.0f) 
			      fee = 4.75f * shares;
			    if((10.0f < shares) && (shares < 50.0f))
			      fee = 4.00f * shares;
			    if(50.0f < shares)
			      fee = 3.00f * shares;
			  }
			  if(8.00f <= price) {
			    if(shares <= 10.0f) 
			      fee = 6.25f * shares;
			    if((10.0f < shares) && (shares < 50.0f))
			      fee = 5.25f * shares;
			    if(50.0f < shares)
			      fee = 5.00f * shares;
			  }
			  if(fee < 25.00f) 
			    fee = 25.00f;
			  fee = fee + 4.00f;
			  commisions[10] = fee;
			}
			   
			{
			  if(price < 2.00f) {
			    if(shares <= 10.0f)
			      fee = 3.00f * shares;
			    if((10.0f < shares) && (shares <= 30.0f) )
			       fee = 2.00f * (shares - 10) + 30.00f;
			    if(30.0f < shares)
			      fee = 1.50f * (shares - 30) + 70.00f;
			  }
			  if((2.00f <= price) && (price < 4.00f)) {
			    if(shares <= 10.0f)
			      fee = 4.50f * shares;
			    if((10.0f < shares) && (shares <= 30.0f))
			       fee = 2.50f * (shares - 10) +  45.00f;
			    if(30.0f < shares)
			      fee = 2.00f * (shares - 30) +  95.00f;
			  }
			  if((4.00f <= price) && (price < 8.00f)) {
			    if(shares <= 10.0f)
			      fee = 6.50f * shares;
			    if((10.0f < shares) && (shares <= 30.0f))
			       fee = 3.50f * (shares - 10) +  65.00f;
			    if(30.0f < shares)
			      fee = 3.00f * (shares - 30) + 135.00f;
			  }
			  if(8.00f <= price) {
			    if(shares <= 10.0f)
			      fee = 8.00f * shares;
			    if((10.0f < shares) && (shares <= 30.0f))
			       fee = 6.00f * (shares - 10) +  80.00f;
			    if(30.0f < shares)
			      fee = 5.00f * (shares - 30) + 200.00f;
			  }
			  if(fee < 35.00f) 
			    fee = 35.00f;
			  commisions[14] = fee;
			}
			
			{
			  if(price < 2.00f) {
			    if(shares <= 10.0f)
			      fee = 3.00f * shares;
			    if((10.0f < shares) && (shares <= 30.0f))
			      fee = 2.00f * (shares - 10.0f) + 30.00f;
			    if(30.0f < shares)
			      fee = 1.50f * (shares - 30.0f) + 70.00f;
			  }
			  if((2.00f <= price) && (price < 4.00f)) {    
			    if(shares <= 10.0f)
			      fee = 4.50f * shares;
			    if((10.0f < shares) && (shares <= 30.0f))
			      fee = 2.50f * (shares - 10.0f) + 45.00f;
			    if(30.0f < shares)
			      fee = 2.00f * (shares - 30.0f) + 95.00f;
			  }
			  if(4.00f <= price) {
			    if(shares <= 10.0f)
			      fee = 5.50f * shares;
			    if((10.0f < shares) && (shares <= 30.0f))
			      fee = 3.50f * (shares - 10.0f) +  55.00f;
			    if(30.0f < shares)
			      fee = 3.00f * (shares - 30.0f) + 125.00f;
			  }
			  if(fee < 30.00f)
			    fee = 30.00f; 
			  fee = fee + 3.00f;
			  commisions[15] = fee;
			}
			
			{
			  fee = 20.00f + 1.50f * shares;
			  if(1.00f <= price)
			    fee = 20.00f + 2.00f * shares;
			  if(fee < 40.00f)
			    fee = 40.00f;
			  commisions[16] = fee;
			}
			
			{
			  fee = 30.00f;
			  if(price < 1.00f) 
			    fee += 1.00f * shares;
			  if((1.00f <= price) && (price < 4.00f))
			    fee += 2.00f * shares;
			  if((4.00f <= price) && (price < 7.00f))
			    fee += 3.00f * shares;
			  if(7.00f <= price)
			    fee += 4.00f * shares;
			  commisions[17] = fee;
			}
			
			{
			  fee = 20.00f;
			  if(price < 1.00f) 
			    fee += 1.00f * shares;
			  if((1.00f <= price) && (price < 4.00f))
			    fee += 2.00f * shares;
			  if((4.00f <= price) && (price < 7.00f))
			    fee += 3.00f * shares;
			  if(7.00f <= price)
			    fee += 4.00f * shares;
			  commisions[18] = fee;
			}
			
			{
			  fee = 29.0f;
			  if(5.0f < shares)
			    fee = fee + 1.50f * (shares - 5.0f); 
			  commisions[19] = fee;
			}
			
			{
			  if(price < 1.00f)
			    fee = 40.00f + 1.50f * shares;
			  else
			    fee = 40.00f + 2.00f * shares;
			  commisions[20] = fee;
			}
			
			{
			  fee = 2.00f * shares;
			  if(0.50f < price)
			    fee = 3.75f * shares;
			  if(fee < 50.0f)
			    fee = 50.0f;
			  commisions[22] = fee;
			}
			
			{
			  fee = 3.00f * shares;
			  if(4.00f <= price)
			    fee = 4.00f * shares;
			  if(fee < 35.00f)
			    fee = 35.00;
			  commisions[24] = fee;
			}
			
			{
			  fee = 27.00f + 3.00f * shares;
			  commisions[25] = fee;
			}

		} catch(NumberFormatException e) {
			for(i = 0; i < number_of_brokers; i++)
				commisions[i] = -1.0;
                }
	}
        public boolean action(Event e, Object dummy) {
 		compute();
		return(true);
	}
	public String getAppletInfo() {
		return("Robert's Online Commissions Pricer! by Dr. Robert Lum");
	}
}
