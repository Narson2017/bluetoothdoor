package org.park.authorize;

public interface MsgManager {
	public void authPassed(int i);

	public void hint(int res_id);

	public void hint(String str);

	public void unHint();

	public void setRegisterBtn(int res_id);

	public void stopLoading();
}
