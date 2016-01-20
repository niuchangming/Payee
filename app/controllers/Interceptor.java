package controllers;

import models.User;
import play.cache.Cache;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Controller;
import utils.CommonUtil;

public class Interceptor extends Controller{
	
	@Before(priority = 1, unless={"Application.searchTask", 
								"TaskController.taskDetail", 
								"TaskController.getImage", 
								"TaskController.showTaskImageThumbnail",
								"TaskController.wxShare",
								"ProfileController.showCompanyLogo",
								"ProfileController.showLogoById",
								"ProfileController.showAvatar",
								"JobController.viewProduct",
								"JobController.scanReciever"})
	static void authenticated() {
		User user = Cache.get(session.getId(), User.class);
		
		if (user == null) {
			Application.index();
		}
		
		renderArgs.put("user", user);
	}
	
}
