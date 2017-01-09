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

  public static byte[] mergeArray(byte[] arr1, byte[] arr2) {
	  byte[] result = new byte[arr1.length + arr2.length];
	  
	  for (int i = 0; i < arr1.length; i++)
		  result[i] = arr1[i];
	  
	  for (int i = 0; i < arr2.length; i++)
		  result[i + arr1.length] = arr2[i];
	  
	  return result;
  }
}
