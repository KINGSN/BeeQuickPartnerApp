package com.pocketporter.partner.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PayTrazection{

	@SerializedName("ResponseCode")
	private String responseCode;

	@SerializedName("ResponseMsg")
	private String responseMsg;

	@SerializedName("PayoutListData")
	private List<PayoutListDataItem> payoutListData;

	@SerializedName("Result")
	private String result;

	public String getResponseCode(){
		return responseCode;
	}

	public String getResponseMsg(){
		return responseMsg;
	}

	public List<PayoutListDataItem> getPayoutListData(){
		return payoutListData;
	}

	public String getResult(){
		return result;
	}
}