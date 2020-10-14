
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Window;
import java.awt.Panel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Event;

import java.net.URL;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.StringTokenizer;

/* Copyright (C) 1995 Linkoping University, Linkoping, Sweden. */

/**
 * The class NuclearPlant is an applet that lets you run a nuclear power plant
 *
 * @version 1.0f
 * @author Henrik Eriksson
 */
          public class NuclearPlant extends java.applet.Applet implements Runnable {

              /** The thread that updates the display (for animation) */
              protected Thread kicker = null;

              /** The thread that runs the sequences */
              protected Thread buttonThread;

              /** The amount of time too sleep between display updates (ms?) */
              protected int kickerDelay;

              /** The offscreen image */
              protected Image im;

              /** The offscreen graphics context */
              protected Graphics offscreen;

              /** The static background image */
              protected Image background;

              /** The directory that contains the images. This
                  directory is the location for GIFS for the
                  background, plant components, and display
                  animation. */
              static String imageDir = "images/";

              /** The reactor object */
              Reactor reactor;

              /** Valve object */
              Valve valve_1, valve_2, valve_3, valve_4;

              /** Pump object */
              Pump pump_1, pump_2, pump_3;

              /** The turbine */
              Turbine turbine;

              /** The condenser */
              Condenser condenser;

              /** The generator */
              Generator generator;

              /** The nuclear power plant simulator */
              Simulator simulator;

              /** Panel for the sequence control buttons */
              Panel controlPanel, sequencePanel;

              Stop_Button stopButton;

              /** Flag that is true iff a simulation is running */
              boolean isRunningSimulation = false;

              /** The status message that is display in the applet area */
              String message = " ";

              /** Initialize the applet. Sets the applet size, creates
                  the plant components, and creates the simulator. */
              public void init() {
                  //resize(680, 473);       // java.applet.Applet size
                  im = createImage(680,473);
                  offscreen = im.getGraphics();
                  offscreen.setColor(Color.black);

                  background = getImage(getCodeBase(), imageDir + "BACKGROUND.GIF");

                  Component.plant = this;
                  reactor = new Reactor();
                  valve_1 = new Valve("SV1", 360, 59);
                  valve_2 = new Valve("SV2", 360, 178);
                  valve_3 = new Valve("WV1", 385, 382);
                  valve_4 = new Valve("WV2", 385, 420);
                  pump_1 = new Pump("Pump 1", 406, 389, 1400);
                  pump_2 = new Pump("Pump 2", 406, 427, 1400);
                  pump_3 = new Pump("Pump 3", 619, 421, 1285);
                  turbine = new Turbine();
                  condenser = new Condenser();
                  generator = new Generator();

		  setLayout(new BorderLayout());

		  sequencePanel = new Panel();
		  sequencePanel.setLayout(new FlowLayout());
                  sequencePanel.add(new Seq_1_Button(this, "Sequence 1"));
                  sequencePanel.add(new Seq_2_Button(this, "Sequence 2"));
                  sequencePanel.add(new Seq_3_Button(this, "Sequence 3"));
                  sequencePanel.add(new Rand_Button(this, "Randomize"));
		  controlPanel = new Panel();
		  controlPanel.setLayout(new FlowLayout());
                  controlPanel.add(sequencePanel);
                  stopButton = new Stop_Button(this, "STOP");
                  stopButton.disable();
                  controlPanel.add(stopButton);
		  add("North", controlPanel);

		  simulator = new LocalSimulator(this);
              }


              /** Update the plant display
                * @param g Graphics context to paint in */
              public void paint(Graphics g) {
		  if (reactor == null) {
		      return;
		  }
                  if (!reactor.overheated) {
		    Dimension d = size();
		    offscreen.setColor(getBackground());
		    offscreen.fillRect(0, 0, d.width, d.height);

                    offscreen.drawImage(background,0,50, this);
                    reactor.paint(offscreen);
                    valve_1.paint(offscreen);
                    valve_2.paint(offscreen);
                    valve_3.paint(offscreen);
                    valve_4.paint(offscreen);
                    pump_1.paint(offscreen);
                    pump_2.paint(offscreen);
                    pump_3.paint(offscreen);
                    turbine.paint(offscreen);
                    condenser.paint(offscreen);
                    generator.paint(offscreen);
                    offscreen.drawString(message, 50, 440);
                  } else reactor.paintMeltdown(offscreen);
                  g.drawImage(im,0,0, this);
              }


              /** Update without erasing background
                * @param g Graphics context to update in */
              public void update(Graphics g) {
                paint(g);
              }

              /** Handle mouseDown events. This method distributes the
                  message by calling the mouseDown methods for the
                  appropriate plant components
                * @param x The X coordinate
                * @param y The Y coordinate */
              public boolean mouseDown(Event evt, int x, int y) {
// getAppletContext().showStatus("mouseDown " + x + " " + y);
                if (isRunningSimulation) {
                  if (simulator instanceof LocalSimulator) {
                    pump_1.mouseDown(evt, x, y);
                    pump_2.mouseDown(evt, x, y);
                    pump_3.mouseDown(evt, x, y);
                    valve_1.mouseDown(evt, x, y);
                    valve_2.mouseDown(evt, x, y);
                    valve_3.mouseDown(evt, x, y);
                    valve_4.mouseDown(evt, x, y);
                    reactor.mouseDown(evt, x, y);
                  }
                } else getAppletContext().showStatus("Start the simulation by clicking on a sequence button");
		return true;
              }


              /** Handle mouseDrag events. This method distributes the
                  message by calling the mouseDrag methods for the
                  appropriate plant components
                * @param x The X coordinate
                * @param y The Y coordinate */
              public boolean mouseDrag(Event evt, int x, int y) {
                return reactor.mouseDrag(evt, x, y);
              }

              /** Handle mouseUp events. This method distributes the
                  message by calling the mouseUp methods for the
                  appropriate plant components
                * @param x The X coordinate
                * @param y The Y coordinate */
              public boolean mouseUp(Event evt, int x, int y) {
                return reactor.mouseUp(evt, x, y);
              }

              /** Rotate the pumps one step. (Only pumps with rpm > 0
                  are rotated.) */
              protected void rotatePumps() {
                pump_1.rotate();
                pump_2.rotate();
                pump_3.rotate();
              }

              /** Move the waves on the water surfaces in the tanks */
              protected void waterWave() {
                reactor.waterWave();
                condenser.waterWave();
              }

              /** Start the simulation */
              public void startReactor() {
                  getAppletContext().showStatus("The simulation is running...");
                  simulator.start();
              }

              /** Run the simulation n steps. Uses the crurrent
                * simulator to calculate the next states
                * @param n The number of steps */
              public void timeStep(int n) {
                  simulator.timeStep(n);
              }

              /** Blow up a device.
                * @param device The device to blow (e.g., a turbine object) */
              public void blow(Component device) {
                  simulator.blow(device);
              }

              /** Stop the simulation */
              public void stopReactor() {
                  getAppletContext().showStatus("The simulation has stopped");
                  simulator.stop();
              }

              /** Play a crash sound. This method is called when plant
                  components blow up.
                * @param n Duration */
              public void crashSound(int n) {
                if (n < 1000)
                  play(getCodeBase(), "audio/breakdown.au");
                else if (n < 10000)
                  play(getCodeBase(), "audio/Explosion-2.au");
                else
                  play(getCodeBase(), "audio/Explosion-1.au");
              }


              /** Run the animation thread. This method is called when
                  the animation thread is started. */
              public void run() {
                Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                while (kicker != null) {
                  if (isRunningSimulation) {
                    rotatePumps();
                    waterWave();
                  }
                  repaint();
try {Thread.sleep(kickerDelay);} catch (InterruptedException e){}
                }
              }

              /** Start the applet. Creates the animation thread */
              public void start() {
                if (kicker == null) {
                  kicker = new Thread(this);
                  kickerDelay = 60;
                  kicker.start();
                }
              }

              /** Stop the applet. Stops the animation thread and
                  closes the window for the sequence control buttons */
              public void stop() {
                if (kicker != null) {
                  kicker.stop();
                  kicker = null;
                }
              }
          }


