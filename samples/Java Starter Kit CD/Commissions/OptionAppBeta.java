import java.applet.*;
import java.awt.*;

class OptionBeta {
	double _value, _theta, _delta_S, _delta_X, _gamma_S, _gamma_X;
	public OptionBeta() {
	}
	double value() {
                return _value;
        }
        public double theta() {
                return _theta;
        }
        public double delta_S() {
                return _delta_S;
        }
        public double delta_X() {
                return _delta_X;
        }
        public double gamma_S() {
                return _gamma_S;
        }
        public double gamma_X() {
                return _gamma_X;
        }
}

class OptionAmerBeta extends OptionBeta {
	final int ITERATES = 30;
	public OptionAmerBeta(double S, double X, double r_f, double r_d, double sigma, double T) {
		int i,j;
		double X1,S1,S2,c,e;
		double u,d,p,q,t,p1,q1;
		double s[] = new double[ITERATES + 3];
		double d2,u2;
    
		c=T/ITERATES;
		e=Math.exp(r_d*c);
		u=Math.exp(sigma*Math.sqrt(c));
		d=1/u;
		p1=(Math.exp((r_d-r_f)*c)-d)/(u-d);
		q1=(u-Math.exp((r_d-r_f)*c))/(u-d);
		p=p1/e;
		q=q1/e;
		d2=d*d;
		u2=u*u;
		t=0.0f;
    
		X1=S/X;
		S1=X1*Math.pow(u,(double)(ITERATES+2));
		S2=S1;
		for(j=0;j<=ITERATES+2;j++) {
			s[j]=S2-1.0f;
			if(s[j]<0.0f)
				s[j]=0.0f;
			S2=S2*d2;
		};
		S1=S1*d;
		for(i=ITERATES+1;2<=i;i--) {
			S2=S1;
			for(j=0;j<=i;j++) {
				s[j]=p*s[j]+q*s[j+1];
				if(s[j]<S2-1.0f)
					s[j]=S2-1.0f;
				S2=S2*d2;
			};
			if(i==4)
				t=s[2];
			S1=S1*d;
		};
		_value=s[1]*X;
		_delta_S=(s[0]-s[2])/(X1*(u2-d2));
		_gamma_S=((s[0]-s[1])/(X1*(u2-1))-(s[1]-s[2])/(X1*(1-d2)))/
			(X1*(u2-d2)/2)/X;
		_delta_X=-(s[0]-s[2])/(X1*(u2-d2))*S/X+s[1];
		_gamma_X=((s[0]-s[1])/(X1*(u2-1))-(s[1]-s[2])/(X1*(1-d2)))/
			(X1*(u2-d2)/2)*S/X*S/(X*X);
		for(i=1;0<=i;i--) {
			S2=S1;
			for(j=0;j<=i;j++) {
				s[j]=p*s[j]+q*s[j+1];
				if(s[j]<S2-1.0f)
					s[j]=S2-1.0f;
				if(s[j]==0.0f)
					break;
				S2=S2*d2;
			};
			S1=S1*d;
		};
		_theta=-(s[0]-t)*X/(c*4.0f)/365.0f;
	}
}

class OptionEuroBeta extends OptionBeta {
	double normal(double x) {
		double y;
		double k;
 
		if(x<0)
			return 1-normal(-x);
		if(x==0)
			return 0.5f;
		k=1.0f/(1.0f+0.2316419f*x);
		y=1-Math.exp(-x*x/2)*k*(0.31938153f+
			k*(-0.356563782f+
				k*(1.781477937f+
					k*(-1.821255978f+
						k*1.330274429f))))/Math.sqrt(6.28318530717958646f);
		return y;
	}
	public OptionEuroBeta(double S, double X, double r_f, double r_d, double sigma, double T) {
		double x1, p1, q1, p2, q2, p3;

		x1=(Math.log(S/X)+(r_d-r_f+sigma*sigma/2)*T)/(sigma*Math.sqrt(T));
		p1=normal(x1);
		q1=normal(x1-sigma*Math.sqrt(T));
		p2=Math.exp(-r_f*T);
		q2=Math.exp(-r_d*T);
		p3=Math.exp(-x1*x1/2)/Math.sqrt(6.28318530717958646f);
    
		_value=S*p2*p1-X*q2*q1;
		_theta=-r_f*S*p2*p1+S*p2*p3*sigma/2/Math.sqrt(T)+r_d*X*q2*q1;
		_theta=-_theta/365.0f;
    
		_delta_S=p2*p1;
		_gamma_S=p2*p3/(S*sigma*Math.sqrt(T));
		_delta_X=-q2*q1;
		_gamma_X=q2*Math.exp(-(x1-sigma*Math.sqrt(T))*(x1-sigma*Math.sqrt(T))/2)/
			Math.sqrt(6.28318530717958646f)/(X*sigma*Math.sqrt(T));
	}
}

