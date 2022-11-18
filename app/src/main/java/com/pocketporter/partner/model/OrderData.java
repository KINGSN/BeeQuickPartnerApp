package com.pocketporter.partner.model;

import com.google.gson.annotations.SerializedName;

public class OrderData{

	@SerializedName("total_complete_order")
	private String totalCompleteOrder;

	@SerializedName("total_sale")
	private String totalSale;

	@SerializedName("total_reject_order")
	private String totalRejectOrder;

	@SerializedName("cash_on_hand")
	private String cashOnHand;

	@SerializedName("total_eaning")
	private String totalEaning;

	@SerializedName("total_receive_order")
	private int totalReceiveOrder;

	public String getTotalCompleteOrder(){
		return totalCompleteOrder;
	}

	public String getTotalSale(){
		return totalSale;
	}

	public String getTotalRejectOrder(){
		return totalRejectOrder;
	}

	public String getCashOnHand(){
		return cashOnHand;
	}

	public String getTotalEaning(){
		return totalEaning;
	}

	public int getTotalReceiveOrder(){
		return totalReceiveOrder;
	}
}