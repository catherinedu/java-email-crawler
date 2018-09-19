
package emailextractor;

/**
 *
 * @author CatherineDu
 */

import java.net.*; 
import java.io.*;
import javax.swing.*;

// Extract class - Extract all the emails from a webpage
public class EmailExtractor
{ // start class
    private static String EMAILS = ""; 
    private static String site = ""; 
    private static String HOSTNAME = ""; 
    private static String WEBPAGE = "http://www.mysiteurl.com/page.html"; 
    
    // downloads the source for the webpage, finds and displays all email addresses in the file
    public static void main(String[] args)
    { 
        String email = "blank"; 
        
        WEBPAGE = JOptionPane.showInputDialog("Enter the web page you want to extract email address from:");
        
        HOSTNAME = getHost(WEBPAGE);
        getHTML(WEBPAGE); 
        
        System.out.println("Extracting Email addresses..."); 
        while((email = getEmail(site)) != null) 
        {   //System.out.println(email); // print the email address to the screen
            if (EMAILS.indexOf(email) == -1) 
                EMAILS += email + "\n"; 
            site = site.substring(site.indexOf(email) + email.length());
        } 
        
        System.out.println(EMAILS); 
        System.out.println("All Emails Extracted."); 
    } 

    // this function returns the first email address found in a string
    private static String getEmail(String s)
    {   String email = ""; 
        int at = s.indexOf("@"); 
        int c = 0, d = 0; 
        boolean onePeriod = false; 

        if (at == -1) 
            return null;

        for (c = at - 1; c >= 0; c--)
        { 
            if (!IsChar(s.charAt(c)))
                break; 
        }
        
        // loop forward from the @ until we find the closest "non email letter"
        for (d = at + 1; d < s.length(); d++)
        { 
            // check to see if the character is a valid email character   
            if (!IsChar(s.charAt(d)))
            { 
                if (s.charAt(d) != '.')
                    break; 
                else if ((s.charAt(d) == '.') && (!onePeriod))
                    onePeriod = true;
                    
            } 
        }
        
  
        email = s.substring(c + 1, d);
        
        return email;
    } // end getEmail function
    
    // returns true if c is a valid character for an email address, false otherwise
    private static boolean IsChar(char c)
    { // start IsChar function
        /* 48 - 57  (numbers)
         * 65 - 90  (uppercase letters)
         * 97 - 122 (lowercase letters)
         * 95       (underscore) */
        
        int v = (int)c; // get the ASCII value for the character
        
        // return true if it's a valid email-address character, false otherwise
        return (((c >= 48) && (c <= 57)) || ((c >= 65) && (c <= 90)) || ((c >= 97) && (c <= 122)) || (c == 95)); 
    } // end IsChar function
    
    // this function downloads the source for the webpage in filename and stores it in global variable site
    private static void getHTML(String filename)
    { // start getHTML function
        site = ""; // clear site
        int x = filename.indexOf(HOSTNAME); 
        filename = filename.substring(x + HOSTNAME.length()); 
        
        int port = 80;
        Socket s = null; 
        InputStream sin = null; 
        BufferedReader fromServer = null; 
        OutputStream sout = null; 
        PrintWriter toServer = null; 
        
        try 
        {
            System.out.println ("Connecting to " + HOSTNAME + " on port " + port + "..."); 
            s = new Socket(HOSTNAME, port); 
        } 
        catch (Exception exception) {}
          
        try 
        { 
            sin = s.getInputStream(); 
            fromServer = new BufferedReader(new InputStreamReader(sin)); 
            sout = s.getOutputStream(); 
            toServer = new PrintWriter(new OutputStreamWriter(sout)); 
            
            toServer.print("GET " + filename + " HTTP/1.1\r\n"); 
            toServer.print("Host: " + HOSTNAME + "\r\n\r\n"); 
            toServer.flush(); 
            
            System.out.println("Connected. Getting Source Code from " + HOSTNAME + filename + "..."); 
            
            for (String l = null; (l = fromServer.readLine()) != null; ) 
            { 
                site += l; 
            } 
             
            System.out.println("Source obtained. Closing connection...");
            toServer.close(); 
            fromServer.close(); 
            s.close(); 
        } 
        catch (Exception ioException) 
        { 
            System.out.println ("Error"); 
        } 
    } // end getHTML function
    
    // returns the HOSTNAME from the string (should be a webpage)
    private static String getHost(String s)
    { 
        String host = ""; 
        String temp = s; 
        int w = temp.indexOf("www."); 
        int p = 0; 
        
        if (w == -1) 
        { 
            w = temp.indexOf("http://"); 
            
            if (w == -1) 
            { 
                w = -4; 
            } 
            else
            { 
                w += 3; 
            } 
        } 
        else 
        { 
            host = "www."; 
        } 

        temp = temp.substring(w + 4); 
        p = temp.indexOf('/'); 
        
        // check to see if there was a forward slash
        if (p == -1) 
        { 
            continue;
        } 
        else 
        { 
            temp = temp.substring(0, p); 
        } 
        
        return host + temp; // return the www. plus the rest of the host
    } // end getHost function
} // end Extract class 
