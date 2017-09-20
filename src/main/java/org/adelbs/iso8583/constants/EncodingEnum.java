package org.adelbs.iso8583.constants;

import javax.swing.JComboBox;

import org.adelbs.iso8583.util.Encoding;
import org.adelbs.iso8583.util.EncodingBINARY;
import org.adelbs.iso8583.util.EncodingEBCDIC;
import org.adelbs.iso8583.util.EncodingHEXA;
import org.adelbs.iso8583.util.EncodingUTF8;

public enum EncodingEnum implements Encoding {

	BINARY("BINARY", new EncodingBINARY()), 
	EBCDIC("EBCDIC", new EncodingEBCDIC()), 
	ISO88591("ISO 8859-1", new EncodingUTF8()), 
	UTF8("UTF-8", new EncodingUTF8()), 
	HEXA("HEXA", new EncodingHEXA());
	
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
	
	public static EncodingEnum getEncoding(String value) {
		if ("BINARY".equals(value))
			return EncodingEnum.BINARY;
		else if ("EBCDIC".equals(value))
			return EncodingEnum.EBCDIC;
		else if ("ISO88591".equals(value))
			return EncodingEnum.ISO88591;
		else if ("UTF8".equals(value))
			return EncodingEnum.UTF8;
		else if ("HEXA".equals(value))
			return EncodingEnum.HEXA;

		return EncodingEnum.UTF8;
	}
	
	public static void addCmbItemList(JComboBox<EncodingEnum> combo) {
		combo.addItem(BINARY);
		combo.addItem(EBCDIC);
		combo.addItem(ISO88591);
		combo.addItem(UTF8);
		combo.addItem(HEXA);
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

}
