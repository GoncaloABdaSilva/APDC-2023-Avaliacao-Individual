package pt.unl.fct.di.apdc.webapp.util;

public class RegisterData {

	private static final String UNDEFINED = "UNDEFINED";
	
	public String username;
	public String email;
	public String password;
	public String confirmation;	
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
	
	
	public RegisterData() {	}
	
	public RegisterData(String username, String password, String confirmation, String email, String fullName, String profileVisibility, String telephoneNumber,
										String mobilePhoneNumber, String occupation, String workPlace, String address, String compAddress, String zip, String nif) {	
		this.username = username;
		this.password = password;
		this.email = email;
		this.fullName = fullName;
		this.confirmation = confirmation;
		this.profileVisibility = profileVisibility;
		this.telephoneNumber = telephoneNumber;
		this.mobilePhoneNumber = mobilePhoneNumber;
		this.occupation = occupation;	
		this.workPlace = workPlace;
		this.address = address;
		this.compAddress = compAddress;
		this.zip = zip;
		this.nif = nif;
	}
	
	public boolean validRegistration() {
		return !(this.username == null || this.username.equals("") ||
		    	this.password == null || this.password.equals("") ||
	    		this.confirmation == null || this.confirmation.equals("") ||
	    		this.email == null || this.email.equals("") ||
				this.fullName == null || this.fullName.equals("") ||
				!this.password.equals(this.confirmation) );
	}
	
	public void verifyOptionals() {
		if (profileVisibility == null) 
			this.profileVisibility = UNDEFINED ; 
		
		if (telephoneNumber == null) 
			this.telephoneNumber = UNDEFINED ;
		
		if (mobilePhoneNumber == null) 
			this.mobilePhoneNumber = UNDEFINED ; 
		
		if (occupation == null) 
			this.occupation = UNDEFINED ; 		
		
		if (workPlace == null) 
			this.workPlace = UNDEFINED ; 
		
		if (address == null) 
			this.address = UNDEFINED ; 
		
		if (compAddress == null) 
			this.compAddress = UNDEFINED ;
		
		if (zip == null) 
			this.zip = UNDEFINED ;
		
		if (nif == null) 
			this.nif = UNDEFINED ;
	}
}
