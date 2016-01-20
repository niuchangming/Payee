package models;

public class WXConfig {
	public String url;
	public String ticket;
	public String nonceStr;
	public String timestamp;
	public String signature;
	
	public WXConfig(String url, String ticket, String nonceStr,
			String timestamp, String signature) {
		super();
		this.url = url;
		this.ticket = ticket;
		this.nonceStr = nonceStr;
		this.timestamp = timestamp;
		this.signature = signature;
	}
	

}
