package org.adelbs.iso8583.payload;

import java.util.HashMap;
import java.util.Map;

import org.adelbs.iso8583.constants.EncodingEnum;
import org.adelbs.iso8583.constants.TypeEnum;
import org.adelbs.iso8583.exception.OutOfBoundsException;
import org.adelbs.iso8583.util.Encoding;

/**
 * Factory class to handle instances of {@link Transformator}
 */
public class PayloadTransformator {
	
	private final Encoding encoding;
	
	private static Map<String, PayloadTransformator> INSTANCES = new HashMap<String, PayloadTransformator>();
	private static Map<TypeEnum, Transformator> TRANSFORMATORS = new HashMap<TypeEnum, Transformator>();
	
	/**
	 * Get a new instance of PayloadTransformator. Utilizes Lazy initialization and its not thread safe
	 * @param encoding Which type of enconde this transformator will be specialized
	 * @return a new instantiated PayloadTransformator
	 */
	public static PayloadTransformator getInstance(final EncodingEnum encoding){
		PayloadTransformator payloadTransformator = INSTANCES.get(encoding.toString());
		if(payloadTransformator == null){
			payloadTransformator = new PayloadTransformator(encoding);
			INSTANCES.put(encoding.toString(), payloadTransformator);
		}
		return payloadTransformator;
	}

	private PayloadTransformator(final Encoding encoding) {
		super();
		this.encoding = encoding;
	}
	
	public byte[] transform(final Object value, final TypeEnum type){
		Transformator transformator = TRANSFORMATORS.get(type);
		if(transformator == null){
			switch (type) {
				case TLV:
					transformator = new TLVTransformator(encoding);
					break;
				default:
					throw new UnsupportedOperationException("There is no Transformation available for this TypeEnum");
			}
			TRANSFORMATORS.put(type, transformator);
		}
	
		
		return transformator.transform(value);
	}
	
	public RevertResult revert(final byte[] payload, final TypeEnum type) throws OutOfBoundsException{
		RevertResult revertedValue;
		switch (type) {
			case TLV:
				revertedValue = (new TLVTransformator(encoding).revert(payload));
				break;
			default:
				throw new UnsupportedOperationException("There is no Transformation available for this TypeEnum");
		}
		return revertedValue;
	}
}
