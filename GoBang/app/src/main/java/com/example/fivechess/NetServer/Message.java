package com.example.fivechess.NetServer;

import java.io.Serializable;

public class Message implements Serializable{

	private static final long serialVersionUID = 7965470946723147485L;
	private int type;
	private User user;
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getState() {
		return type;
	}

	public void setState(int type) {
		this.type = type;
	}

}
