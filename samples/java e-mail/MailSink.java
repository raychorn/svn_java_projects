package amm.mail;

import java.io.*;
import java.util.*;

/**
 * An interface to be implemented by classes that receive e-mail via
 * POPHandlers.  When a POPHandler is started, it runs through all
 * pending actions, and then closes the connection.  Thus, the
 * MailSink needs to start a new POPHandler when there is not one
 * currently running but a some new action is required.  The MailSink
 * must pass the POPHandler a PipedOutputStream before retrieving any
 * messages.
 * @author      Aaron Mulder
 * @see         amm.mail.POPHandler
 */
public interface MailSink {
/**
 * The flag indicating that the list of available messages should be
 * updated.
 */
  public final static int UPDATE=1;
/**
 * The flag indicating that all mail should be retrieved from the POP3
 * server.
 */
  public final static int GET_ALL=2;
/**
 * The flag indicating that all new mail should be retrieved from the
 * POP3 server.
 */
  public final static int GET_NEW=4;
/**
 * The flag indicating that one or more messages should be removed
 * from the POP3 server.
 */
  public final static int DELETE=8;
/**
 * The flag indicating that all messages should be removed from
 * the POP3 server.
 */
  public final static int DEL_ALL=16;

/**
 * Returns the address of the POP3 server the POPHandler should connect to.
 * @return      The desired address.
 */
  public String pop3Host();
/**
 * Returns the user name for the account on the POP3 server.
 * @return      The desired user name.
 */
  public String pop3User();
/**
 * Returns the password for the account on the POP3 server.
 * @return      The desired password.
 */
  public String pop3Pass();
/**
 * The POPHandler uses this to tell the MailSink how many messages are
 * waiting on the server.
 * @param num   The number of messages waiting.
 */
  public void numThere(int num);
/**
 * Returns a boolean indicating whether messages read from the POP3
 * server should be subsequently removed from the server.
 * @return      <TT>True</TT> if the messages <EM>should</EM> be
 *              removed.
 */
  public boolean remove();
/**
 * Receives status messages from the POPHandler.  <EM>status</EM> typically
 * displays the messages to a status bar or the Standard Output.
 * @param msg   The message from the POPHandler.
 */
  public void status(String msg);
/**
 * Returns an integer specifying the position of the message in the
 * mail client's list of known messages.  If the result is <TT>not
 * found</TT>, the message is considered to be new.  The position of
 * the last message found can be used to speed searches.
 * @param msgId     The SMTP ID of the message on the server.
 * @param last      The position of the last message found; <TT>-1</TT>
 *                  should cause the search to start at the beginning.
 * @return          The position of the message, or <TT>-1</TT> if the
 *                  message was not found.
 */
  public int verify(String msgId,int last);
/**
 * Returns an integer indicating the next action to be taken by the
 * POPHandler.
 * @return          One of the flags declared above.
 */
  public int runNext();
/**
 * Signifies that the POPHandler has finished, and any new actions need
 * to be carried out by a new POPHandler.
 */
  public void handlerDone();
/**
 * When a DELETE action is called for, the POPHandler uses this to
 * determine which messages should be removed.
 * @return          SMTP IDs of messages to be removed, one at each
 *                  element of the array.
 */
  public String[] delWhich();
}