/**
 * The class SeqButton is an abstract class for buttons that control
 * simulation sequences.
 *
 * @version 1.0f
 * @author Henrik Eriksson
 */
          abstract class SeqButton extends Button implements Runnable {
              /** Backpointer to the NuclearPlant applet */
              protected NuclearPlant plant;

              /** Construct a button and initialize it.
                * @param p The plant
                * @param name The button label */
              public SeqButton(NuclearPlant p, String name) {
                super(name);
                plant = p;
              }

              /** Perform a sequence when the button is selected. This
                  method is called automatically when the button is
                  selected by the user. The method runs the sequence
                  in a separate thread.
                * @param evt the Event
                * @param arg the argument */
              public boolean action(Event evt, Object arg) {
                plant.buttonThread = new Thread(this);
                plant.buttonThread.start();
		return true;
              }

          }


/**
 * Class for the Sequence 1 button.
 *
 * @version 1.0f
 * @author Henrik Eriksson
 */
          class Seq_1_Button extends SeqButton {
              /** Construct a button and initialize it.
                * @param p The plant
                * @param name The button label
                * @param x The X coordinate
                * @param y The Y coordinate */
              public Seq_1_Button(NuclearPlant p, String name) {
                super(p, name);
              }

              /** Run the sequence. This method is called in a
                  separate thread when the button is selected by the
                  user. Modify this method to change the sequence in
                  question. */
              public void run() {
		plant.sequencePanel.disable();
		plant.stopButton.enable();
		plant.isRunningSimulation = true;
                plant.startReactor();
                plant.message = "Sequence 1 running...";
                plant.timeStep(25);
                plant.blow(plant.turbine);
                plant.timeStep(200);
                plant.message = " ";
                plant.stopReactor();
		plant.isRunningSimulation = false;
		plant.sequencePanel.enable();
		plant.stopButton.disable();
              }

          }


