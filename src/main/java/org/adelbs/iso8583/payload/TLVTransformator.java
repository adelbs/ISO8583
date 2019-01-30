package org.adelbs.iso8583.payload;

import org.adelbs.iso8583.constants.EncodingEnum;
import org.adelbs.iso8583.exception.OutOfBoundsException;
import org.adelbs.iso8583.util.Encoding;
import org.adelbs.iso8583.util.ISOUtils;
import org.adelbs.iso8583.vo.FieldVO;

class TLVTransformator implements Transformator {
	
	private static final int TLV_TYPE_SIZE = 2;
	private static final int TLV_LENGTH_SIZE = 3;
	
	private final Encoding encoding;
	
	TLVTransformator(final Encoding encoding) {
		super();
		this.encoding = encoding;
	}

	/**
	 * Transform a {@link FieldVO} into an array of bytes with following this format:
	 * [TLV TYPE][TLV LENGTH][TLV VALUE]
	 * 
	 * @return The converted payload 
	 */
	@Override
	public byte[] transform(final Object value) {
		if(!(value instanceof FieldVO)){
			throw new IllegalArgumentException("Cannot transform objects, other than FieldVO");
		}
		
		final FieldVO field = (FieldVO) value;
		final String tlvType = field.getTlvType() == null ? "" : field.getTlvType();
		final int tlvLength = (field.getTlvLength() == null || field.getTlvLength().length() == 0) ? 0 : Integer.parseInt(field.getTlvLength());

		byte[] payload = encoding.convert(getMaxSizeStr(tlvType, TLV_TYPE_SIZE));
		payload = ISOUtils.mergeArray(payload, encoding.convert(getMaxSizeStr(Integer.toString(tlvLength), TLV_LENGTH_SIZE)));
		payload = ISOUtils.mergeArray(payload, encoding.convert(getMaxSizeStr(field.getValue(), tlvLength)));
		
		return payload;
	}

	/**
	 * Revert an array o bytes, with the following format [TLV TYPE][TLV LENGTH][TLV VALUE][CONTINUATION OF THE MESSAGE]
	 * into a {@link FieldVO}. First byte of the array SHOULD be the first byte of the TLV Type
	 * attribute. Bytes, from other fields, after this TLV Field can be sent within the payload. They will
	 * not be considered during the revert.
	 * 
	 * 
	 * @return {@link RevertResult} object with both FieldVO and the last byte position we converted from the original payload
	 */
	@Override
	public RevertResult revert(final byte[] payload) throws OutOfBoundsException{
		final int encondedTypeSize = encoding.getEncondedByteLength(TLV_TYPE_SIZE);
		int endPosition = encondedTypeSize;
		String tlvType = new String(ISOUtils.subArray(payload, 0, endPosition));
		
		final int encodedLengthSize = encoding.getEncondedByteLength(TLV_LENGTH_SIZE);
		endPosition = endPosition + encodedLengthSize;
		String tlvLength = new String(ISOUtils.subArray(payload, encondedTypeSize, endPosition));

		endPosition = endPosition + Integer.parseInt(tlvLength);
		String value = new String(ISOUtils.subArray(payload, encondedTypeSize + encodedLengthSize, endPosition));
		
		final FieldVO fieldVO = new FieldVO();
		fieldVO.setEncoding((EncodingEnum)encoding);
		fieldVO.setValue(value);
		fieldVO.setTlvLength(removingFiller(tlvLength));
		fieldVO.setTlvType(removingFiller(tlvType));
		
		return  new RevertResult(endPosition,  fieldVO);		
/*		
 * 		TODO: Load also the Subfields of a TLV
 * 		int count = 1;
 * 		fieldList = new ArrayList<FieldVO>();
		while (endPosition < endTlvPosition) {
			tlvSubfield = getInstanceCopy();
			tlvSubfield.setFieldList(new ArrayList<FieldVO>());
			tlvSubfield.setBitNum(count++);
			endPosition = tlvSubfield.populateTLVFromPayload(Integer.parseInt(value.substring(0, 1)), Integer.parseInt(value.substring(1, 2)), payload, endPosition);
			fieldList.add(tlvSubfield);
		}*/
	}

	/**
	 * Removing leading zeros, added by the transformation
	 * @param value Raw String value
	 * @return cleanned String value
	 */
	private String removingFiller(String value) {
		return value.replaceAll("^0*", "");
	}
			
	/**
	 * Add leading Zeros the value, case the value's length is less than
	 * the number of bits of the field, or crop the value, adding 9's
	 * 
	 * @param value
	 * @param numBits
	 * @return
	 */
	private String getMaxSizeStr(String value, int numBits) {
		String maxSize = value;
		if (maxSize.length() > numBits) {
			maxSize = "";
			for (int i = 0; i < numBits; i++)
				maxSize += "9";
		}
		else {
			while (maxSize.length() < numBits)
				maxSize = "0" + maxSize;
		}
		return maxSize;
	}
}
