package controllers;

import play.*;
import play.cache.Cache;
import play.db.helper.JpqlSelect;
import play.db.jpa.JPA;
import play.mvc.*;
import utils.CommonUtil;

import java.util.*;

import javax.persistence.Query;

import controllers.api.Error;

import antlr.Utils;
import models.*;

public class Application extends Controller {
	public final static int DEFAULT_CCOUNT_PER_PAGE = 10;
	
    public static void index() {
    	User user = Cache.get(session.getId(), User.class);
   
    	List<Task> tasks = Task
    			.find("end_date >= ? and is_delete = false order by create_datetime desc", new Date())
    			.fetch(DEFAULT_CCOUNT_PER_PAGE);
    	
    	List<Tag> tags = Tag.find("parentTag is null").fetch();
		
        render(user, tasks, tags);
    }
    
    public static void getTasksByRange(int offset, long tagId){
    	List<Task> tasks = null;
    	if(tagId == -1){
    		tasks = Task.find("end_date >= ? and is_delete = false order by create_datetime desc", new Date())
        			.from(offset).fetch(DEFAULT_CCOUNT_PER_PAGE);
    	}else{
    		Tag dbTag = Tag.findById(tagId);
    		
    		if(dbTag == null ){
    			renderJSON(new Error("Tag cannot be found"));
    		}
    		
    		if(dbTag.parentTag != null){
    			renderJSON(new Error("Tag type is not parent."));
    		}
    		
    		StringBuilder where = new StringBuilder();
        	for(Tag childTag : dbTag.childrenTags){
        		where.append("tg.id = " + childTag.id + " or ");
        	}
        	where.append("tg.id = " + dbTag.id);
        	
        	tasks = Task.find("select t from Task t inner join t.tags tg where " + where.toString() 
        			+ " and end_date >= ? and is_delete = false order by create_datetime desc", new Date())
        			.from(offset).fetch(DEFAULT_CCOUNT_PER_PAGE);
    	}
    	
    	User user = Cache.get(session.getId(), User.class);
    	
    	renderTemplate("/Application/taskpage.html", tasks, offset, tagId, user);
    }
    
    public static void searchTask(String text){
    	List<Task> tasks = Task.find("title like ? and end_date >= ? and is_delete = false order by create_datetime desc", "%" + text + "%", new Date()).fetch();
    	
    	List<Tag> tags = Tag.find("parentTag is null").fetch();
		
		//issue 1
		User user = Cache.get(session.getId(), User.class);
		renderArgs.put("user", user);
		
    	renderTemplate("/Application/index.html", tasks, tags);
	}
    
    public static void getTaskByTag(long tagId){
    	List<Tag> tags = Tag.find("parentTag is null").fetch();
    	
    	//issue 1
		User user = Cache.get(session.getId(), User.class);
		renderArgs.put("user", user);
    	
    	if(tagId == -1){
    		List<Task> tasks = Task.find("end_date >= ? and is_delete = false order by create_datetime desc", new Date()).fetch();
        	renderTemplate("/Application/index.html", tasks, tags, tagId);
    	}
    	
    	Tag dbTag = Tag.findById(tagId);
    	
    	if(dbTag == null){
    		throw new RuntimeException("Tag cannot be found by tag id: " + tagId);
    	}
    	
    	if(dbTag.parentTag != null){
    		throw new RuntimeException("Tag type is not parent.");
    	}
    	
    	StringBuilder where = new StringBuilder();
    	for(Tag childTag : dbTag.childrenTags){
    		where.append("tg.id = " + childTag.id + " or ");
    	}
    	where.append("tg.id = " + dbTag.id);
    	
    	List<Task> tasks = Task.find("select t from Task t inner join t.tags tg where " + where.toString() 
    			+ " and end_date >= ? and is_delete = false order by create_datetime desc", new Date())
    			.fetch(DEFAULT_CCOUNT_PER_PAGE);
    	
    	renderTemplate("/Application/index.html", tasks, tags, tagId);
    }
}























