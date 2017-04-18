package com.github.aikivinen.birtdemo.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users") // "user" is actually a reserved word in postgresql so
						// table name has to be defined as "users". Apparently
						// there is no automatic pluralization...
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int userid;

	private String username;
	private String password;
	private boolean enabled;
	private String email;
	private String phone;
	private String authority;

	private boolean rightToAddReport;
	private boolean rightToPrintReport;
	private boolean rightToRemoveReport;
	private boolean rightToEditReport;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public boolean isRightToAddReport() {
		return rightToAddReport;
	}

	public void setRightToAddReport(boolean rightToAddReport) {
		this.rightToAddReport = rightToAddReport;
	}

	public boolean isRightToPrintReport() {
		return rightToPrintReport;
	}

	public void setRightToPrintReport(boolean rightToPrintReport) {
		this.rightToPrintReport = rightToPrintReport;
	}

	public boolean isRightToRemoveReport() {
		return rightToRemoveReport;
	}

	public void setRightToRemoveReport(boolean rightToRemoveReport) {
		this.rightToRemoveReport = rightToRemoveReport;
	}

	public boolean isRightToEditReport() {
		return rightToEditReport;
	}

	public void setRightToEditReport(boolean rightToEditReport) {
		this.rightToEditReport = rightToEditReport;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

}
