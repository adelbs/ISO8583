package org.adelbs.iso8583.constants;

import javax.swing.JComboBox;

import org.adelbs.iso8583.util.Encoding;
import org.adelbs.iso8583.util.EncodingBASE64;
import org.adelbs.iso8583.util.EncodingBINARY;
import org.adelbs.iso8583.util.EncodingEBCDIC;
import org.adelbs.iso8583.util.EncodingHEXA;
import org.adelbs.iso8583.util.EncodingUTF8;

/**
 * This class represents the available encoding options,
 * to convert ISO messages sent/received by this library
 */
public enum EncodingEnum implements Encoding {

	BINARY("BINARY", new EncodingBINARY()), 
	EBCDIC("EBCDIC", new EncodingEBCDIC()), 
	ISO88591("ISO 8859-1", new EncodingUTF8()), 
	UTF8("UTF-8", new EncodingUTF8()), 
	HEXA("HEXA", new EncodingHEXA()),
	BASE64("BASE64", new EncodingBASE64());
	
	private String value;
	private Encoding encodingImpl;
	
	EncodingEnum (String value, Encoding encodingImpl) {	
		this.value = value;
		this.encodingImpl = encodingImpl;
	}
	
	public String toString() {
		return value;
	}

	public String toPlainString() {
		return value.replaceAll(" ", "").replaceAll("-", "");
	}
	
	/**
	 * Returns an instance of Encoding, based on its name. The name must be a exact match
	 * of one of the Enumerators elements.
	 * 
	 * @param value String name of the encoder.
	 * @return an instance of {@link Encoding}, depending on the type provided as,
	 * parameter. The default encoding is UTF8.
	 */
	public static EncodingEnum getEncoding(String value) {
		EncodingEnum encoder = EncodingEnum.UTF8;
		try{
			encoder = EncodingEnum.valueOf(value);
		}catch (IllegalArgumentException e){
			encoder =  EncodingEnum.UTF8;
		}
		if(value == null || value.isEmpty()){
			return EncodingEnum.UTF8;
		}
		return encoder;
	}
	
	/**
	 * Populates a {@link JComboBox} with all elements of this Enumerator.
	 * @param combo previously created instance of a comboBox to be populated
	 */
	//TODO: This method should be on a separated UI class. UI must be separated from business classes
	public static void addCmbItemList(JComboBox<EncodingEnum> combo) {
		combo.addItem(BINARY);
		combo.addItem(EBCDIC);
		combo.addItem(ISO88591);
		combo.addItem(UTF8);
		combo.addItem(HEXA);
		combo.addItem(BASE64);
	}

	@Override
	public String convert(byte[] bytesToConvert) {
		return encodingImpl.convert(bytesToConvert);
	}

	@Override
	public byte[] convert(String strToConvert) {
		return encodingImpl.convert(strToConvert);
	}

	@Override
	public String convertBitmap(byte[] binaryBitmap) {
		return encodingImpl.convertBitmap(binaryBitmap);
	}

	@Override
	public byte[] convertBitmap(String binaryBitmap) {
		return encodingImpl.convertBitmap(binaryBitmap);
	}

	@Override
	public int getMinBitmapSize() {
		return encodingImpl.getMinBitmapSize();
	}
	
	@Override
	public int getEncondedByteLength(final int asciiLength) {
		return encodingImpl.getEncondedByteLength(asciiLength);
	}
	
}