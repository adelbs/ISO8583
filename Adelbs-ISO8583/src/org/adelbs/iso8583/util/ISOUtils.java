package org.adelbs.iso8583.util;

public class ISOUtils {

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

  public static byte[] subArray(byte[] data, int start, int end) {
	  byte[] result = new byte[end - start];
	  
	  for (int i = start; i < end; i++)
		  result[i - start] = data[i];

	  return result;
  }
  
}
