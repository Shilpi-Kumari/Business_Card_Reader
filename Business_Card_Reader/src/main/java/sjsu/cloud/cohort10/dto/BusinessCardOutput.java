package sjsu.cloud.cohort10.dto;

import java.io.Serializable;

public class BusinessCardOutput implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String contactName = null;
	private String organization = null;
	private String emailId = null;
	private String contactNumber = null;
	
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

}
