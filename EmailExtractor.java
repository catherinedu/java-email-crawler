/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailextractor;

/**
 *
 * @author CatherineDu
 */

import java.net.*; // allows us to make a connection to the server
import java.io.*; // allows us to do IO with the server
import javax.swing.*; // allows us to get input from the user (if you want)

// Extract class - Extract all the emails from a webpage
public class EmailExtractor
{ // start class
    private static String EMAILS = ""; // holds all the email addresses
    private static String site = ""; // will hold the HTML for the web page
    private static String HOSTNAME = ""; // the HOSTNAME for the webpage we download
    private static String WEBPAGE = "http://www.mysiteurl.com/page.html"; // SET THE PAGE YOU WANT TO GET HERE
    
    // downloads the source for the webpage, finds and displays all email addresses in the file
    public static void main(String[] args)
    { // start main function
        String email = "blank"; // a temp string that will hold the email address
        
        // the following line allows the user to input what page they want to extract from...
        WEBPAGE = JOptionPane.showInputDialog("Enter the web page you want to extract email address from:");
        
        HOSTNAME = getHost(WEBPAGE); // get the correct hostname for the page
        getHTML(WEBPAGE); // download's the source to webpage, stores it in site (global variable)
        
        System.out.println("Extracting Email addresses..."); // show the user where we are at currently
        
        while((email = getEmail(site)) != null) // loop so long as we still find an email address
        { // start while loop
            //System.out.println(email); // print the email address to the screen
            if (EMAILS.indexOf(email) == -1) // make sure we haven't found this email address yet
                EMAILS += email + "\n"; // add the email to the list
            site = site.substring(site.indexOf(email) + email.length()); // remove the email address from the HTML
        } // end while loop
        
        System.out.println(EMAILS); // show the user the emails we found
        System.out.println("All Emails Extracted."); // tell the user we are done
    } // end main function

    // this function returns the first email address found in a string
    private static String getEmail(String s)
    { // start getEmail function
        String email = ""; // will hold the email address later
        int at = s.indexOf("@"); // find the index of the first @
        int c = 0, d = 0; // counters
        boolean onePeriod = false; // will make sure we only find one period in the emial address

        if (at == -1) // check to see if there was even an email address found
            // there aren't any email address
            return null;

        // loop backwards from the @ until we find the closest "non email letter"
        for (c = at - 1; c >= 0; c--)
        { // start for loop
            // check too see if the character is a valid email character
            if (!IsChar(s.charAt(c)))
                break; // get out
        } // end for loop
        
        // loop forward from the @ until we find the closest "non email letter"
        for (d = at + 1; d < s.length(); d++)
        { // start for loop
            // check to see if the character is a valid email character   
            if (!IsChar(s.charAt(d)))
            { // start if statement
                // check to see if it is a period
                if (s.charAt(d) != '.')
                    break; // not a period, end of the email get out
                else if ((s.charAt(d) == '.') && (!onePeriod))
                    onePeriod = true; // just skip the period, look for the next non email character
                    
            } // end if statement
        } // end for loop
        
        // c is now equal to the index before the first letter of the email address
        // d is now equal to the index after the last letter of the email address
        // put the email address together
        email = s.substring(c + 1, d);
        
        return email; // return the email address
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
        int x = filename.indexOf(HOSTNAME); // find the first occurrence of the hostname in the webpage
        filename = filename.substring(x + HOSTNAME.length()); // get the filename w/o hostname
        
        int port = 80; // HTTTP port
        Socket s = null; // used to d/l the source
        InputStream sin = null; // InputStream for the socket
        BufferedReader fromServer = null; // BufferedReader for the socket
        OutputStream sout = null; // OutputStream for the socket
        PrintWriter toServer = null; // let's us send info to the server
        
        try // watch for an exception
        { // start try
            System.out.println ("Connecting to " + HOSTNAME + " on port " + port + "..."); // show the user some output
            s = new Socket(HOSTNAME, port); // connect to the HTTP server
        } // end try
        catch (Exception exception) {} // don't display an error message
          
        try // watch for an exception
        { // start try
            sin = s.getInputStream(); // set up the inputstream
            fromServer = new BufferedReader(new InputStreamReader(sin)); // allow us to get info from the server
            sout = s.getOutputStream(); // set up the output stream
            toServer = new PrintWriter(new OutputStreamWriter(sout)); // allow us to send info to the server
            
            toServer.print("GET " + filename + " HTTP/1.1\r\n"); // send the request for the file to the server
            toServer.print("Host: " + HOSTNAME + "\r\n\r\n"); // set the host parameter
            toServer.flush(); // send the info to the server
            
            System.out.println("Connected. Getting Source Code from " + HOSTNAME + filename + "..."); // show the user more output
            
            for (String l = null; (l = fromServer.readLine()) != null; ) // loop while there is still HTML
            { // start for loop
                site += l; // add the line to site
            } // end for loop
             
            System.out.println("Source obtained. Closing connection..."); // tell the user what we are doing
            toServer.close(); // close ouput stream
            fromServer.close(); // close input stream
            s.close(); // close socket
        } // end try
        catch (Exception ioException) 
        { // start catch
            System.out.println ("Error"); // tell the user we encountered an error
        } // end catch
    } // end getHTML function
    
    // returns the HOSTNAME from the string (should be a webpage)
    private static String getHost(String s)
    { // start getHost function
        // This function works off the assumption that s will be in the format of the following:
        // http://www.mysiteurl.com/page.html - it should be looking for a webpage
        // but I made it check for the following URL formats:
            // http://www.mysiteurl.com/page.html (with or without specific page afterwards)
            // http://www.mysiteurl.com (with or without specific page afterwards)
            // http://mysiteurl.com (with or without specific page afterwards)
            // mysiteurl.com (with or specific page afterwards)
            
        String host = ""; // ends up with the hostname
        String temp = s; // used to do manipulations to, so we don't mess with s
        int w = temp.indexOf("www."); // find the first occurrence of www.
        int p = 0; // will hold the index of the forward slash '/' later
        
        if (w == -1) // check to make sure it had a www. in it
        { // start if statement
            // it did not have a www. in it..find the http://
            w = temp.indexOf("http://"); // find the index of http://
            
            if (w == -1) // make sure there was a http://
            { // start if statement
                // there was no http://
                // set w to -4..so that the substring works below
                w = -4; // -4, b/c it adds 4 below (it will start at 0)
            } // end if statement
            else // there was a http://
            { // start else statement
                // set w to (w + 3) so that it is on the p (4 characters in the string)
                w += 3; // so that the substring works below (it will start after the http://)
            } // start else statement
        } // end if statement
        else // else, it had a www. in it
        { // start else statement
            // start host off as www.
            host = "www."; // so that the www. is in the final hostname (not that it really matters)
        } // end else statement

        temp = temp.substring(w + 4); // get rid of the www. (and anything before it [http://])
        p = temp.indexOf('/'); // find the next slash in the URL
        
        // check to see if there was a forward slash
        if (p == -1) // check
        { // start if statement
            // there was no forward slash...just leave temp as is...should be the right hostname
            // don't do anything here
        } // end if statement
        else // there was a forward slash
        { // start else statement
            temp = temp.substring(0, p); // get the hostname (without the '/')
        } // end else statement
        
        return host + temp; // return the www. plus the rest of the host
    } // end getHost function
} // end Extract class 
