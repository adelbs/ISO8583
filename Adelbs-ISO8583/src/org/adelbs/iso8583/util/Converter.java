package org.adelbs.iso8583.util;

import java.util.ArrayList;

public class Converter {

	public static byte[] hexToBytes(String hex) {
		 
        String strBin;
        String strDec;

        ArrayList<Byte> resultArray = new ArrayList<Byte>();
        
        for (int i = 0; i < hex.length(); i += 2) {
              
           strBin = hexToBin(hex.substring(i, i + 2));
           strDec = String.valueOf(Integer.parseInt(strBin, 2));
           resultArray.add(new Byte((byte) Integer.parseInt(strDec)));
              
        }
        
        byte result[] = new byte[resultArray.size()];
        for (int i = 0; i < result.length; i++) result[i] = resultArray.get(i);

        return result;
  }

	public static String bytesToBin(byte[] bytes) {
		
		String result = "";
		String byteBin;
		
		for (byte bt : bytes) {
			byteBin = Integer.toBinaryString(new Byte(bt).intValue());
			while (byteBin.length() < 8)
				byteBin = "0"+ byteBin;
			
			result = result + byteBin;
		}
		
		return result;
	}
	
	final private static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
		
		char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);  
	}
	
  public static String hexToBin(String hex){
      String bin = "";
      String binFragment = "";
      int iHex;
      hex = hex.trim();
      hex = hex.replaceFirst("0x", "");

      for(int i = 0; i < hex.length(); i++){
          iHex = Integer.parseInt(""+hex.charAt(i),16);
          binFragment = Integer.toBinaryString(iHex);

          while(binFragment.length() < 4){
              binFragment = "0" + binFragment;
          }
          bin += binFragment;
      }
      return bin;
  }

}
