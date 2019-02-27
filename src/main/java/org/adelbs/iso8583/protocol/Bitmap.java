package org.adelbs.iso8583.protocol;

import java.util.ArrayList;
import java.util.HashMap;

import org.adelbs.iso8583.constants.TypeEnum;
import org.adelbs.iso8583.constants.TypeLengthEnum;
import org.adelbs.iso8583.exception.FieldNotFoundException;
import org.adelbs.iso8583.exception.OutOfBoundsException;
import org.adelbs.iso8583.exception.ParseException;
import org.adelbs.iso8583.exception.PayloadIncompleteException;
import org.adelbs.iso8583.util.Encoding;
import org.adelbs.iso8583.util.ISOUtils;
import org.adelbs.iso8583.vo.FieldVO;
import org.adelbs.iso8583.vo.GenericIsoVO;
import org.adelbs.iso8583.vo.MessageVO;


public class Bitmap {

	private HashMap<Integer, FieldVO> bitmap = new HashMap<Integer, FieldVO>();
	private String binaryBitmap = "";
	private int totalBits = 128;
	
	private byte[] payloadBitmap;
	
	private MessageVO messageVO;
	private StringBuilder visualPayload = new StringBuilder();
	
	/**
	 * Builds the bitmap and the values of each bit from the payload.
	 * @param payload
	 * @param messageVO
	 * @throws ParseException
	 */
	public Bitmap(byte[] payload, MessageVO messageVO) throws ParseException {
		
		this.messageVO = messageVO.getInstanceCopy();
		
		String tempBitmap1 = "";
        String tempBitmap2 = "";
        int headerPlusType = messageVO.getHeaderSize() + 4;
		int bitmapSize = 0;
		
		//This try block will extract the bitmap from the payload
		try {
			visualPayload.append("Message Type: [").append(messageVO.getType()).append("]\n");
            
            if (messageVO.getHeaderSize() > 0)
                this.messageVO.setHeader(messageVO.getHeaderEncoding().convert(ISOUtils.subArray(payload, 0, messageVO.getHeaderSize())));

			bitmapSize = messageVO.getBitmatEncoding().getMinBitmapSize();
			tempBitmap1 = messageVO.getBitmatEncoding().convertBitmap(ISOUtils.subArray(payload, headerPlusType, headerPlusType + bitmapSize));
			
			if (tempBitmap1.substring(0, 1).equals("1")) {
				tempBitmap2 = messageVO.getBitmatEncoding().convertBitmap(ISOUtils.subArray(payload, headerPlusType + bitmapSize, headerPlusType + (bitmapSize * 2)));
				bitmapSize = bitmapSize * 2;
			}
			
			binaryBitmap = tempBitmap1 + tempBitmap2;
	
			payloadBitmap = messageVO.getBitmatEncoding().convert(binaryBitmap.substring(0, bitmapSize));
			visualPayload.append("Bitmap: [").append(new String(payloadBitmap)).append("]\n\n");
		}
		catch (OutOfBoundsException x) {
			throw new PayloadIncompleteException("Error trying to parse the Bitmap from payload. Payload incomplete.", 0);
		}
		catch (Exception x) {
			throw new ParseException("Error parsing the bitmap.\n" + x.getMessage() + "\n" + visualPayload);
		}

		//The next try block will extract the values of each bit field from the payload
		extractValueFromPayload(payload, messageVO, headerPlusType, bitmapSize);
	}

	/**
	 * Based on the Map of Bits, extract the Value from the Payload and recreate the original MessageVO/FieldVo structure
	 * with values
	 * 
	 * @param payload
	 * @param messageVO
	 * @param headerSize
	 * @param bitmapSize
	 * @throws PayloadIncompleteException
	 * @throws ParseException
	 */
	private void extractValueFromPayload(byte[] payload, MessageVO messageVO, int headerSize, int bitmapSize) throws PayloadIncompleteException, ParseException {
		int bitNum = 1;
		try{
			this.messageVO.setFieldList(new ArrayList<FieldVO>());
			int startPosition = headerSize + bitmapSize;
			for (; bitNum < binaryBitmap.length(); bitNum++){
				if (this.bitIsEnabled(bitNum)) {
					final FieldVO foundFieldVO = getFieldVOFromBitMap(messageVO, bitNum);
					startPosition = foundFieldVO.setValueFromPayload(payload, startPosition);
					
					if(!foundFieldVO.isIgnored()){
						bitmap.put(foundFieldVO.getBitNum(), foundFieldVO);
						bitmap.get(foundFieldVO.getBitNum()).setPresent(true);
						
						this.messageVO.getFieldList().add(foundFieldVO);
						visualPayload.append("Bit").append(bitNum).append(": [").append(foundFieldVO.getPayloadValue()).append("]\n");
					}
				}
			}
		}catch (OutOfBoundsException x) {
			throw new PayloadIncompleteException("Error trying to parse the fields from the payload. Payload incomplete.", bitNum + 1);
		}
		catch (Exception x) {
			throw new ParseException("Error parsing the message body.\n" + x.getMessage() + "\n" + visualPayload);
		}
	}

