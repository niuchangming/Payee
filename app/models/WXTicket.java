package models;

import java.io.Serializable;
import java.util.Date;

public class WXTicket implements Serializable{
	private String ticket;
	private int expiredTime;
	private String errMsg;
	private int errCode;
	private Date getTicketDatetime;
	
	public WXTicket() {
		getTicketDatetime = new Date();
	}
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public long getExpiredTime() {
		return expiredTime;
	}
	public void setExpiredTime(int expiredTime) {
		this.expiredTime = expiredTime;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	public int getErrCode() {
		return errCode;
	}
	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}
	public Date getGetTicketDatetime() {
		return getTicketDatetime;
	}
	public void setGetTicketDatetime(Date getTicketDatetime) {
		this.getTicketDatetime = getTicketDatetime;
	}
	
}
