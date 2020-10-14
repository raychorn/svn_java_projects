Welcome, beta testers!

    To run AMMail, install the class files in your class directory.
They're zipped in the proper subdirectories, but I'll list them 
at the end here for completeness.
    Next, copy ammail.cfg to whatever directory you want to run
AMMail from.  Note that you must run it from that directory every
time, or it won't be able to find its config files and your saved
mail!
    To run AMMail, use "java amm.mail.AMMail".

    The first time you run AMMail, go to the Preferences menu and
select "User and Servers".  Replace all the stuff there with your
actual address, server names, etc.  Next, select any of the other
options you choose from the main Preference menu.

"Save on Exit" will save your mail to disk every time you exit the
    program, unless you've saved it yourself since the last time
    you contacted the POP3 server.
    
"Remove on load" will remove your mail from the server as it loads
    it into AMMail - be sure to save your mail if you choose this
    option!
    
"Remove on delete" will remove a message from the server when you
    delete it in AMMail.
    
    AMMail stores the properties by user, so if you log on to your
workstation as a different user, you'll need to fill in the
properties again.  However, it only stores one mail file right now,
so it's not yet suitable for multiple users.
    
    Finally, you can select multiple items in the main window.
However, only "delete" has an affect on multiple selections.  If
you have more than one item selected and you press a different
button, it will simply clear all selections.

Aaron
ammulder@princeton.edu
http://www.princeton.edu/~ammulder/java.html

Files
-------
ammail.cfg              working directory
FrameTab.class          classes/
PanelTab.class          classes/
AMMail.class            classes/amm/mail
Compose.class           classes/amm/mail
DiskUpdate.class        classes/amm/mail
Mailer.class            classes/amm/mail
MailHeader.class        classes/amm/mail
MailProps.class         classes/amm/mail
MailSink.class          classes/amm/mail
MailSource.class        classes/amm/mail
NetUpdate.class         classes/amm/mail
POPHandler.class        classes/amm/mail
POPState.class          classes/amm/mail
