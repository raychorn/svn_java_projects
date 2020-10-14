JAVA APPLETS FROM EARTHWEB

All rights in and to the Java applets developed by EarthWeb 
(the "Software"), including, but not limited to, copyrights and 
trade secret rights, belong to EarthWeb. EarthWeb grants to Sams.net 
Publishing ("Sams.net") a non-exclusive and non-transferable license 
to copy and distribute the Software to users as part of the "Java Power 
Pack" only. Neither Sams.net nor any user of the Software has any right 
to sell, transfer, disclose, display or otherwise make available the 
Software or any copies or portions thereof to any person other than a 
person or entity permitted by the terms hereof. The software is licensed 
for non-commercial use; contact EarthWeb at mwork@earthweb.com for more 
information on using these applets in a commercial site.

We have chosen for this CDROM a small collection of entertaining and
interesting Java applets written by the programmers at EarthWeb.

Most of these applets can be seen on a Java-enabled browser, such as
Netscape 2.0.  There are two which are labelled "best seen/played in
appletviewer."  These exhibit problems with the current version of
Netscape (2.0 beta 5) but work well invoked with the program "appletviewer,"
which is distributed with the Java Development Kit (JDK) available from
Sun's java site http://www.javasoft.com.

If you use Netscape to view an applet, simply start up Netscape and
enter the location of the applet's index.html file.  Depending on your
setup procedure for this CDROM, this can be a full URL:

    http://www.someplace.com/java/Bounce/index.html

or a filename:

    file:/home/user/myname/Bounce/index.html

The applet will then appear in your browser window.

If you use appletviewer, invoke appletviewer with the full URL of the
applet as the argument:

    appletviewer http://www.someplace.com/java/Bounce/index.html

or just the filename as the argument:

    appletviewer /home/user/myname/Bounce/index.html


The Java source code for most of these applets can be found at EarthWeb's
Java resource site "Gamelan" (http://www.gamelan.com).  Gamelan is both
a registry and directory of Java applets, source, and any other resources
involving Java.

Enjoy!

info@earthweb.com


--------------------------------------------------------------------------

               Index of Java applets with descriptions


Gravity
Gravity is our Java version of a shoot-'em-up arcade-style
game.  Use the keyboard command keys to rotate and move the ship as well as
fire or jam on the brakes.  The command-key breakdown follows:

    ::Play Mode::
    z        rotate ship left     x        rotate ship right
    ,        thrust               .        fire
    [space]  shields              [tab]    brakes
    e        enter Edit Mode      [mouse]  enter Edit Mode

    ::Edit Mode::
    [mouse]  draw walls/bring into focus the wall or target nearest to mouse
             cursor
    a        add enemy target to the wall in focus (where the red dot is)
    d        delete the target or wall in focus

One exciting feature of Gravity is its ability to allow the user to build/
edit the game world visually -- right on the playing screen.  To toggle Edit
Mode, hit the "e" key.  Once in Edit Eode, draw walls by clicking, releasing,
then dragging the mouse in the direction you wish.  Click again to place the
wall.  Now move the mouse close to any wall.  You will see a red dot appear.
This dot is the proposed placement of a new enemy target as well as an
indicator of which wall is currently "in focus."  Hit the "a" key to add this
target or "d" to either delete an added target or delete the wall which is in
focus.  Hit "e" again to exit Edit Mode.

Currently there is no score-keeping but keep your eye on Gamelan
(http://www.gamelan.com) for the latest version!



3D Netris
Netris is EarthWeb's 3-dimensional Java version of a block-dropping 
game.  Click and drag in the window to rotate the board to any 
zany viewpoint.  The keyboard commands to play follow: 

      ::Keyboard controls::
      h        move piece left             l   move piece right,
      j        rotate piece to the left    k   rotate piece to the right
      [space]  drop piece
        


Bounce
"Fang," EarthWeb's logo/mascot, has been animated in this
applet.  It travels inside a rectangle (whose size is designated in the
"width" and "height" parameters of the  tag), bouncing off
and away from the applet's boundary walls with velocity fluctuations
according to an algorithm.  Note that the applet is flicker-free due to
the programming method known as double-buffering.  First, draw to a
graphics object off-screen, then swap it in one fell swoop to the
screen.  By overriding the Applet object's default update() method (which
first clears a grey rectangle, causing flickering) the programmer is able to
circumvent the annoying flicker.



Maze
Self-solving randomly generated maze.  The maze is
different each time.  Click anywhere in the maze to watch it solve itself.
The starting point is the lower left-hand corner of the maze and the goal is
the upper-right corner.



Moire
Moire is a graphical excursion.  Lines sweep around a central point
in opposite directions, change colors, then trace back over themselves.
This applet features thread suspend and resume: click on the applet once
to suspend it; click on it once again to resume drawing.



Mandelbrot
The Mandelbrot Set is the most well-known of Iterated Function System
fractals.  This applet allows you to zoom into any point in the set.  It
uses two separate threads: one to progressively render the image in higher
and higher resolution and one to display the rendered image.  It accepts
parameters from the HTML page: originx, originy, and mag.  (Best viewed
in appletviewer.)

	::Key comands::
   +          zoom in
   -          zoom out
   [mouse]    click to make cursor the new center

      

Jigsaw Puzzle
This highly-customizable applet splits a randomly selected picture into
a number of rectangular pieces which are shuffled.  Click and drag each piece
from the left frame into the right and watch it slide into the nearest
blocked section.  Reconstruct the original image in the right-hand frame.
The applet will notify you when you have succeeded.

This applet takes parameters defined in HTML <PARAM> tags to specify what
picture files to choose from and how many rows and columns the disseminated
picture should have.



Spline Editor
Splines are used in animation development.  Graphical spline editors
make the calculation of object/camera movement much simpler than the
alternative method of manual number-crunching.  This is a spline editor
written in Java.  You can add, delete, and drag points with your mouse.
Keyboard commands are as follows:

    ::Keyboard commands::
    [mouse]  select and drag points, place cursor for a new point or to bring
             the point nearest cursor into focus
    a        add a point
    d        delete the point in focus (nearest the mouse cursor)

        

Thingy
Thingy demonstrates several concepts: some mouse events (click, drag),
available to Java applets, the flicker side-effect of the applet's update()
method, and the Moire effect of your monitor.



Throwable Ball
This is "Bounce" with a twist.  Taking advantage of mouse events, this
applet allows you to catch Fang (by clicking on it) and then throw it in any
direction (by dragging the mouse in a direction and then releasing the mouse
button).  The faster you "throw" Fang, the quicker it moves, until it hits a
wall and rebounds at a velocity corresponding to your throw.  After releasing
Fang, try catching it!



Remember: Check Gamelan (http://www.gamelan.com) for updates of these and
other applets.

