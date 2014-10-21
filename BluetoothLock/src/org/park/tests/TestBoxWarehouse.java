package org.park.tests;

import junit.framework.TestCase;

import com.bluetooth.server.BoxWarehouse;

public class TestBoxWarehouse extends TestCase {
	BoxWarehouse mWarehouse;

	protected void setUp() throws Exception {
		mWarehouse = new BoxWarehouse();
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void test_getAvaiableBoxes() {
		mWarehouse.getAvaiableBoxes();
	}
}
