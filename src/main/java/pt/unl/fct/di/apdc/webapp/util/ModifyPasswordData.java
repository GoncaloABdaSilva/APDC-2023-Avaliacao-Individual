package pt.unl.fct.di.apdc.webapp.util;

import org.apache.commons.codec.digest.DigestUtils;

public class ModifyPasswordData {

	public String username;
	public String previousPassword;
	public String newPassword;
	public String confirmationPassword;
	
	public ModifyPasswordData() { }
	
	public ModifyPasswordData(String username, String previousPassword, String newPassword, String confirmationPassword) {
		this.username = username;
		this.previousPassword = previousPassword;
		this.newPassword = newPassword;
		this.confirmationPassword = confirmationPassword;
	}
	
	public boolean isOldPasswordValid(String currentHashedPassword) {
		return currentHashedPassword.equals(DigestUtils.sha512Hex(this.previousPassword));
	}
	
	public boolean isNewPasswordValid() {
		return newPassword.equals(confirmationPassword);
	}
	
	public boolean newPwdEqualsOldPwd() {
		return this.previousPassword.equals(this.newPassword);
	}
	
}
