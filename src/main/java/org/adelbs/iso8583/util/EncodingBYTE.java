package org.adelbs.iso8583.util;

public class EncodingBYTE implements Encoding {

    @Override
    public String convert(byte[] bytesToConvert) {
        try {
            StringBuilder strResult = new StringBuilder();
    		
    		for(int i=0;i<bytesToConvert.length;i++) {
    			/*
    			int y = ((int) bytesToConvert[i]);
    			String x=Integer.toString(y);
    			*/
    			String x="";
    			try {
    	    		x = Integer.toHexString((int) bytesToConvert[i]);
    	    		x=x.replace("f", "");
    			
    				if(Integer.parseInt(x) < 10)
	    				x="0".concat(x);
    			}
    			catch(Exception ex) {
    				//System.out.println("non numeric tpdu char "+x);
    			}
    			strResult.append(x);
    		}
    		return strResult.toString();
            
        } 
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] convert(String strToConvert) {
        try {
            System.out.println("converting ["+strToConvert+"] to byte[]");
        	int l=strToConvert.length();
            byte[] resultBytes = new byte[l];
    		for(int i=0;i<l;i++) {
    			System.out.println(strToConvert.substring(i,(i+1)));
    			resultBytes[i] = (byte) Integer.parseInt(strToConvert.substring(i,(i+1)));
    			
    		}

            return resultBytes;
            
        } 
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String convertBitmap(byte[] binaryBitmap) {
        /*
         * TODO: implementar para byte
         * */
    	System.out.println("convertBitmap(byte[] binaryBitmap) needs to be implemented");
        return "";
    }

    @Override
    public byte[] convertBitmap(String binaryBitmap) {
    	/*
         * TODO: implementar para byte
         * */
    	System.out.println("convertBitmap(String binaryBitmap) needs to be implemented");
        return null;
    	//return ISOUtils.binToHex(binaryBitmap).getBytes();
    }

    @Override
    public int getMinBitmapSize() {
        return 16;
    }

    @Override
    public int getEncondedByteLength(final int asciiLength) {
        return asciiLength;
    }
}
