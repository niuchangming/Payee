package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import models.WXConfig;
import models.WXTicket;

public class WXTicketUtil {
	public final static String TICKET_KEY = "wx_ticket";
	
	public static WXTicket getWXTicket() throws IOException{
        String accessToken = WXAccessTokenUtil.requestWXAccessToken();
        
        if(CommonUtil.isBlank(accessToken)){
        	return null;
        }
        
        StringBuilder result = new StringBuilder();
		String ticketUrl="https://api.wechat.com/cgi-bin/ticket/getticket?type=jsapi&access_token="+accessToken;
		
		URL url = new URL(ticketUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		
		JsonElement jelement = new JsonParser().parse(result.toString());
		JsonObject jsonObj = jelement.getAsJsonObject();
		int errCode = jsonObj.get("errcode").getAsInt();
		String ticket = "";
		String errMsg = "";
		int expiredSecond = 0;
		if(errCode == 0){
			ticket = jsonObj.get("ticket").getAsString();
			errMsg = jsonObj.get("errmsg").getAsString();
			expiredSecond = jsonObj.get("expires_in").getAsInt();
		}
		
		WXTicket wxTicket = new WXTicket();
		wxTicket.setErrCode(errCode);
		wxTicket.setErrMsg(errMsg);
		wxTicket.setExpiredTime(expiredSecond);
		wxTicket.setTicket(ticket);
		
		return wxTicket;
    }
   
    public static WXConfig sign(String jsapi_ticket, String url) {
        WXConfig config;
    	String nonce_str = create_nonce_str();
        String timestamp = create_timestamp();
        String signature = "";
        
        String paramStr = "jsapi_ticket=" + jsapi_ticket +
                  "&noncestr=" + nonce_str +
                  "&timestamp=" + timestamp +
                  "&url=" + url;
        
        try{
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(paramStr.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        
        config = new WXConfig(url, jsapi_ticket, nonce_str, timestamp, signature);

        return config;
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash){
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private static String create_nonce_str() {
    	return UUID.randomUUID().toString();
    }

    private static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
}
