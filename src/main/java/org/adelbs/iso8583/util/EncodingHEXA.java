package org.adelbs.iso8583.util;

public class EncodingHEXA implements Encoding {

    @Override
    public String convert(byte[] bytesToConvert) {
        try {
            return new String(bytesToConvert);
        } 
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] convert(String strToConvert) {
        try {
            String hexStr = String.format("%1x", new java.math.BigInteger(1, strToConvert.getBytes()));

            int len = hexStr.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(hexStr.charAt(i), 16) << 4)
                             + Character.digit(hexStr.charAt(i+1), 16));
            }
            return data;
        } 
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String convertBitmap(byte[] binaryBitmap) {
        String tempBM = new String(binaryBitmap);
        tempBM = ISOUtils.hexToBin(tempBM);
        return tempBM;
    }

    @Override
    public byte[] convertBitmap(String binaryBitmap) {
        return ISOUtils.binToHex(binaryBitmap).getBytes();
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
