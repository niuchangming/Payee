package controllers.api;

import java.util.Date;
import java.util.List;

import play.cache.Cache;
import play.mvc.Controller;
import utils.CommonUtil;
import models.Tag;
import models.Task;
import models.User;

public class TaskController extends Controller{

	public static void tasks(int from, int max){
		List<Task> tasks = Task.find("end_date >= ? and is_delete = false order by create_datetime desc", new Date()).fetch(from, max);
		
		renderJSON(CommonUtil.toJson(tasks,
				"*.class",
				"*.tasks",
				"*.id",
				"*.persistent",
				"*.user",
				"company.frontIC",
				"company.backIC",
				"company.businessCert",
				"company.logos.thumbnail",
				"company.logos.image",
				"company.logos.file",
				"company.logos.store",
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
				"images.store",
				"jobs"));
	}
	
	public static void tasksByTag(long tagId, int from, int max){
    	Tag dbTag = Tag.findById(tagId);
    	
    	if(dbTag == null){
    		renderJSON(new Error("Tag cannot be found."));
    	}
    	
    	if(dbTag.parentTag != null){
    		renderJSON(new Error("Tag type is not parent."));
    	}
    	
    	StringBuilder where = new StringBuilder();
    	for(Tag childTag : dbTag.childrenTags){
    		where.append("tg.id = " + childTag.id + " or ");
    	}
    	where.append("tg.id = " + dbTag.id);
    	
    	List<Task> tasks = Task.find("select t from Task t inner join t.tags tg where " + where.toString() 
    			+ " and end_date >= ? and is_delete = false order by create_datetime desc", new Date()).from(from).fetch(max);
    	
    	renderJSON(CommonUtil.toJson(tasks,
				"*.class",
				"*.tasks",
				"*.id",
				"*.persistent",
				"*.user",
				"company.frontIC",
				"company.backIC",
				"company.businessCert",
				"company.logos.thumbnail",
				"company.logos.image",
				"company.logos.file",
				"company.logos.store",
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
				"images.store",
				"jobs"));
	}
	
	public static void searchTask(String text){
    	List<Task> tasks = Task.find("title like ? and end_date >= ? and is_delete = false order by create_datetime desc", "%" + text + "%", new Date()).fetch();
    	renderJSON(CommonUtil.toJson(tasks,
				"*.class",
				"*.tasks",
				"*.id",
				"*.persistent",
				"*.user",
				"company.frontIC",
				"company.backIC",
				"company.businessCert",
				"company.logos.thumbnail",
				"company.logos.image",
				"company.logos.file",
				"company.logos.store",
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
				"images.store",
				"jobs"));
	}
	
	public static void getAllTags(){
		List<Tag> tags = Tag.find("parentTag is null").fetch();
		renderJSON(CommonUtil.toJson(tags,
				"*.class",
				"*.id",
				"*.persistent",
				"*.tasks",
				"parentTag",
				"childrenTags.childrenTags"));
	}
	
	
	
	
	
	
	
	
	
	
	
}