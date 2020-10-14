package amm.mail;

import java.io.*;
import java.util.*;

/**
 * An interface implemented by classes that send mail via a Mailer.
 * @see         amm.mail.Mailer
 * @author      Aaron Mulder
 */
public interface MailSource {

/**
 * Returns a list of addresses to cc the message to.
 * @return      Each address in an element of the array.
 */
  public String[] cc();
/**
 * Returns a list of addresses to send the message to.
 * @return      Each address is an element of the array.
 */
  public String[] to();
/**
 * Returns the address that the mail is originating from.
 * @return      The desired e-mail address.
 */
  public String from();
/**
 * Returns the name of the sender.
 * @return      The desired name.
 */
  public String name();
/**
 * Returns the subject of the message.
 * @return      The desired subject.
 */
  public String subject();
/**
 * This method allows the MailSource to send the content of the message.
 * @param out   A PrintStream to output the message body to.
 * @see         java.io.PrintStream
 */
  public void data(PrintStream out);
/**
 * Returns the SMTP server's address (ie. <TT>smtp.nowhere.com</TT>).
 * @return      The desired address.
 */
  public String host();
/**
 * Recevies status messages from the Mailer.  <EM>status</EM> typically
 * displays the messages to a status bar or the Standard Output.
 * @param msg   The message from the Mailer.
 */
  public void status(String msg);
/**
 * Called when the Mailer has completed the transaction.
 * @param succeeded     <TT>True</TT> if the mail was send successfully.
 */
  public void done(boolean succeeded);
}
