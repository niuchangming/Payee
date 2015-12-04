package controllers.api;

import java.util.ArrayList;
import java.util.List;

import models.Job;
import models.Reward;
import models.User;
import models.Voucher;
import play.mvc.Controller;
import utils.CommonUtil;

public class RewardController extends Controller{
	
	public static void getRewardsByUser(String accessToken, int offset, int max){
		User dbUser = User.find("byAccessToken", accessToken).first();
		if(dbUser == null){
			renderJSON(new Error("Invalid access token"));
		}
		
		long totalCount = Job.count("byIsDeleteAndUser", false, dbUser);
		if(offset >= totalCount){
			renderJSON(new Error("No more reward being found!"));
		}
		
		List<Job> jobs = Job.find("user_id = ? and is_delete = ? order by taken_datetime desc", dbUser.id, false).from(offset).fetch(max);
		
		renderJSON(CommonUtil.toJson(jobs, 
				"*.user",
				"*.job",
				"*.class",
				"*.id",
				"*.persistent",
				"task.tags",
				"task.images.thumbnail",
				"task.images.image",
				"task.images.file",
				"task.images.store",
				"task.rewards.images.thumbnail",
				"task.rewards.task.images.image",
				"task.rewards.task.images.file",
				"task.rewards.task.images.store",
				"task.products",
				"task.jobs",
				"task.rewards.task",
				"task.rewards.vouchers",
				"vouchers"));
	}
	
}
