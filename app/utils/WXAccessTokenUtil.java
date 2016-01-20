package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import play.cache.Cache;

public class WXAccessTokenUtil {
	private final static String APPID = "wxeee8473b3968d003";
	private final static String APPSECRET = "d4624c36b6795d1d99dcf0547af5443d";
	
	public static String requestWXAccessToken(){
		String url = "https://api.wechat.com/cgi-bin/token?grant_type=client_credential&appid="+ APPID +"&secret=" + APPSECRET;
		
		StringBuilder result = new StringBuilder();
		URL wxUrl;
		try {
			wxUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) wxUrl.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			
			rd.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JsonElement jelement = new JsonParser().parse(result.toString());
		JsonObject jsonObj = jelement.getAsJsonObject();
		return jsonObj.get("access_token").getAsString();
	}
	
}