/**
 * Class for the Sequence 2 button.
 *
 * @version 1.0f
 * @author Henrik Eriksson
 */
          class Seq_2_Button extends SeqButton {
              /** Construct a button and initialize it.
                * @param p The plant
                * @param name The button label
                * @param x The X coordinate
                * @param y The Y coordinate */
              public Seq_2_Button(NuclearPlant p, String name) {
                super(p, name);
              }

              /** Run the sequence. This method is called in a
                  separate thread when the button is selected by the
                  user. Modify this method to change the sequence in
                  question. */
              public void run() {
		plant.sequencePanel.disable();
		plant.stopButton.enable();
                plant.startReactor();
		plant.isRunningSimulation = true;
                plant.message = "Sequence 2 running...";
                plant.timeStep(25);
                plant.blow(plant.pump_1);
                plant.timeStep(140);
                plant.message = " ";
                plant.stopReactor();
		plant.isRunningSimulation = false;
		plant.sequencePanel.enable();
		plant.stopButton.disable();
              }
          }


/**
 * Class for the Sequence 3 button.
 *
 * @version 1.0f
 * @author Henrik Eriksson
 */
          class Seq_3_Button extends SeqButton {
              /** Construct a button and initialize it.
                * @param p The plant
                * @param name The button label
                * @param x The X coordinate
                * @param y The Y coordinate */
              public Seq_3_Button(NuclearPlant p, String name) {
                super(p, name);
              }

              /** Run the sequence. This method is called in a
                  separate thread when the button is selected by the
                  user. Modify this method to change the sequence in
                  question. */
              public void run() {
		plant.sequencePanel.disable();
		plant.stopButton.enable();
                plant.startReactor();
		plant.isRunningSimulation = true;
                plant.message = "Sequence 3 running...";
                plant.timeStep(25);
                plant.blow(plant.pump_3);
                plant.timeStep(200);
                plant.message = " ";
                plant.stopReactor();
		plant.isRunningSimulation = false;
		plant.sequencePanel.enable();
		plant.stopButton.disable();
              }
          }


/**
 * Class for the random sequence button.
 *
 * @version 1.0f
 * @author Henrik Eriksson
 */
          class Rand_Button extends SeqButton {
              /** Construct a button and initialize it.
                * @param p The plant
                * @param name The button label
                * @param x The X coordinate
                * @param y The Y coordinate */
              public Rand_Button(NuclearPlant p, String name) {
                super(p, name);
              }

              /** Run the sequence. This method is called in a
                  separate thread when the button is selected by the
                  user. Modify this method to change the sequence in
                  question. */
              public void run() {
		plant.sequencePanel.disable();
		plant.stopButton.enable();
                plant.startReactor();
		plant.isRunningSimulation = true;
                plant.message = "Random sequence running...";
                plant.timeStep(25);
                switch ((int)(Math.random()*5)) {
                  case 0: plant.blow(plant.turbine); break;
                  case 1: plant.blow(plant.pump_1); break;
                  case 2: plant.blow(plant.pump_3); break;
                  case 3: plant.blow(plant.condenser); break;
                  case 4: plant.blow(plant.reactor); break;
                }
                plant.timeStep(25);
                switch ((int)(Math.random()*5)) {
                  case 0: plant.blow(plant.turbine); break;
                  case 1: plant.blow(plant.pump_1); break;
                  case 2: plant.blow(plant.pump_3); break;
                  case 3: plant.blow(plant.condenser); break;
                  case 4: plant.blow(plant.reactor); break;
                }
                plant.timeStep(140);
                plant.message = " ";
                plant.stopReactor();
		plant.isRunningSimulation = false;
		plant.sequencePanel.enable();
		plant.stopButton.disable();
              }
          }


/**
 * Class for the stop button.
 *
 * @version 1.0f
 * @author Henrik Eriksson
 */
          class Stop_Button extends Button {

              /** Backpointer to the NuclearPlant applet */
              protected NuclearPlant plant;

              /** Construct a button and initialize it.
                * @param p The plant
                * @param name The button label */
              public Stop_Button(NuclearPlant p, String name) {
                super(name);
                plant = p;
              }

              /** Stop a running the sequence. Modify this method to change
                  the stop function.
                * @param evt the Event
                * @param arg the argument */
              public boolean action(Event evt, Object arg) {
		plant.isRunningSimulation = true;
                plant.buttonThread.stop();
                plant.stopReactor();
                plant.message = " ";
		plant.isRunningSimulation = false;
		disable();
		plant.sequencePanel.enable();
		return true;
              }
          }


/**
 * Class for components of the nuclear power plant
 *
 * @version 1.0f
 * @author Henrik Eriksson
 */
          class Component {
              /** Backpointer to the plant */
              protected static NuclearPlant plant;

              /** Component location */
              protected int x, y;

              /** Component label (if any) */
              protected String label;

              /** True iff component broken */
              boolean blown = false;

              /** Returns an image given its name
                * @param name The image name 
                * @return The image */
              protected final Image getImage(String name) {
                Image im = plant.getImage(plant.getCodeBase(), plant.imageDir + name);
                im.getWidth(plant);  // Initiate image fetching
                return im;
              }

              /** Returns an image given its name
                * @param name The image name 
                * @param cache The image cache
                * @return The image */
              protected final Image getImage(String name, Image cache) {
                if (cache instanceof Image) return cache;
                else return getImage(name);
              }


              /** No-op at this level */
              void blow() {}

              /** Set the value of a slot (no-op at this level)
                * @param slot The slot name
                * @param val The value */
              void setIntValue(String slot, int val) { }

              /** Set the value of a slot (no-op at this level)
                * @param slot The slot name
                * @param val The value */
              void setValue(String slot, float val) { }

              /** Set the value of a slot (no-op at this level)
                * @param slot The slot name
                * @param val The value */
              void setValue(String slot, String val) { }

              /** Set the value of a slot
                * @param slot The slot name
                * @param val The value */
              void setValue(String slot, boolean val) {
                if (slot.equals("blown")) blown = val;
              }

              /** Random number generator (pseudo)
                * @param min The lower bound
                * @param max The upper bound
                * @return A random number (between min and max) */
              protected final int rand(int min, int max) {
                return (int)(Math.random()*(max-min) + min);
              }

          }

