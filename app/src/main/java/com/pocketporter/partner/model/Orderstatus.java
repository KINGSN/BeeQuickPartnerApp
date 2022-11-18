package com.pocketporter.partner.model;

import com.google.gson.annotations.SerializedName;

public class Orderstatus{

	@SerializedName("ResponseCode")
	private String responseCode;

	@SerializedName("Next_step")
	private String nextStep;

	@SerializedName("ResponseMsg")
	private String responseMsg;

	@SerializedName("Result")
	private String result;

	public String getResponseCode(){
		return responseCode;
	}

	public String getNextStep(){
		return nextStep;
	}

	public String getResponseMsg(){
		return responseMsg;
	}

	public String getResult(){
		return result;
	}
}