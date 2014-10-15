package org.park.tests;

import junit.framework.TestCase;

import org.park.connection.CommandMgr;

public class TestCmd extends TestCase {
	CommandMgr mcmd;

	protected void setUp() throws Exception {
		mcmd = new CommandMgr();
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCalculate() {
		assertEquals("adf611e7020e",
				mcmd.calculateDynamicPsw("BEEB0CB2DAF6ADE7110E02F200000029"));
	}

}