public class OptionAppBeta extends Applet {
	TextField value;
	TextField delta;
	TextField gamma;
	TextField theta;
	TextField price;
	TextField strike;
	TextField dividend;
	TextField interest;
	TextField volatility;
	TextField time_left;
	Checkbox is_call;
	Checkbox is_put;
	Checkbox is_american;
	Checkbox is_european;
	Checkbox is_daily;
	Checkbox is_monthly;
	Checkbox is_yearly;
	CheckboxGroup call_put;
	CheckboxGroup amer_euro;
	CheckboxGroup dmy;
	OptionEuroBeta optioneuro;
	OptionAmerBeta optionamer;
	public void init() {
		add("ValueLabel", new Label("Value:", Label.RIGHT));
		add("Value", value = new TextField("", 10));
		add("DeltaLabel", new Label("Delta:", Label.RIGHT));
		add("Delta", delta = new TextField("", 10));
		add("GammaLabel", new Label("Gamma:", Label.RIGHT));
		add("Gamma", gamma = new TextField("", 10));
		add("ThetaLabel", new Label("Theta:", Label.RIGHT));
		add("Theta", theta = new TextField("", 10));
		add("PriceLabel", new Label("How much is the stock price? (in $.c)"));
		add("Price", price = new TextField("", 10));
		add("StrikeLabel", new Label("What is the strike price? (in $.c)"));
		add("Strike", strike = new TextField("", 10));
		add("DividendLabel", new Label("What is the dividend yield? (in % per annum)"));
		add("Dividend", dividend = new TextField("", 10));
		add("InterestLabel", new Label("What is the interest rate? (in % per annum)"));
		add("Interest", interest = new TextField("", 10));
		add("VolatilityLabel", new Label("What is the volatility? (in % per annum)"));
		add("Volatility", volatility = new TextField("", 10));
		add("Time_leftLabel", new Label("How long left to expiration?"));
		add("Time_left", time_left = new TextField("", 10));
		dmy = new CheckboxGroup();
		amer_euro = new CheckboxGroup();
		call_put = new CheckboxGroup();
		add("Daily", is_daily = new Checkbox("Expiration in Days or...", dmy, true));
		add("Monthly", is_monthly = new Checkbox("Months or...", dmy, false)); 
		add("Yearly", is_yearly = new Checkbox("Years?", dmy, false));
		dmy.setCurrent(is_daily);
		add("Call", is_call = new Checkbox("Call option or...", call_put, true));
		add("Put", is_put = new Checkbox("Put option?", call_put, false));
		call_put.setCurrent(is_call);
		add("American", is_american = new Checkbox("American option or...", amer_euro, true));
		add("European", is_european = new Checkbox("European option?", amer_euro, false));
		amer_euro.setCurrent(is_american);
	}
	public synchronized void layout() {
		resize(500, 400);
		if(0 < countComponents()) { 
			getComponent(0).reshape(20, 10, 40, 30);
			getComponent(1).reshape(60, 10, 70, 30);
			getComponent(2).reshape(130, 10, 40, 30);
			getComponent(3).reshape(170, 10, 70, 30);
			getComponent(4).reshape(240, 10, 55, 30);
			getComponent(5).reshape(295, 10, 70, 30);
			getComponent(6).reshape(365, 10, 40, 30);
			getComponent(7).reshape(405, 10, 70, 30);
			getComponent(8).reshape(60, 70, 300, 30);
			getComponent(9).reshape(360, 70, 80, 30);
			getComponent(10).reshape(60, 100, 300, 30); 
			getComponent(11).reshape(360, 100, 80, 30);
			getComponent(12).reshape(60, 130, 300, 30);
			getComponent(13).reshape(360, 130, 80, 30);
			getComponent(14).reshape(60, 160, 300, 30);
			getComponent(15).reshape(360, 160, 80, 30);
			getComponent(16).reshape(60, 190, 300, 30);
			getComponent(17).reshape(360, 190, 80, 30);
			getComponent(18).reshape(60, 220, 300, 30);
			getComponent(19).reshape(360, 220, 80, 30);
			getComponent(20).reshape(60, 260, 160, 30);
			getComponent(21).reshape(230, 260, 120, 30);
			getComponent(22).reshape(350, 260, 120, 30);
			getComponent(23).reshape(0, 290, 110, 30);
			getComponent(24).reshape(110, 290, 100, 30);
			getComponent(25).reshape(210, 290, 145, 30);
			getComponent(26).reshape(355, 290, 140, 30);
		}	
	}
	public void compute() {
        	double S, X, rf, rd, s, t;
		try {
			price.setText(price.getText().replace(',', '.'));
			strike.setText(strike.getText().replace(',', '.'));
			dividend.setText(dividend.getText().replace(',', '.'));
			interest.setText(interest.getText().replace(',', '.'));
			volatility.setText(volatility.getText().replace(',', '.'));
			time_left.setText(time_left.getText().replace(',', '.'));
			S = Double.valueOf(price.getText()).doubleValue();
			X = Double.valueOf(strike.getText()).doubleValue();
			rf = Double.valueOf(dividend.getText()).doubleValue() / 100.0f;
			rd = Double.valueOf(interest.getText()).doubleValue() / 100.0f;
			s = Double.valueOf(volatility.getText()).doubleValue() / 100.0f;
			t = Double.valueOf(time_left.getText()).doubleValue();
			if(dmy.getCurrent() == is_daily)
				t = t / 365.0;
			if(dmy.getCurrent() == is_monthly)
				t = t / 12.0;
			if(amer_euro.getCurrent() == is_european) {
				if(call_put.getCurrent() == is_call) {
					optioneuro = new OptionEuroBeta(S, X, rf, rd, s, t);
					value.setText(new Double(optioneuro.value()).toString());
					delta.setText(new Double(optioneuro.delta_S()).toString());
					gamma.setText(new Double(optioneuro.gamma_S()).toString());
					theta.setText(new Double(optioneuro.theta()).toString());
				};
				if(call_put.getCurrent() == is_put) {
					optioneuro = new OptionEuroBeta(X, S, rd, rf, s, t);
					value.setText(new Double(optioneuro.value()).toString());
					delta.setText(new Double(optioneuro.delta_X()).toString());
					gamma.setText(new Double(optioneuro.gamma_X()).toString());
					theta.setText(new Double(optioneuro.theta()).toString());
				};
			};
			if(amer_euro.getCurrent() == is_american) {
				if(call_put.getCurrent() == is_call) {
					optionamer = new OptionAmerBeta(S, X, rf, rd, s, t);
					value.setText(new Double(optionamer.value()).toString());
					delta.setText(new Double(optionamer.delta_S()).toString());
					gamma.setText(new Double(optionamer.gamma_S()).toString());
					theta.setText(new Double(optionamer.theta()).toString());
				};
				if(call_put.getCurrent() == is_put) {
					optionamer = new OptionAmerBeta(X, S, rd, rf, s, t);
					value.setText(new Double(optionamer.value()).toString());
					delta.setText(new Double(optionamer.delta_X()).toString());
					gamma.setText(new Double(optionamer.gamma_X()).toString());
					theta.setText(new Double(optionamer.theta()).toString());
				};
			};
		} catch(NumberFormatException e) {
			value.setText("");
			delta.setText("");
			gamma.setText("");
			theta.setText("");
		}
		repaint();
	}
        public boolean action(Event e, Object dummy) {
 		compute();
		return(true);
	}
	public String getAppletInfo() {
		return("Robert's Online Option Pricer! by Dr. Robert Lum");
	}
}
