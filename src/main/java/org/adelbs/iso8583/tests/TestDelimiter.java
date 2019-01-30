package org.adelbs.iso8583.tests;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
/*
 *Delimiter test got from  
 *https://communities.ca.com/thread/241768793
 *it uses FIX messages and counts with String to delimit the message
 **/


	public class TestDelimiter {
    
 
    /* 
     * FIX Messages will end with TAG 10, a FIX message checksum
     * followed by the SOH (Start of Header) character - ASCII 0x01 
     */
    private Pattern pattern = Pattern.compile("10=\\d{3}");
    
    private int startOfNextRequest = 0;
    private int endOfRequest = -1;
    private String message;
 
    public boolean locateRequest(List<Byte> bytes) {
        System.out.println("Entering FIXDelimiter.locateRequest");
        
        byte[] ba = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            ba[i] = bytes.get(i);
        }
        String request = new String(ba);
        
        System.out.println("request=" + request);
        
        Matcher m = pattern.matcher(request);
        if (m.find()) {
                System.out.println("FIX match found.  Matched: " + request.substring(m.start(), m.end()));
            //TODO: Pattern.compile("10=\\d{3}\\x01") matches but doesn't get 
            //      the last character 0x01.  Using Pattern.compile("10=\\d{3}")
            //      and +1 for now.
            endOfRequest = m.end() + 1;            
            startOfNextRequest = 0;
            return true;
        }
        endOfRequest = -1;
        startOfNextRequest = -1;
        return false;
    }
 
    public int getEndOfRequest() {
        return endOfRequest;
    }
 
    public int getStartOfNextRequest() {
        return startOfNextRequest;
    }
 
    public String validate() {
        return message;
    }
 
    public void configure(String config) {
        // Empty
    }
     public static void main(String[] args) {  
        Pattern pattern = Pattern.compile("49=CDRG");
        String request = "8=FIX.4.2 9=67 35=A 49=CDRG_TST 56=MSSB_EQ 34=53 52=20110811-16:50:28 108=30 98=0 10=018 ";    
        Matcher m = pattern.matcher(request);
        if (m.find()) {
            System.out.println("found");
            System.out.println("start:"+ m.start() + ", end: " + m.end());
            System.out.println(request.substring(m.start(), m.end()));
        } else {
            System.out.println("not found");
        }
     }
}

