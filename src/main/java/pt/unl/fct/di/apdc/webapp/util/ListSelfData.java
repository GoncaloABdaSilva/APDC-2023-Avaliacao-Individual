package pt.unl.fct.di.apdc.webapp.util;

public class ListSelfData {
	
	public String username;
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


	public ListSelfData() {
		
	}
	
	public ListSelfData(String username) {
		this.username = username;
		this.email= null ;	
		this.fullName = null;
		this.profileVisibility = null;
		this.telephoneNumber = null;
		this.mobilePhoneNumber = null;
		this.occupation = null;
		this.workPlace = null;
		this.address = null;
		this.compAddress = null;
		this.zip = null;
		this.nif = null;
		this.state = null;
		this.role = null;
	}
}
