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
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import flexjson.JSONSerializer;

import models.Job;
import models.Task;
import models.User;
import models.WXConfig;
import play.cache.Cache;
import play.mvc.Controller;
import utils.CommonUtil;
import utils.WXTicketCache;
import utils.WXTicketUtil;

public class WXComtroller extends Controller{
	private final static String token = "mootaskweixin";
	
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
	
}

























