package org.adelbs.iso8583.vo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FieldVOTest {
	
	private FieldVO fieldVO; 

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		this.fieldVO = new FieldVO();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIsIgnored_BasicTrueFalse() {
		fieldVO.setDynaCondition("false");
		assertFalse("String false should evaluate as  boolean false", fieldVO.isIgnored());
		
		fieldVO.setDynaCondition("true");
		assertTrue("String true should evaluate as  boolean true", fieldVO.isIgnored());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testisIgnored_NonBooleanExpression(){
		fieldVO.setDynaCondition("return \"foobar\";");
		fieldVO.isIgnored();
	}
}
