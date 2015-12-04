package controllers;

import java.util.List;

import models.Job;
import models.User;
import play.mvc.Controller;
import play.mvc.With;

@With(Interceptor.class)
public class AwardController extends Controller{
	
	public static void myAwards(int from, int max){
		User sessionUser = renderArgs.get("user", User.class);
		List<Job> jobs = Job.find("user_id = ? and is_delete = ? order by taken_datetime desc", sessionUser.id, false).fetch(from, max);
		
		long totalCount = Job.count("byIsDeleteAndUser", false, sessionUser);
		int pageIndex = (int) Math.ceil(from / max);
		
		render(jobs, pageIndex, totalCount);
	}
	
}
