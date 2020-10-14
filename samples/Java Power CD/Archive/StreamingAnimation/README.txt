
The Streaming Animation Player
   
     _________________________________________________________________
   
  What is it?
     
   The Streaming Animation Player from Dimension X is a set of Java(tm)
   classes which enable you to put animations on your web pages. These
   animations display as they download.


  What does it play?   
   
   Currently the Streaming animation applet supports the FLIC file format
   (both FLI and FLC), and will support a format which allows for better
   compression, and shorter download times.
   
   Right now you can use any standard FLIC encoder Dave's TARGA Animator
   works well or you can purchase Animator Pro from Autodesk which will
   also export FLIC files.


  How do I use it?
  
    1. Download either the zip file or tar file from Dimension X.
    2. Uncompress the file in a directory of your choice. A good choice
       would be the directory in which you plan to put the page
       containing your animation, though this is not required.
    3. The directory in which you uncompressed the file you downloaded
       will now contain a dnx directory which contains the class files
       needed to use the Streaming Animation Player.
       
     * Now just put the APPLET tag in your document.


  The APPLET Tag and you
   
   This is the tag used to show the animation at the top of the page:

<APPLET CODE="dnx.StrAnimItem.class" CODEBASE="./" WIDTH=320 HEIGHT=220>
<PARAM NAME="stranim" VALUE="my_animation.flc">
<PARAM NAME="dither" VALUE="better">
</APPLET>

   The HEIGHT and WIDTH arguments to the Streaming Animation Applet tell
   the applet at what size the applet should display your animation.
   However, these values do not have to be the actual dimensions of the
   animation that you are playing. Scaling of the animation will not
   occur, though, if only one of the HEIGHT and WIDTH arguments differs
   from that defined in the animation file itself. The parameters that
   you can pass to the Streaming Animation Player include "stranim", and
   "dither." Stranim gives the path to, and name of the animation which
   you wish to play. Dither allows you to select the method that will be
   used to dither the animation. The accepted values for dither are
   "better"(the default) and "faster." Faster dithering is (Surprise!)
   slightly faster than better dithering. However, some animations will
   not look as good when dithered faster.
   
   For more information about the APPLET tag try the HTML Reference
   Manual


  Details
  
   You must access pages containing streaming animtions through a Web
   server. That is, using Open File in your browser to view your page
   will not allow you to view the Streaming animations on your page.
   
   If there is more than one Streaming Animation on a page they will
   download in sequence.


  The Future
   
   Better compression and an encoder are on the way. For a licensing fee
   of $195.00(US) you will get updates to the current version of the
   decoder, e-mail support, and the encoder, when it is available. Source
   code licenses are also available - Contact Brad Karns
   <bk@dimensionx.com> at (213)957.0300 for licensing information.
     _________________________________________________________________