/**
 * A generic tank component for the nuclear power plant
 *
 * @version 1.0f
 * @author Henrik Eriksson
 */
          class Tank extends Component {

              /** The tank pressure (in bar) */
              float pressure;

              /** The tank waterlevel (in mm) */
              float waterLevel;

              /** The number of the water image shown */
              protected int imageState = 0;

              /** Water (surface) images for animation */
              protected static Image water_bm[];

              /** Blow up the tank */
              void blow() {
                plant.crashSound(15000);
                plant.getAppletContext().showStatus("The " + label + " blow up");
                blown = true;
              }

              /** Set the value of a slot
                * @param slot The slot name
                * @param val The value */
              void setValue(String slot, float val) {
                if (slot.equals("pressure")) pressure = val;
                else if (slot.equals("waterLevel")) waterLevel = val;
                else super.setValue(slot, val);
              }

              /** Array of bubble depths */
              private int bubble_depth[] = new int[100];

              /** Array of bubble X positions */
              private int bubble_x[] = new int[100];

              /** Paint animated bubbles in a region
                * @param g The graphics context
                * @param x The X position for the bubble region
                * @param y The Y position for the bubble region
                * @param size().width The size().width of the bubble region
                * @param size().height The size().height of the bubble region
                * @param noOfBubbles The bubble frequency */
              protected final void paintBubbles(Graphics g, int x, int y,
                               int width, int depth, int noOfBubbles) {
                  if (plant.isRunningSimulation) {
                    Color oldForeground = g.getColor();
                    g.setColor(Color.white);
                    if (noOfBubbles > bubble_depth.length)
                      noOfBubbles = bubble_depth.length;
                    for (int i=0; i < noOfBubbles; i++) {
                      if (bubble_depth[i] == 0) {     // Create a new bubble
                        bubble_depth[i] = rand(1, depth);
                        bubble_x[i] = rand(x, width);
                      }
                      g.fillRect(bubble_x[i], y+bubble_depth[i]--, 1, 1);
Thread.yield();
                    }
                    g.setColor(oldForeground);
                  }
              }
          }


