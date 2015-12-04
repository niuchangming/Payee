package controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import models.FBUser;
import models.NormalUser;
import models.User;
import play.cache.Cache;
import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;
import utils.CommonUtil;

public class SessionController extends Controller{

	public static void logout() {
		session.clear();
		Application.index();
	}
	
	public static void signup(String email, String password){
		if(!notExists(email)){
			renderText("User already exists.");
		}
    	new NormalUser(email, password);
    	login(email, password);
    }
	
	public static boolean notExists(String email){
		User user = User.find("byEmail", email).first();
		if (user != null)
			return false;

		return true;
	}
	
	public static void login(String email, String password) {
		NormalUser user= NormalUser.find("byEmailAndPasswordAndIsDelete", email, CommonUtil.md5(password), false).first();
		if(user == null)
			renderText("Email or password is incorrect.");
		
    	Cache.set(session.getId(), user);
    	renderArgs.put("user", user);
    
    	renderText("success");
    }
	
	public static void loginWithFB(String fbId){
		if(CommonUtil.isBlank(fbId))
			renderText("Facebook ID is empty.");
		
		FBUser fbUser = FBUser.find("byFbId", fbId).first();
		if(fbUser == null){
			fbUser = new FBUser(fbId);
		}

    	Cache.set(session.getId(), fbUser);
    	renderArgs.put("user", fbUser);
    	
		renderText("success");
	}
	
}
















