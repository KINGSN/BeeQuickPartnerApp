package com.pocketporter.partner.model;

import com.google.gson.annotations.SerializedName;

public class Payout{

	@SerializedName("ResponseCode")
	private String responseCode;

	@SerializedName("ResponseMsg")
	private String responseMsg;

	@SerializedName("walletbalance")
	private String walletbalance;

	@SerializedName("payoutlimit")
	private String payoutlimit;

	@SerializedName("Result")
	private String result;

	public String getResponseCode(){
		return responseCode;
	}

	public String getResponseMsg(){
		return responseMsg;
	}

	public String getWalletbalance(){
		return walletbalance;
	}

	public String getPayoutlimit(){
		return payoutlimit;
	}

	public String getResult(){
		return result;
	}
}