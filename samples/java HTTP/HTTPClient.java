package amm.http;

import java.net.*;
import java.io.*;

/**
 * This interface defines the methods required by a HTTPConnect
 * to connect to an HTTP server and retrieve content.  The
 * HTTPConnect calls the <TT>response</TT> method of the
 * HTTPClient to handle the actual content.  The HTTPClient
 * can feel free to ignore the content if it cannot handle
 * the appropriate MIME type.
 * @see amm.http.HTTPConnect
 * @author Aaron Mulder
 * @author ammulder@princeton.edu
 * @author http://www.princeton.edu/~ammulder/java.hmtl
 */
public interface HTTPClient {
  /**
   * The appropriate String for a request using the GET method.
   */
  public static final String GET="GET";
  /**
   * The appropriate String for a request using the HEAD method.
   */
  public static final String HEAD="HEAD";
  /**
   * The appropriate String for a request using the POST method.
   */
  public static final String POST="POST";

  /**
   * Returns the host name (and port, if required) for the
   * HTTPConnect to connect to (ie "www.nowhere.com:4039").
   * The default port is 80.
   * @return The host to connect to.
   */
  public String host();
  /**
   * Returns the path of the file to retrieve from the
   * HTTP server.  The path should be relative to the
   * root of the server, and start with a / (even an
   * empty path).
   * @return The path of the file to retrieve.
   */
  public String abs_path();
  /**
   * Returns the request method - typically GET, POST, or
   * HEAD.
   * @return The request method.
   */
  public String method();
  /**
   * The HTTPConnect passes the server's response code and
   * description using this method.  A code in the 200s
   * indicates a successful request, and a call to response()
   * should be forthcoming.  A code in the 300s to 500s
   * indicates some error preventing further data transfer.
   * In this case, response() will <EM>not</EM> be called.
   * @param code The status code of the server's response.
   * @param desc The status description of the server's
   *    response.
   * @see amm.http.HTTPClient#response
   */
  public void returnCode(int code,String desc);
  /**
   * The HTTPConnect passes the content type and length
   * and an Input Stream with the content itself using
   * this method.  The HTTPClient can decide how (and
   * whether) to handle the content.  The default length
   * or type are used if the HTTP server did not specify
   * one of the fields.
   * @param cType The content type (defaults to "unknown").
   * @param cLength The content length (defaults to -1).
   * @param in A stream holding the content (no header
   *        information).
   */
  public void response(String cType,long cLength,DataInputStream in);
  /**
   * The HTTPConnect uses this method to pass any status
   * information to the HTTPClient.
   * @param line The status string.
   */
  public void status(String line);
}