/**
 * A reactor tank component for the nuclear power plant
 *
 * @version 1.0f
 * @author Henrik Eriksson
 */
          class Reactor extends Tank {

              /** The moderator rod level (in percent) */
              int moderatorPercent = 50;

              /** True iff the reactor is overheated */
              boolean overheated;

              /** The state of a meltdown */
              private int meltStage;

              /** Water (surface) images for animation */
              protected static Image water_bm[] = new Image[3];

              /** Image for a blown reactor tank */
              protected static Image crashed_reactor_bm;

              /** Radiation sign (image) */
              protected static Image radiak_bm;

              Reactor() {
                  water_bm[0] = getImage("R_VATTEN_A_BM.GIF", water_bm[0]);
                  water_bm[1] = getImage("R_VATTEN_B_BM.GIF", water_bm[1]);
                  water_bm[2] = getImage("R_VATTEN_C_BM.GIF", water_bm[2]);
                  crashed_reactor_bm =
                        getImage("CRASHED_REACTOR_BM.GIF", crashed_reactor_bm);
                  radiak_bm = getImage("RADIAK_BM.GIF", radiak_bm);
                  label = "reactor";
                  waterLevel = 1800;
              }

              /** Set the value of a slot
                * @param slot The slot name
                * @param val The value */
              void setIntValue(String slot, int val) {
                if (slot.equals("moderatorPercent")) moderatorPercent = val;
                else super.setIntValue(slot, val);
              }

              /** Set the value of a slot
                * @param slot The slot name
                * @param val The value */
              void setValue(String slot, boolean val) {
                if (slot.equals("overheated")) {
                  overheated = val;
                  if (val) meltdown();
                } else super.setValue(slot, val);
              }

              /** Advance the water-surface animation one step */
              void waterWave () {
                if (++imageState >= water_bm.length) imageState = 0;
              }

              /** Perform the meltdown animation. Called when the reactor
                  core is overheated */
              public void meltdown() {
                overheated = true;
                plant.kickerDelay = 40;
                meltStage = 5;
                plant.getAppletContext().showStatus("The " + label + " is overheated");
              }

              /** Paint operation for the meltdown animation
                * @param g The graphics context */
              public void paintMeltdown(Graphics g) {

                //im = new Image(item.parent);
                //offscreen = new Graphics(im);

//                int x0 = 10, y0 = 150;
                int x0 = -60, y0 = 110;
                if (meltStage == 5) g.drawImage(radiak_bm, 170, 150, plant);
                if (meltStage < 1500) {
                  int d = meltStage;
                  int x = Math.max(x0 - (d - 100), 0) +
                          rand(0, Math.min(d, plant.size().width));
                  int y = Math.max(y0 - (d - 100), 0) +
                          rand(0, Math.min(d, plant.size().height));
                  // BITBLT
                  g.copyArea(x, y,
                           rand(10, 100),
                           rand(10, 100),
                           ((d < 250) ? rand(-3, 3) : rand(-2, 2)),
                           ((d < 250) ? rand(-3, 3) : rand(-2, 2)));
                  meltStage++;
                }
              }


              /** The color of overheated fuel rods */
              private static Color overheatedColor = new Color(200,0,0);

              /** The color of water */
              private static Color waterColor = new Color(128,192,255);


              /** Paint operation for the reactor tank
                * @param g The graphics context */
              public void paint(Graphics g) {
                  g.setColor(Color.black);
                  if (pressure > 500) g.setColor(Color.red);
                  g.drawString((int)pressure+" bar", 55, 70);
                  int surfaceY = 175-(int)waterLevel/50;
                  g.setColor(waterColor);
                  g.fillRect(3, surfaceY+1, 135, 279-surfaceY);
                  g.setColor(Color.black);
                  g.drawImage(water_bm[imageState], 3, surfaceY, plant);
                  g.drawString("Level:", 160, 110);
                  if (waterLevel < 0) g.setColor(Color.red);
                  g.drawString((int)waterLevel+" mm", 160, 125);
                  g.setColor(Color.gray);
                  for (int i = 0; i < 7; i++)
                      g.fillRect(33 + i*12, 102+moderatorPercent*3/4, 3, 80);
                  if (moderatorOutlinePos > 0) {
                    g.setColor(Color.yellow);
                    for (int i = 0; i < 7; i++)
                      g.drawRect(33 + i*12, moderatorOutlinePos, 2, 80);
                  }
                  if (waterLevel < 0) {
                    Color hotColor =
                            new Color(Math.min(-(int)waterLevel/10,150),0,0);
                    g.setColor(hotColor);
                  } else g.setColor(Color.black);
                  for (int i = 0; i < 8; i++)
                      g.fillRect(25 + i*12, 177, 7, 80);
                  if (waterLevel < 0) {
                    g.setColor(overheatedColor);
                    for (int i = 0; i < 8; i++)
                        g.fillRect(25 + i*12, 177, 7,
                             Math.max(-(int)waterLevel/50-1, 0));
                  }
                  g.setColor(Color.black);
                  if (waterLevel > 0)
                    paintBubbles(g, 3, surfaceY, 137, 50, 50);
                  else
                    paintBubbles(g, 3, surfaceY, 137, 60, 100);
                  if (blown) {
                    g.drawImage(crashed_reactor_bm, 30, 3, plant);
                    g.drawImage(radiak_bm, 170, 150, plant);
                  }
              }

              /** Initial Y position for dragged moderator rods */
              private int y0 = 0;

              /** The distance of the drag */
              protected int dragDelta = 0;

              /** The position of the outline of the dragged moderator rods */
              protected int moderatorOutlinePos = 0;

              /** Checks if a certain position is inside the area of
                  the moderator rods
                * @param x The X coordinate
                * @param y The Y coordinate
                * @return True iff the position is inside the moderator-rod
                          area, otherwise false */
              private boolean isInsideModerator(int x, int y) {
                return x > 25 && x < 120 && y > 102+moderatorPercent*3/4 &&
                       y < 102+moderatorPercent*3/4 + 80;
              }

              /** Handle mouseDown events (i.e., clicks on moderator rods).
                * @param x The X coordinate
                * @param y The Y coordinate */
              public boolean mouseDown(Event evt, int x, int y) {
                if (isInsideModerator(x, y)) {
                  y0 = y;
                }
		return true;
              }

              /** Handle mouseDrag events. This method allows the user to
                  drag moderator rods
                * @param x The X coordinate
                * @param y The Y coordinate */
              public boolean mouseDrag(Event evt, int x, int y) {
                if (y0 > 0) {
                  moderatorOutlinePos = 102+moderatorPercent*3/4 - (y0 - y);
                  if (moderatorOutlinePos < 97) moderatorOutlinePos = 97;
                  if (moderatorOutlinePos > 177) moderatorOutlinePos = 177;
                }
		return true;
              }

              /** Handle mouseUp events. This method sets the moderatorPercent
                * @param x The X coordinate
                * @param y The Y coordinate */
              public boolean mouseUp(Event evt, int x, int y) {
                if (y0 > 0 && moderatorOutlinePos > 0) {
                  moderatorPercent = (moderatorOutlinePos - 97) * 5/4;
                }
                moderatorOutlinePos = 0;
                y0 = 0;
		return true;
              }


          }


