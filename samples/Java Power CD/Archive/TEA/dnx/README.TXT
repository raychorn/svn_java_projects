=============================================================
* Copyright (c) 1996 Dimension X, Inc. All Rights Reserved.
*
* Dimension X MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT
* THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
* MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
* OR NON-INFRINGEMENT.
* Dimension X SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
* LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
* THIS SOFTWARE OR ITS DERIVATIVES.
=============================================================

Win32 install:

Assumes you installed Sun Microsystems' 1.0 Java Developers' Kit
in C:\Java.  You can edit the tea.bat to adjust the drive and
classpath.

Install Liquid Motion (formerly known as TEA) to C:\Java, so that 
the files end up in:

C:\Java\classes\dnx\jack ...
C:\Java\classes\dnx\tea ...
C:\Java\dnx\tea.bat
etc.

Then, use the tea.bat file included herein to run Liquid Motion.

================================
ENVIRONMENT VARIABLES:

The CLASSPATH variable tells the java runtime where to look for
classes.  It must include the "classes" root of the DimensionX
class tree.

================================
Usage:

Look at the tea.bat file to see the use of the -T tea directory
option.  This tells Liquid Motion where the button images are, 
etc. You can adjust this to suit your installation.

Liquid Motion starts with an empty scene.  You add still images, 
images sequences and audio sequences to this scene.  You can 
adjust the placement and pacing of animations, the timing (i.e. 
start time) of image and audio sequences, and length and looping
characteristics.

Sequences can play once and stop (after which they disappear),
or loop indefinitely.  If you want to make something
loop for a while, and then stop, use the ForcedDuration to
override the sequence duration.

The scene has an associated project directory, which is where
the .jck file is saved (this is the scene description file).
You must set this by saving the new scene, before you
add any media.

In the scene Editor:
Selected elements have reverse video (negative look) around them,
unless they fill the screen (i.e. backgrounds), in which case
the whole pic is reversed.  
When an element is selected, you can move it by dragging the
mouse.
You can also set the properties (Element menu), or add motion
and/or time behaviors.

Simple tutorial:
Set the scene directory using SaveAs on the new scene.
Add a background picture:
  - Select the Edit->Add Image Menu
  - Select single image radio button.
  - Browse for the file
  - Click OK (or press Enter)
  - Select Edit->Properties menu and enable position lock.
      (this is so you do not move it by accident).

Add an image sequence:
  - Select the Edit->Add Image Menu
  - Select image sequence
  - Browse for the first file in the sequence (e.g. Pic1.gif)
  - Set the number of images in the sequence
  - Set the pacing of the image
  - Click OK (or press Enter)
  - Select Edit->Properties menu and enable the looping,
      if you want the image to keep sequencing.

Add a motion path:
  - Select the Behaviors->Motion->DrawnPath menu
  - Draw a motion path on the scene by dragging the mouse
  - Click OK (or press Enter)

The DrawnPath motion behavior lets you draw a path with the
mouse, which the image will follow as it animates.  Once
you commit the drawn path, you can translate the entire path
by dragging the image.
If you try to drag it while it is moving, you will
generally have trouble getting the position right. Instead,
pause the animation at some indicative point (or jog to the
position with the timeline control buttons).  Then drag the image
to the desired position, and replay the animation.


Play the sequence by pressing the play button on the
timeline control panel below the scene window.

Save the animation with the File->Save menu.

Export for HTML embedding with the File->Export menu.

Double click the .html file, to start your (java-enabled)
browser on the sequence.

To publish, move the html file and the classes,
images and audio sub-directories to your http tree.

Some more notes:

Note that a scaled time applies to the motion path as well, so
if you are trying to match the motion path to the animation,
the duration can be confusing if you already have a time scale.
(Do motion behaviors first, then add scaling).

See the release notes for last minute changes.