	/**
	 * Search for the field that represents the Bit that is enabled at the BitMap.
	 * @param messageVO
	 * @param bitNum
	 * @param foundFieldVO
	 * @return 
	 * @return
	 * @throws FieldNotFoundException case no fieldVO found
	 */
	private FieldVO getFieldVOFromBitMap(final GenericIsoVO genericVO, int bitNum) throws FieldNotFoundException {
		for (final FieldVO fieldVO : genericVO.getFieldList()) {
			if (fieldVO.getBitNum().intValue() == (bitNum + 1)) {
				return fieldVO.getInstanceCopy();
			}
		}
		
		throw new FieldNotFoundException("Field bit ("+ bitNum +") not found.");
	}

	private boolean bitIsEnabled(final int bitNum) {
		return binaryBitmap.substring(bitNum, bitNum + 1).equals("1");
	}
	
	public Bitmap(MessageVO sourceMessageVO) {
		this.messageVO = sourceMessageVO.getInstanceCopy();
		this.messageVO.setFieldList(new ArrayList<FieldVO>());
		visualPayload.append("Message Type: [").append(sourceMessageVO.getType()).append("]\n");
		
		buildBitMapFromParentVO(sourceMessageVO, this.messageVO);
		
		int lastBit = 0;
		for (int i = 1; i <= 128; i++) {
			binaryBitmap = binaryBitmap.concat(bitmap.get(i) == null ? "0" : "1");
			if (bitmap.get(i) != null) lastBit = i;
		}
		
		totalBits = lastBit;
		
		payloadBitmap = sourceMessageVO.getBitmatEncoding().convertBitmap(binaryBitmap.substring(0, 64));
		visualPayload.append("Bitmap: [").append(new String(payloadBitmap)).append("]\n\n");
		
		if (lastBit > 64) {
			FieldVO secondBitmap = new FieldVO(null, "Bitmap", "", 1, TypeEnum.ALPHANUMERIC, TypeLengthEnum.FIXED, 16, sourceMessageVO.getBitmatEncoding(), "true");
			binaryBitmap = "1".concat(binaryBitmap.substring(1));
			payloadBitmap = sourceMessageVO.getBitmatEncoding().convertBitmap(binaryBitmap.substring(0, 64));
			secondBitmap.setPayloadValue(sourceMessageVO.getBitmatEncoding().convertBitmap(binaryBitmap.substring(64, 128)));
			secondBitmap.setPresent(true);
			bitmap.put(1, secondBitmap);
			binaryBitmap.substring(0, 64);
		}
	}
	
	//TODO: @Felipe Why we should create instance copies? Is it necessary?
	/**
	 * Calculate the bits that are enabled, based on the UI checkboxes that enables and disables
	 * super fields. 
	 * 
	 * Only message's fields are able to be enabled or disabled, Their bitNum can't be repeated, and 
	 * will be the base to build the BitMap that will be sent to the Server. SubFields do not participate
	 * of the bit map calculation
	 * 
	 * @param originalParentVO
	 * @param parentVOCopy
	 */
	private void buildBitMapFromParentVO(final GenericIsoVO originalParentVO, final GenericIsoVO parentVOCopy){
		originalParentVO.getFieldList().forEach(originalFieldVO->{
			//TODO Maybe we could update isPresent for the Fields and its subfields, when the checkbox is clicked.
			if (isASuperFieldAndCheckBoxClicked(originalParentVO, originalFieldVO)) {
				//Setup field copy
				final FieldVO fieldVOCopy = originalFieldVO.getInstanceCopy();
				
				//insert into parent's field list
				parentVOCopy.getFieldList().add(fieldVOCopy);
				bitmap.put(originalFieldVO.getBitNum(), fieldVOCopy);
				
				visualPayload.append("Bit").append(originalFieldVO.getBitNum()).append(": [").append(originalFieldVO.getPayloadValue()).append("]\n");
			}
		});
	}
	
	private static boolean isASuperFieldAndCheckBoxClicked(final GenericIsoVO originalParentVO, FieldVO originalFieldVO) {
		return originalFieldVO.isPresent() && originalParentVO instanceof MessageVO;
	}

	public int getSize() {
		return totalBits;
	}
	
	public byte[] getPayloadBitmap() {
		return payloadBitmap;
	}
	
	public FieldVO getBit(Integer bit) {
		return bitmap.get(bit);
	}
	
	public String getVisualPayload() {
		return visualPayload.toString();
	}
	
	public MessageVO getMessageVO() {
		return messageVO;
	}

	public Encoding getBitmapEncoding() {
		return messageVO.getBitmatEncoding();
	}
}
