package pt.unl.fct.di.apdc.webapp.util;

import com.google.cloud.datastore.Entity;

public class ModifyAttributesData {
	private static final String SU = "SU";
	private static final String GBO = "GBO";
	private static final String GA = "GA";
	private static final String GS = "GS";
	private static final String USER = "USER";

	public String modifierUsername;
	
	public String modifiedUsername;
	public String email;
	public String fullName;
	public String profileVisibility;
	public String telephoneNumber;
	public String mobilePhoneNumber;
	public String occupation;
	public String workPlace;
	public String address;
	public String compAddress;
	public String zip;
	public String nif;
	public String state;
	public String role;
	
	public ModifyAttributesData() {}
	
	public ModifyAttributesData(String modifierUsername, String modifiedUsername, String email, String fullName, String profileVisibility,
														String telephoneNumber, String mobilePhoneNumber, String occupation, String workPlace, String address,
														String compAddress, String zip, String nif, String state, String role) {
		this.modifiedUsername = modifiedUsername;
		this.modifierUsername = modifierUsername;
		this.email= email ;	
		this.fullName= fullName ;
		this.profileVisibility= profileVisibility ;
		this.telephoneNumber= telephoneNumber;
		this.mobilePhoneNumber= mobilePhoneNumber ;
		this.occupation = occupation;
		this.workPlace= workPlace ;
		this.address = address;
		this.compAddress = address;
		this.zip = zip;
		this.nif= nif; ;
		this.state = state;
		this.role = role;
	}
	
	public void verifyOptionals(Entity modified) {
		if (email == null)
			this.email = modified.getString("email");
		
		if (fullName == null)
			this.fullName = modified.getString("fullName");
		
		if (profileVisibility == null) 
			this.profileVisibility =  modified.getString("visibility"); 
		
		if (telephoneNumber == null) 
			this.telephoneNumber =  modified.getString("telephone");
		
		if (mobilePhoneNumber == null) 
			this.mobilePhoneNumber =  modified.getString("mobilePhone"); 
		
		if (occupation == null) 
			this.occupation =  modified.getString("occupation"); 		
		
		if (workPlace == null) 
			this.workPlace =  modified.getString("workPlace"); 
		
		if (address == null) 
			this.address =  modified.getString("address"); 
		
		if (compAddress == null) 
			this.compAddress =  modified.getString("compAddress"); 
		
		if (zip== null) 
			this.zip =  modified.getString("zip"); 
		
		if (nif == null) 
			this.nif =  modified.getString("nif");

		if (role == null)
			this.role = modified.getString("role");
		
		if (state == null)
			this.state = modified.getString("state");
	}
	
	public boolean userCanChangeRole(Entity modifierUser, Entity modifiedUser) {
		if (this.role.equals(modifiedUser.getString("role")))
			return true;
		
		if (modifierUser.getString("role").equals(SU)) 
			return true;
		
		if (modifierUser.getString("role").equals(GS) && modifiedUser.getString("role").equals(USER)
				&& this.role.equals(GBO))
				return true;
		
		return false;
	}
	
	public boolean userCanChangeState(Entity modifierUser, Entity modifiedUser) {
		if (this.state.equals(modifiedUser.getString("state")))
			return true;
		
		if (modifierUser.getString("role").equals(SU)) 
			return true;
		
		if (modifierUser.getString("role").equals(GS) && (modifiedUser.getString("role").equals(GA) || modifiedUser.getString("role").equals(GBO)))
			return true;
		
		if (modifierUser.getString("role").equals(GA) && (modifiedUser.getString("role").equals(GBO) || modifiedUser.getString("role").equals(USER)))
			return true;
		
		if (modifierUser.getString("role").equals(GBO) && modifiedUser.getString("role").equals(USER))
			return true;
		
		if (modifierUser.getString("role").equals(USER) && this.modifierUsername.equals(this.modifiedUsername))
			return true;
		
		return false;
	}
}
