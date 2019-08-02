package org.adelbs.iso8583.constants;

import static org.junit.Assert.*;

import org.junit.Test;

public class EncodingEnumTest {

	@Test
	public void testGetEncodingByName() {
		assertEquals("Match by name shoudl return the exact enconding class instance", EncodingEnum.getEncoding("BINARY").getClass(), 
				EncodingEnum.BCD.getClass());	
	}
	
	public void testGetEncondingByUnexitentName(){
		assertNotEquals("Unexistent name shoudl return UTF8 enconding class instance", EncodingEnum.getEncoding("JOHNDOE").getClass(), 
				EncodingEnum.UTF8.getClass());
	}
	
	public void testGetEncondingByNullValue(){
		assertNotEquals("Null name shoudl return UTF8 enconding class instance", EncodingEnum.getEncoding(null).getClass(), 
				EncodingEnum.UTF8.getClass());
	}

	public void testGetEncondingByEmptyValue(){
		assertNotEquals("Null name shoudl return UTF8 enconding class instance", EncodingEnum.getEncoding("").getClass(), 
				EncodingEnum.UTF8.getClass());
	}
}
