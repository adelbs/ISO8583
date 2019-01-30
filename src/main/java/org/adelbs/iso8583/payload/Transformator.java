package org.adelbs.iso8583.payload;

import org.adelbs.iso8583.exception.OutOfBoundsException;

public interface Transformator {
	
	/**
	 * Transform a Object into an array of bytes
	 * @param value
	 * @return
	 */
	public byte[] transform(final Object value);
	
	
	/**
	 * Revert the array of bytes into an Object
	 * @param payload payload array of bytes
	 * @return
	 * @throws OutOfBoundsException 
	 */
	public RevertResult revert(final byte[] payload) throws OutOfBoundsException;
}
