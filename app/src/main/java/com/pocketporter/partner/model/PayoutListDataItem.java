package com.pocketporter.partner.model;

import com.google.gson.annotations.SerializedName;

public class PayoutListDataItem{

	@SerializedName("rname")
	private String rname;

	@SerializedName("amt")
	private String amt;

	@SerializedName("r_date")
	private String rDate;

	@SerializedName("acno")
	private String acno;

	@SerializedName("upi")
	private String upi;

	@SerializedName("p_by")
	private Object pBy;

	@SerializedName("bname")
	private String bname;

	@SerializedName("id")
	private String id;

	@SerializedName("proof")
	private Object proof;

	@SerializedName("ifsc")
	private String ifsc;

	@SerializedName("paypalid")
	private String paypalid;

	@SerializedName("request_id")
	private String requestId;

	@SerializedName("status")
	private String status;

	public String getRname(){
		return rname;
	}

	public String getAmt(){
		return amt;
	}

	public String getRDate(){
		return rDate;
	}

	public String getAcno(){
		return acno;
	}

	public String getUpi(){
		return upi;
	}

	public Object getPBy(){
		return pBy;
	}

	public String getBname(){
		return bname;
	}

	public String getId(){
		return id;
	}

	public Object getProof(){
		return proof;
	}

	public String getIfsc(){
		return ifsc;
	}

	public String getPaypalid(){
		return paypalid;
	}

	public String getRequestId(){
		return requestId;
	}

	public String getStatus(){
		return status;
	}
}