package controllers.api;

import java.util.Date;
import java.util.List;

import play.mvc.Controller;
import utils.CommonUtil;
import models.Tag;
import models.Task;
import models.User;

public class TaskController extends Controller{

	public static void tasks(int from, int max){
		List<Task> tasks = Task.find("end_date >= ? and is_delete = false order by create_datetime desc", new Date()).fetch(from, max);
		
		renderJSON(CommonUtil.toJson(tasks,
				"user", 
				"jobs",
				"*.class",
				"*.tasks",
				"*.id",
				"*.persistent",
				"tags.parentTag",
				"tags.childrenTags",
				"products.coupons", 
				"rewards.vouchers", 
				"rewards.images.thumbnail",
				"rewards.images.image",
				"rewards.images.file",
				"rewards.images.store",
				"images.thumbnail",
				"images.image",
				"images.file",
				"images.store"));
	}
}