package com.wireme.util;

public class DataMessage {
	// private statue;
	private String msg;
	private MyDataManager.MessageType status = MyDataManager.MessageType.UNKOWN;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public MyDataManager.MessageType getStatus() {
		return status;
	}

	public void setStatus(MyDataManager.MessageType status) {
		this.status = status;
	}

}