/**
 * A valve component for the nuclear power plant
 *
 * @version 1.0f
 * @author Henrik Eriksson
 */
          class Valve extends Component {

              /** True iff the valve is open */
              boolean status = false;

              /** Valve image */
              protected static Image ventil_o_bm, ventil_s_bm;

              /** Construct a valve and initialize it.
                * @param l The valve label
                * @param xPos The X coordinate
                * @param yPos The Y coordinate */
              Valve(String l, int xPos, int yPos) {
                  x = xPos;
                  y = yPos;
                  label = l;
                  ventil_o_bm = getImage("VENTIL.O_BM.GIF", ventil_o_bm);
                  ventil_s_bm = getImage("VENTIL.S_BM.GIF", ventil_s_bm);
              }

              /** Set the value of a slot
                * @param slot The slot name
                * @param val The value */
              void setValue(String slot, boolean val) {
                if (slot.equals("status")) status = val;
                else super.setValue(slot, val);
              }

              /** Paint operation for valve
                * @param g The graphics context */
              public void paint(Graphics g) {
                  if (status)
                     g.drawImage(ventil_o_bm, x, y, plant);
                  else
                     g.drawImage(ventil_s_bm, x, y+6, plant);
                  g.drawString(label, x, y+40);
              }

              /** Handle mouseDown events (i.e., clicks on the valve).
                * @param mx The X coordinate
                * @param my The Y coordinate */
              public boolean mouseDown(Event evt, int mx, int my) {
                if (mx > x && mx < x+20 && my > y && my < y+40) {
                  status = !status;
                }
		return true;
              }

          }


/**
 * A pump component for the nuclear power plant
 *
 * @version 1.0f
 * @author Henrik Eriksson
 */
          class Pump extends Component {

              /** The state of the pump (0=crashed) */
              int status = 1;

              /** The pump rpm */
              int rpm;

              /** Maximum pump rpm */
              int full_rpm;

              /** Pump images for amimation */
              protected static Image pump_bm[] = new Image[6];

              /** Crashed pump image */
              protected static Image pump_crash_bm;


              /** Construct a pump and initialize it.
                * @param l The pump label
                * @param xPos The X coordinate
                * @param yPos The Y coordinate
                * @param maxRpm The maximum pump rpm */
              Pump(String l, int xPos, int yPos, int maxRpm) {
                  x = xPos;
                  y = yPos;
                  label = l;
                  full_rpm = maxRpm;
                  pump_bm[0] = getImage("PUMP.A_BM.GIF", pump_bm[0]);
                  pump_bm[1] = getImage("PUMP.B_BM.GIF", pump_bm[1]);
                  pump_bm[2] = getImage("PUMP.C_BM.GIF", pump_bm[2]);
                  pump_bm[3] = getImage("PUMP.D_BM.GIF", pump_bm[3]);
                  pump_bm[4] = getImage("PUMP.E_BM.GIF", pump_bm[4]);
                  pump_bm[5] = getImage("PUMP.F_BM.GIF", pump_bm[5]);
                  pump_crash_bm = getImage("PUMP.CRASH_BM.GIF", pump_crash_bm);
              }


              /** Set the value of a slot
                * @param slot The slot name
                * @param val The value */
              void setIntValue(String slot, int val) {
                if (slot.equals("status")) status = val;
                else if (slot.equals("rpm")) rpm = val;
                else super.setIntValue(slot, val);
              }

              /** Blow up the pump */
              void blow() {
                plant.crashSound(800);
                plant.getAppletContext().showStatus(label + " crashed");
                status = 0;
                rpm = 0;
              }

              /** Rotate the pump one step. (Only pumps with rpm > 0
                  are rotated.) */
              public void rotate() {
                if (rpm > 0 && status != 0) status++;
                if (status > 6) status = 1;
              }

              /** Paint operation for pump
                * @param g The graphics context */
              public void paint(Graphics g) {
                  if (status == 0) g.drawImage(pump_crash_bm, x, y, plant);
                  else g.drawImage(pump_bm[status-1], x, y, plant);
                  g.drawString(rpm+" rpm", x+4, y-2);
              }

              /** Handle mouseDown events (i.e., clicks on the pump).
                * @param mx The X coordinate
                * @param my The Y coordinate */
              public boolean mouseDown(Event evt, int mx, int my) {
                if (mx > x && mx < x+30 && my > y && my < y+30)
                  if (status != 0)
                    if (rpm == 0) rpm = full_rpm; else rpm = 0;
		return true;
              }

          }



/**
 * Turbine component for the nuclear power plant
 *
 * @version 1.0f
 * @author Henrik Eriksson
 */
          class Turbine extends Component {

              /** Image of crash turbine */
              protected static Image crashed_turbine_bm;

              /** Construct a turbine and initialize it. */
              Turbine() {
                crashed_turbine_bm =
                      getImage("CRASHED_TURBIN_BM.GIF", crashed_turbine_bm);
              }

              /** Blow up the turbine */
              void blow() {
                  blown = true;
              }

              /** Count down for blow up animation */
              private byte blow_countdown = -1;

              /** Paint operation for turbine
                * @param g The graphics context */
              public void paint(Graphics g) {
                  if (blown && blow_countdown < 0) {
                    blow_countdown = 20;
                  } else if (blown && blow_countdown < 10) {
                    g.drawImage(crashed_turbine_bm, 403, 73, plant);
                    if (blow_countdown == 8) {
                      plant.getAppletContext().showStatus("The turbine crashed");
                      plant.crashSound(8000);
                    }
                  } 
                  if (blown && blow_countdown > 0) {
                    blow_countdown--;
                    g.copyArea(403, 95, 145, 65,
                               403+rand(-2,2), 95+rand(-4,2));
                  } else if (!blown) blow_countdown = -1;
              }
          }


