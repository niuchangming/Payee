package controllers.api;

import java.util.Date;

import models.FBUser;
import models.NormalUser;
import models.User;
import play.cache.Cache;
import play.mvc.Controller;
import utils.CommonUtil;

public class SessionController extends Controller{
	
	public static void signup(String email, String password){
		if(!notExists(email)){
			renderJSON(new Error("User already exists."));
		}
		
    	User user = new NormalUser(email, password);
    	renderJSON(CommonUtil.toJson(user, 
    			"*.class",
				"*.user",
				"*.profile",
				"*.id",
				"*.persistent",
				"companys",
				"tasks",
				"jobs"));
    }
	
	public static void login(String email, String password) {
		NormalUser user= NormalUser.find("byEmailAndPasswordAndIsDelete", email, CommonUtil.md5(password), false).first();
		if(user == null)
			renderJSON(new Error("Email or password is incorrect."));
		
		renderJSON(CommonUtil.toJson(user, 
    			"*.class",
				"*.user",
				"*.profile",
				"*.id",
				"*.persistent",
				"avatars.file",
				"avatars.image",
				"avatars.store",
				"companys",
				"tasks",
				"jobs"));
    }
	
	public static void loginWithFB(String fbId){
		if(CommonUtil.isBlank(fbId))
			renderJSON(new Error("Facebook ID is empty."));
		
		FBUser user = FBUser.find("byFbIdAndIsDelete", fbId, false).first();
		if(user == null)
			user = new FBUser(fbId);
			
		renderJSON(CommonUtil.toJson(user, 
    			"*.class",
				"*.user",
				"*.profile",
				"*.id",
				"*.persistent",
				"avatars.file",
				"avatars.image",
				"avatars.store",
				"companys",
				"tasks",
				"jobs"));
	}
	
	public static boolean notExists(String email){
		User user = User.find("byEmail", email).first();
		if (user != null)
			return false;

		return true;
	}
	
}
