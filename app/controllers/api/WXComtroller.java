package controllers.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import models.Job;
import models.Task;
import models.User;
import play.mvc.Controller;
import utils.CommonUtil;

public class WXComtroller extends Controller{
	private static String token = "mootaskweixin";
	private static String APPID = "wxea51457ffb869624";
	private static String APPSECRET = "c2cafbffe86dba7cb54ac51e9dc4f833";
	
	public static void checkSignature(String signature, String timestamp, String nonce, String echostr) {
        String[] arr = new String[] { token, timestamp, nonce };
        Arrays.sort(arr);
        
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
        MessageDigest md = null;
        String tmpStr = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(content.toString().getBytes());
            tmpStr = CommonUtil.byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        content = null;
        
        boolean isValidSign = tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
        if(isValidSign){
        	renderText(echostr);
        }
    }
	
	public static void getConfig(){
		String url = "https://api.wechat.com/cgi-bin/token?grant_type=client_credential&appid="+ APPID +"&secret=" + APPSECRET;
		try {
			String response = getWXAccessToken(url);
			JsonElement jelement = new JsonParser().parse(response);
			JsonObject jsonObj = jelement.getAsJsonObject();
			String accessToken = jsonObj.get("access_token").toString();
			if(!CommonUtil.isBlank(accessToken)){
				String ticket = getWXTicket(accessToken);
				System.out.println("-----> " + ticket);
			}
		} catch (Exception e) {
			renderText("Error: " + e.getMessage());
		}
	}
	
	private static String getWXAccessToken(String urlToRead) throws Exception {
		StringBuilder result = new StringBuilder();
		URL url = new URL(urlToRead);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		return result.toString();
	}
	
	private static String getWXTicket(String accessToken) throws IOException{
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
		return result.toString();
	}
	
	public static void main(String[] args){
		getConfig();
	}
}

