/**
 * Condenser component for the nuclear power plant
 *
 * @version 1.0f
 * @author Henrik Eriksson
 */
          class Condenser extends Tank {

              /** Water (surface) images for animation */
              protected static Image water_bm[] = new Image[3];

              /** Image of cooling pipe */
              protected static Image kylror_bm;

              /** Image of crashed condenser */
              protected static Image crashed_condenser_bm;

              /** Construct a condenser and initialize it. */
              Condenser() {
                  water_bm[0] = getImage("K_VATTEN_A_BM.GIF", water_bm[0]);
                  water_bm[1] = getImage("K_VATTEN_B_BM.GIF", water_bm[1]);
                  water_bm[2] = getImage("K_VATTEN_C_BM.GIF", water_bm[2]);
                  crashed_condenser_bm =
                     getImage("CRASHED_KONDENSOR_BM.GIF",crashed_condenser_bm);
                  kylror_bm = getImage("KYLROR_BM.GIF", kylror_bm);
                  label = "condenser";
                  waterLevel = 6000;
              }

              /** Advance the water-surface animation one step */
              void waterWave () {
                if (++imageState >= water_bm.length) imageState = 0;
              }

              /** The color of water */
              private static Color waterColor = new Color(128,192,255);

              /** Paint operation for condenser
                * @param g The graphics context */
              public void paint(Graphics g) {
                  if (pressure > 200) g.setColor(Color.red);
                  g.drawString((int)pressure+" bar", 520, 250);
                  g.setColor(waterColor);
                  int surfaceY = 440 - (int)waterLevel / 50;
                  g.fillRect(483, surfaceY+1, 108, 449-surfaceY);
                  g.drawImage(kylror_bm, 500, 349, plant);
                  g.setColor(Color.black);
                  g.drawImage(water_bm[imageState], 483, surfaceY, plant);
                  paintBubbles(g, 483, surfaceY, 590, 12, 8);
                  g.drawString("Level:", 615, 290);
                  g.drawString((int)waterLevel+" mm", 615, 305);
                  if (blown) {
                    g.drawImage(crashed_condenser_bm, 570, 214, plant);
                  }
              }
          }


/**
 * Generator component for the nuclear power plant
 *
 * @version 1.0f
 * @author Henrik Eriksson
 */
          class Generator extends Component {

              /** Output generator power (in MW) */
              int power;

              /** Set the value of a slot
                * @param slot The slot name
                * @param val The value */
              void setIntValue(String slot, int val) {
                if (slot.equals("power")) power = val;
                else super.setIntValue(slot, val);
              }

              /** Paint operation for generator
                * @param g The graphics context */
              public void paint(Graphics g) {
                  g.drawString(power+" MW", 570, 95);
              }
          }



/**
 * The class Simulator is an abstact class for power-plant
 * simulators. The actual simulators are subclasses of this class.
 *
 * @see NuclearPlant
 * @version 1.0f
 * @author Henrik Eriksson
 */
          abstract class Simulator {
              /** The plant */
              protected NuclearPlant plant;
              abstract void start();
              abstract void timeStep(int n);
              abstract void blow(Component device);
              abstract void stop();
          }

