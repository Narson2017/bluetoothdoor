package org.park.tests;

import junit.framework.TestCase;

import com.bluetooth.server.ServerConn;
import com.bluetooth.server.ServerHandle;

public class TestServerConn extends TestCase {
	ServerConn mServ;
	private ServerHandle mHandle = new ServerHandle() {

		@Override
		public void sended(boolean done) {
			// TODO Auto-generated method stub

		}

		@Override
		public void received(String data) {
			// TODO Auto-generated method stub

		}

	};
	protected void setUp() throws Exception {
		super.setUp();
		mServ = new ServerConn(mHandle);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
