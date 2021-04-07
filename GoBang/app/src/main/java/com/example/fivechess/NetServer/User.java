package com.example.fivechess.NetServer;

import java.io.Serializable;

public class User implements Serializable{

	private static final long serialVersionUID = 7965470946723147484L;
	private String account;
	private String name;
	private String password;
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