/**
 * The class LocalSimulatorServer is a test class that simulates the
 * behavior of the simulator server. It is used for testing the
 * java client applet and its user interface.
 *
 * @see TestSimulatorServer
 * @see ClipsSimulatorServer
 * @version 1.0f
 * @author Henrik Eriksson
 */
         class LocalSimulator extends Simulator {
              /** The reactor and its components */
              protected Reactor reactor;    

              /** Valve object */
              protected Valve valve_1, valve_2, valve_3, valve_4;

              /** Pump object */
              protected Pump pump_1, pump_2, pump_3;

              /** The turbine */
              protected Turbine turbine;

              /** The condenser */
              protected Condenser condenser;

              /** The generator */
              protected Generator generator;


              /** Constructor for LocalSimulator. Wires objects based on the
                * NuclearPlant components.
                * @param p The current NuclearPlant object */
              public LocalSimulator(NuclearPlant p) {
                plant = p;
                reactor = p.reactor;
                valve_1 = p.valve_1;
                valve_2 = p.valve_2;
                valve_3 = p.valve_3;
                valve_4 = p.valve_4;
                pump_1 = p.pump_1;
                pump_2 = p.pump_2;
                pump_3 = p.pump_3;
                turbine = p.turbine;
                condenser = p.condenser;
                generator = p.generator;
              }

              /** Starts the local simulator. This method initializes
                * the simulation variables (i.e., the plant is reset
                * to steady state). */
              public void start() {
                reactor.pressure = 288;
                reactor.moderatorPercent = 50;
                reactor.waterLevel = 1800;
                reactor.blown = false;
                reactor.overheated = false;
                turbine.blown = false;
                condenser.pressure = 40;
                condenser.waterLevel = 6000;
                condenser.blown = false;
                generator.power = 622;
                pump_1.status = 1;
                pump_1.rpm = 1400;
                pump_2.status = 1;
                pump_2.rpm = 0;
                pump_3.status = 1;
                pump_3.rpm = 1285;
                pump_1.blown = false;
                pump_2.blown = false;
                pump_3.blown = false;
                valve_1.status = true;
                valve_2.status = false;
                valve_3.status = true;
                valve_4.status = false;
              }


              /** Calculates the next state of the simulation. This
                * calculation is perfomed locally in java.
                * @param n The number of steps to calculate before returning */
              public void timeStep(int n) {
                float v1, v2, v3, v4;

                for (int i = 0; i < n; i++) {
                  if (!reactor.overheated) {
                    // Compute the flow through valve_1...
                    if (valve_1.status)
                      v1 = (reactor.pressure-condenser.pressure) / 10;
                    else v1 = 0;
                    // Compute the flow through valve_2...
                    if (valve_2.status)
                      v2 = (reactor.pressure-condenser.pressure) / 2.5f;
                    else v2 = 0;

                    // Compute the flow through valve_3 and pump_1...
                    if (valve_3.status)
                      if (pump_1.rpm > 0)
                        if (condenser.waterLevel > 0)
                          v3 = pump_1.rpm * 0.07f;
                        else v3 = 0;
                      else v3 = -30;
                    else v3 = 0;

                    // Compute the flow through valve_4 and pump_2...
                    if (valve_4.status)
                      if (pump_2.rpm > 0)
                        if (condenser.waterLevel > 0)
                          v4 = pump_2.rpm * 0.07f;
                        else v4 = 0;
                      else v4 = -30;
                    else v4 = 0;

                    // Scale the flow levels to allow frequent time steps
                    // (smother animation)
                    float factor = 0.5f;
                    v1 *= factor;
                    v2 *= factor;
                    v3 *= factor;
                    v4 *= factor;

                    // Compute new values for pressure and water levels...
                    float boiledRW = (100 - reactor.moderatorPercent)*2*(900 - reactor.pressure)/620;
                    boiledRW *= factor;

                    float cooledKP = (float)(pump_3.rpm * Math.sqrt(condenser.pressure) * 0.003f);
                    cooledKP *= factor;

                    float newRP = reactor.pressure - v1 - v2 + boiledRW/4;

                    // The steam flow to the condenser stops if the
                    // turbine is blown...
                    if (turbine.blown) v1 = 0;

                    // Compute new values for pressure and water levels...
                    float newKP = condenser.pressure + v1 + v2 - cooledKP;
                    float newRW = reactor.waterLevel + v3 + v4 - boiledRW;
                    float newKW = condenser.waterLevel - v3 - v4 + 4*cooledKP;

                    // Make adjustments for blown tanks...
                    if (reactor.blown) newRP = 0.15f * newRP;
                    if (condenser.blown) newKP = 0.2f * newKP;
                    // Check the computed values for illegal values...
                    if (newKW < 0) newKW = 0;
                    if (newKW > 9600) newKW = 9600;
                    if (newRW > 4700) newRW = 4700;
                    if (newKP < 0) newKP = 0;
                    if (newKP > 300) newKP = 300;
                    if (newRP > 800) newRP = 800;

                    // Adjust the generator power...
                    float newEffect;
                    if (valve_1.status && !turbine.blown)
                      newEffect = (newRP - newKP) * 2.5f;
                    else newEffect = 0;

                    // Assign the computed values...
                    generator.power = (int)newEffect;
                    condenser.pressure = newKP;
                    condenser.waterLevel = newKW;
                    reactor.pressure = newRP;
                    reactor.waterLevel = newRW;

                    // Rules for the plant...
                    if (condenser.waterLevel <= 0 && pump_1.rpm > 0)
                      pump_1.blow();
                    if (condenser.waterLevel <= 0 && pump_2.rpm > 0)
                      pump_2.blow();

                    if (pump_1.blown) pump_1.rpm = 0;
                    if (pump_2.blown) pump_2.rpm = 0;
                    if (pump_3.blown) pump_3.rpm = 0;
                    if (reactor.waterLevel < -1500) reactor.meltdown();
                    if (reactor.pressure >= 610) reactor.blow();
                    if (condenser.pressure >= 225) condenser.blow();

                    // R1 (safety rule for blown turbine)...
/* Disabled for now
                    if (turbine.blown) {
                      valve_1.status = false;
                      valve_2.status = true;
                      reactor.moderatorPercent = 100;
                      pump_1.rpm = 0;
                      valve_3.status = false;
                    }
*/

                  } // if

try {Thread.sleep(200); } catch (InterruptedException e){}

                }  // for

              }

              /** Blow up a device. 
                * @param device The device to blow (e.g., a turbine object) */
              public void blow(Component device) {
                device.blow();
              }

              /** Stop the simulation */
              public void stop() {
              }

          }
