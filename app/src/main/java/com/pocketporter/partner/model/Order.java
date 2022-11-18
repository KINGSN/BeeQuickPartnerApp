package com.pocketporter.partner.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Order{

	@SerializedName("ResponseCode")
	private String responseCode;

	@SerializedName("order_data")
	private List<OrderDataItem> orderData;

	@SerializedName("ResponseMsg")
	private String responseMsg;

	@SerializedName("Result")
	private String result;

	public String getResponseCode(){
		return responseCode;
	}

	public List<OrderDataItem> getOrderData(){
		return orderData;
	}

	public String getResponseMsg(){
		return responseMsg;
	}

	public String getResult(){
		return result;
	}
}