package com.pocketporter.partner.model;

import com.google.gson.annotations.SerializedName;

public class Login{

	@SerializedName("ResponseCode")
	private String responseCode;

	@SerializedName("dboydata")
	private User user;

	@SerializedName("ResponseMsg")
	private String responseMsg;

	@SerializedName("currency")
	private String currency;

	@SerializedName("Result")
	private String result;

	public String getResponseCode(){
		return responseCode;
	}

	public User getUser(){
		return user;
	}

	public String getResponseMsg(){
		return responseMsg;
	}

	public String getCurrency(){
		return currency;
	}

	public String getResult(){
		return result;
	}
}