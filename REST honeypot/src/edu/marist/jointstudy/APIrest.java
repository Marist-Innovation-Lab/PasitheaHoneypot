package edu.marist.jointstudy;

import java.io.*;
import java.util.*;

/* Consider adding a logger in this manner.
import java.util.logging.Level;
import java.util.logging.Logger;
 */

/* NanoHTTPD
   GitHub: https://github.com/NanoHttpd/nanohttpd
   Documentation : https://jar-download.com/java-documentation-javadoc.php?a=nanohttpd&g=org.nanohttpd&v=2.2.0
 */
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.util.ServerRunner;

/* GSON
   GitHub: https://github.com/google/gson
   Documentation: http://www.javadoc.io/doc/com.google.code.gson/gson/2.8.1
 */
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/* --------------
   --- Public ---
   --------------  */

/**
 * The SecureCloud REST API Honeypot.
 *
 * @author Alan and the Marist NSF-stars
 */
public class APIrest extends NanoHTTPD {

   public static final String apiVersion = "0.01";
   public static final String apiName    = "SecureCloud REST API Honeypot version " + apiVersion;
   public static final int    apiPort    = 8080;

   public APIrest() {
      super(apiPort);  // call the NanoHTTPD constructor.
      try {
         // There used to be initialization stuff here.
         // Maybe we'll have some in the future.
         // Maybe not.
      } catch (Exception ex) {
         System.out.println("Error in APIrest constructor: " + ex.getCause());
      }
   }

   public static void main(String[] args) throws Exception {
      System.out.println("Welcome to the " + apiName +".");
      Runtime rt = Runtime.getRuntime();
      System.out.print(" JVM says Processors:" + rt.availableProcessors());
      System.out.print("  Total memory:" + java.text.NumberFormat.getNumberInstance(java.util.Locale.US).format(rt.totalMemory()));
      System.out.print("  Free memory:" + java.text.NumberFormat.getNumberInstance(java.util.Locale.US).format(rt.freeMemory()));
      System.out.println("  Used memory:" + java.text.NumberFormat.getNumberInstance(java.util.Locale.US).format(rt.totalMemory() - rt.freeMemory()));
      // Launch the server.
      ServerRunner.run(APIrest.class);
   }

   @Override
   public Response serve(IHTTPSession session) {
      // Grab some session/header data.
      Map<String, String> headers = session.getHeaders();
      String fromIP    = headers.get("remote-addr");
      String userAgent = headers.get("user-agent");

      // Note what method (OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE) got us here.
      // (See https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html for details.)
      // We'll use this to short-circuit the rather lengthy evaluation coming up.
      boolean methodIsOPTIONS = false;
      boolean methodIsGET     = false;
      boolean methodIsHEAD    = false;
      boolean methodIsPOST    = false;
      boolean methodIsPUT     = false;
      boolean methodIsDELETE  = false;
      boolean methodIsTRACE   = false;

      // Store the body of POSTs and PUTs as both string and JSON data for possible future use.
      String reqBodyStr = "";
      JsonObject reqBodyJSON = new JsonObject();

      Method method = session.getMethod();
      if (Method.OPTIONS.equals(method)) {
         methodIsOPTIONS = true;
      } else if (Method.GET.equals(method)) {
         methodIsGET = true;
      } else if (Method.HEAD.equals(method)) {
         methodIsHEAD = true;
      } else if (Method.POST.equals(method)) {
         methodIsPOST = true;
         // Read JSON string from POST body and parse it
         Integer contentLength = Integer.parseInt(headers.get("content-length"));
         byte[] buf = new byte[contentLength];
         try {
             session.getInputStream().read(buf, 0, contentLength);
             reqBodyStr = new String(buf);
             reqBodyJSON = new JsonParser().parse(reqBodyStr).getAsJsonObject();
         } catch (Exception e) {
             // TODO: Do something if this fails.
         }
      } else if (Method.PUT.equals(method)) {
         methodIsPUT = true;
         // Read JSON string from PUT body and parse it
         Integer contentLength = Integer.parseInt(headers.get("content-length"));
         byte[] buf = new byte[contentLength];
         try {
             session.getInputStream().read(buf, 0, contentLength);
             reqBodyStr = new String(buf);
             reqBodyJSON = new JsonParser().parse(reqBodyStr).getAsJsonObject();
         } catch (Exception e) {
            // TODO: Do something if this fails.
         }
      } else if (Method.DELETE.equals(method)) {
         methodIsDELETE = true;
      } else if (Method.TRACE.equals(method)) {
         methodIsTRACE = true;
      }

      // Note what was requested.
      String requestText = String.valueOf(session.getUri());

      // Echo it to the API console.
      System.out.println("API received command: " + requestText + " from " + fromIP);

      // Make a log entry.
      String msg = method.toString() + " from " + fromIP + " ~ " + requestText  + " ~ " + userAgent + " ~ ";
      writeLog(msg);

      // Return the response.
      String responseTxt = "?";  // TODO: What should go here?
      Response response = newFixedLengthResponse(responseTxt);
      return response;
   }
   

   /* -----------------
      --- Protected ---
      -----------------  */


   /* ---------------
      --- Private ---
      ---------------  */

   /**
    * Write to the error log database tables and text stream.
    * @param logMsg the message to log
    */
   private void writeLog(String logMsg) {
      /* Try and write to the database.
      String insertCommand = "INSERT INTO errorlogs(error_string, time, source, severity) " +
                             "VALUES ('" + logMsg + "', current_timestamp, 'back-end', 0)";
      try {
         dbCommand(insertCommand);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
      */

      // Try and write to the text stream.
      try(PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter("logs.txt", true)))) {
         // TODO: Refactor that above line. It's hideous!
         output.println(new java.util.Date() + " ~ " + apiName + " ~ " + logMsg);
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

}
