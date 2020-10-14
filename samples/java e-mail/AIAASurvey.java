// Author; Aaron Mulder (ammulder@princeton.edu)

import java.applet.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.net.*;
import amm.mail.*;

public class AIAASurvey extends Applet implements MailSource {
  TextField name,address,dept;
  Checkbox topics[];
  TextArea character, contact, thoughts;
  Choice year;
  String done;
  Label show,captions[];
  Mailer mail;

  public void init(){
    mail=new Mailer(this);
    done=getParameter("success");
    if(done==null)done="no URL";
    setLayout(new BorderLayout());
    Panel p1=new Panel();
    p1.setLayout(new GridLayout(4,1));
    Panel p2=new Panel();
    p2.setLayout(new GridLayout(1,2));
    p2.add(new Label("Name: ",Label.RIGHT));
    name=new TextField();
    p2.add(name);
    p1.add(p2);
    p2=new Panel();
    p2.setLayout(new GridLayout(1,2));
    p2.add(new Label("E-mail address: ",Label.RIGHT));
    address=new TextField();
    p2.add(address);
    p1.add(p2);
    p2=new Panel();
    p2.setLayout(new GridLayout(1,2));
    p2.add(new Label("Department: ",Label.RIGHT));
    dept=new TextField();
    p2.add(dept);
    p1.add(p2);
    p2=new Panel();
    p2.setLayout(new GridLayout(1,2));
   p2.add(new Label("Class: ",Label.RIGHT));
    year=new Choice();
    year.addItem("1996");
    year.addItem("1997");
    year.addItem("1998");
    year.addItem("1999");
    p2.add(year);
    p1.add(p2);
    add("North",p1);
    
    topics=new Checkbox[44];
    captions=new Label[7];
    p1=new Panel();
    p1.setLayout(new BorderLayout());
    p2=new Panel();
    p2.setLayout(new GridLayout(51,1));
    captions[0]=new Label("Engineering and Technology Management");
    p2.add(captions[0]);
    topics[0]=new Checkbox("Economics");
    p2.add(topics[0]);
    topics[1]=new Checkbox("History");
    p2.add(topics[1]);
    topics[2]=new Checkbox("Legal Aspects");
    p2.add(topics[2]);
    topics[3]=new Checkbox("Management");
    p2.add(topics[3]);
    topics[4]=new Checkbox("Society and Technology");
    p2.add(topics[4]);
    topics[5]=new Checkbox("Information Services");
    p2.add(topics[5]);
    captions[1]=new Label("Aerospace Sciences");
    p2.add(captions[1]);
    topics[6]=new Checkbox("Fluid Dynamics and Aeroacoustics");
    p2.add(topics[6]);
    topics[7]=new Checkbox("Applied Aerodynamics and Flight Mechanics");
    p2.add(topics[7]);
    topics[8]=new Checkbox("Atmospheric Sciences, Balloon Technology, and Sounding Rockets");
    p2.add(topics[8]);
    topics[9]=new Checkbox("Guidance, Navigation, and Control");
    p2.add(topics[9]);
    topics[10]=new Checkbox("Measurement Technology");
    p2.add(topics[10]);
    topics[11]=new Checkbox("Thermophysics, Plasmadynamics, and Lasers");
    p2.add(topics[11]);
    captions[2]=new Label("Aircraft Operations and Technology");
    p2.add(captions[2]);
    topics[12]=new Checkbox("Air Transportation Systems");
    p2.add(topics[12]);
    topics[13]=new Checkbox("Aircraft and Helicopter Design, Operations, and Safety");
    p2.add(topics[13]);
    topics[14]=new Checkbox("General Aviation Systems");
    p2.add(topics[14]);
    topics[15]=new Checkbox("Lighter-Than-Air, Remotely-Piloted, and V/STOL Aircraft Systems");
    p2.add(topics[15]);
    topics[16]=new Checkbox("Marine Systems and Technology");
    p2.add(topics[16]);
    topics[17]=new Checkbox("Multidisciplinary Design and Optimiztion");
    p2.add(topics[17]);
    captions[3]=new Label("Information and Logistics Systems");
    p2.add(captions[3]);
    topics[18]=new Checkbox("Electronic, Sensors, and Artificial Intelligence");
    p2.add(topics[18]);
    topics[19]=new Checkbox("Maintenance, Support, and Logistics");
    p2.add(topics[19]);
    topics[20]=new Checkbox("Command, Control, Communications, and Intelligence Systems");
    p2.add(topics[20]);
    topics[21]=new Checkbox("Communications Systems");
    p2.add(topics[21]);
    topics[22]=new Checkbox("Computer Hardware and Software Systems");
    p2.add(topics[22]);
    topics[23]=new Checkbox("System Effectiveness and Safety");
    p2.add(topics[23]);
    captions[4]=new Label("Propulsion and Energy");
    p2.add(captions[4]);
    topics[24]=new Checkbox("Aerospace Power Systems");
    p2.add(topics[24]);
    topics[25]=new Checkbox("Air Breathing Propulsion");
    p2.add(topics[25]);
    topics[26]=new Checkbox("Electric Propulsion");
    p2.add(topics[26]);
    topics[27]=new Checkbox("Liquid Propulsion");
    p2.add(topics[27]);
    topics[28]=new Checkbox("Propellants and Combustion");
    p2.add(topics[28]);
    topics[29]=new Checkbox("Terrestrial Energy Systems");
    p2.add(topics[29]);
    topics[30]=new Checkbox("Nuclear Thermal Propulsion");
    p2.add(topics[30]);
    topics[31]=new Checkbox("Rockets (Liquid, Solid, Hybrid, Tripropellant)");
    p2.add(topics[31]);
    captions[5]=new Label("Space and Missile Systems");
    p2.add(captions[5]);
    topics[32]=new Checkbox("Life Sciences and Systems");
    p2.add(topics[32]);
    topics[33]=new Checkbox("Missile Systems");
    p2.add(topics[33]);
    topics[34]=new Checkbox("Processing, Operations, Systems, and Support");
    p2.add(topics[34]);
    topics[35]=new Checkbox("Space Transportation");
    p2.add(topics[35]);
    topics[36]=new Checkbox("Space Science and Astronomy");
    p2.add(topics[36]);
    topics[37]=new Checkbox("Space Automation and Robotics");
    p2.add(topics[37]);
    captions[6]=new Label("Structures (Design and Test)");
    p2.add(captions[6]);
    topics[38]=new Checkbox("CAD/CAM");
    p2.add(topics[38]);
    topics[39]=new Checkbox("Survivability");
    p2.add(topics[39]);
    topics[40]=new Checkbox("Design Engineering and Technology");
    p2.add(topics[40]);
    topics[41]=new Checkbox("Flight Simulation/Flight and Ground Testing");
    p2.add(topics[41]);
    topics[42]=new Checkbox("Interactive Computer Graphics");
    p2.add(topics[42]);
    topics[43]=new Checkbox("Structures and Materials");
    p2.add(topics[43]);
    p1.add("North",p2);
    p2=new Panel();
    p2.setLayout(new GridLayout(3,1));
    Panel p3=new Panel();
    p3.setLayout(new BorderLayout());
    p3.add("North",new Label("What character would you like the AIAA to assume (speakers, trips, etc.)?"));
    character=new TextArea();
    p3.add("Center",character);
    p2.add(p3);
    p3=new Panel();
    p3.setLayout(new BorderLayout());
    p3.add("North",new Label("Do you know anyone who might be willing to lecture for the AIAA?"));
    contact=new TextArea();
    p3.add("Center",contact);
    p2.add(p3);
    p3=new Panel();
    p3.setLayout(new BorderLayout());
    p3.add("North",new Label("Any other thoughts?"));
    thoughts=new TextArea();
    p3.add("Center",thoughts);
    p2.add(p3);
    p1.add("Center",p2);
    add("Center",p1);
    p1=new Panel();
    p1.setLayout(new GridLayout(2,1));
    p1.add(new Button("Submit to aiaa@princeton.edu"));
    show=new Label();
    p1.add(show);
    add("South",p1);
  }
  
