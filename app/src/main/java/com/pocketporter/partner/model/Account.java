package com.pocketporter.partner.model;

import com.google.gson.annotations.SerializedName;

public class Account{

	@SerializedName("ResponseCode")
	private String responseCode;

	@SerializedName("order_data")
	private OrderData orderData;

	@SerializedName("dboydata")
	private User user;

	@SerializedName("ResponseMsg")
	private String responseMsg;

	@SerializedName("Result")
	private String result;

	@SerializedName("payoutlimit")
	private String payoutlimit;

	public String getResponseCode(){
		return responseCode;
	}

	public OrderData getOrderData(){
		return orderData;
	}

	public String getResponseMsg(){
		return responseMsg;
	}

	public String getResult(){
		return result;
	}

	public com.pocketporter.partner.model.User getUser() {
		return user;
	}

	public void setUser(com.pocketporter.partner.model.User user) {
		this.user = user;
	}

	public String getPayoutlimit() {
		return payoutlimit;
	}

	public void setPayoutlimit(String payoutlimit) {
		this.payoutlimit = payoutlimit;
	}
}