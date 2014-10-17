package org.park.tests;

import junit.framework.TestCase;

import org.park.command.LockCommand;

public class TestCmd extends TestCase {
	LockCommand mcmd;

	protected void setUp() throws Exception {
		mcmd = new LockCommand();
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCalculate() {
		assertEquals("adf611e7020e",
				mcmd.calculateDynamicPsw("BEEB0CB2DAF6ADE7110E02F200000029"));
	}

	public void testXor(){
		assertEquals("60", mcmd.xor("234506"));
		assertEquals("b7", mcmd.xor("10010203040506a1040506070809010100"));
	}
	public void testGetChangePairPswCmd() {
		assertEquals("feef10010203040506a1040506070809010100b7",
				mcmd.getChangePairPswCmd("123456", "456789", 1, 1));
	}
}