  public void start(){
    status("Composing message...");
  }
  
  public boolean handleEvent(Event evt){
    if(evt.target instanceof Button){
      mail.start();
      return true;
    }
    return false;
  }
  
  public String host(){return "www.princeton.edu";}

  public String[] to(){
    String temp[]={"aiaa@phoenix.princeton.edu",};
     return temp;
  }

  public String from(){ return address.getText();}

  public String name() { return  name.getText();}

  public String subject() {return "AIAA Online Questionaire 1996";}

  public void data(PrintStream out){
    boolean test1=true,test2=true;
    int i;
    out.println(name.getText()+", class of "+year.getSelectedItem()+" ("+dept.getText()+")");
    out.println("");
    out.flush();
    for(i=0;i<6;i++) if(topics[i].getState()){
      if(test1){
        out.println("I am interested in the following topics: ");
        test1=false;
      }
      if(test2){
        out.println(captions[0].getText());
        test2=false;
      }
      out.println(" - "+topics[i].getLabel());
    }
    test2=true;
    for(i=6;i<12;i++) if(topics[i].getState()){
      if(test1){
        out.println("I am interested in the following topics: ");
        test1=false;
      }
      if(test2){
        out.println(captions[1].getText());
        test2=false;
      }
      out.println(" - "+topics[i].getLabel());
    }
    test2=true;
    for(i=12;i<18;i++) if(topics[i].getState()){
      if(test1){
        out.println("I am interested in the following topics: ");
        test1=false;
      }
      if(test2){
        out.println(captions[2].getText());
        test2=false;
      }
      out.println(" - "+topics[i].getLabel());
    }
    test2=true;
    for(i=18;i<24;i++) if(topics[i].getState()){
      if(test1){
        out.println("I am interested in the following topics: ");
        test1=false;
      }
      if(test2){
        out.println(captions[3].getText());
        test2=false;
      }
      out.println(" - "+topics[i].getLabel());
    }
   test2=true;
   for(i=24;i<32;i++) if(topics[i].getState()){
      if(test1){
        out.println("I am interested in the following topics: ");
        test1=false;
      }
      if(test2){
        out.println(captions[4].getText());
        test2=false;
      }
      out.println(" - "+topics[i].getLabel());
    }
    test2=true;
    for(i=32;i<38;i++) if(topics[i].getState()){
      if(test1){
        out.println("I am interested in the following topics: ");
        test1=false;
      }
      if(test2){
        out.println(captions[5].getText());
        test2=false;
      }
      out.println(" - "+topics[i].getLabel());
    }
    test2=true;
    for(i=38;i<44;i++) if(topics[i].getState()){
      if(test1){
        out.println("I am interested in the following topics: ");
        test1=false;
      }
      if(test2){
        out.println(captions[6].getText());
        test2=false;
      }
      out.println(" - "+topics[i].getLabel());
    }
    out.flush();
    if(character.getText().length()>0) {
      out.println("");
      out.println("My thoughts on the character of the AIAA:");
      out.println(character.getText());
      out.flush();
    }
    if(contact.getText().length()>0) {
      out.println("");
      out.println("A possible contact for lectures:");
      out.println(contact.getText());
      out.flush();
    }
    if(thoughts.getText().length()>0) {
      out.println("");
      out.println("Other thoughts I had:");
      out.println(thoughts.getText());
      out.flush();
    }
    out.println("");
    out.println("---------------------");
    out.println("Response generated by AIAASurvey.java");
    out.flush();
  }

  public void done(boolean succeeded) {
    if(succeeded){
      status("Message sent successfully.");
      try getAppletContext().showDocument(new URL(done));
      catch (Exception e) status("mail submitted, but failed to load "+done);
    }
  }

  public void status(String s){
    show.setText(s);
    showStatus(s);
  }
}
