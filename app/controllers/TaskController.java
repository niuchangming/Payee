package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import models.Avatar;
import models.Company;
import models.Coupon;
import models.Job;
import models.Reward;
import models.RewardImage;
import models.RewardType;
import models.Image;
import models.Product;
import models.Role;
import models.Tag;
import models.Task;
import models.TaskImage;
import models.User;
import models.WXConfig;
import play.Play;
import play.cache.Cache;
import play.db.jpa.Blob;
import play.mvc.Controller;
import play.mvc.With;
import utils.CommonUtil;
import utils.WXTicketCache;
import utils.WXTicketUtil;

@With(Interceptor.class)
public class TaskController extends Controller{
	
	public static void createTaskByTitle(String title){
		if(CommonUtil.isBlank(title)){
			renderText("Title cannot be empty.");
		}
		
		User sessionUser = renderArgs.get("user", User.class);
		Company company = Company.find("user_id = ?", sessionUser.id).first();
		if(company == null){
			renderText("Company cannot be found.");
		}
		
		Task task = new Task(company, title);
		
		renderJSON(CommonUtil.toJson(task, "images", "tags", "rewards", "company"));
	}
	
	public static void taskForm(long taskId){
		Task task = Task.findById(taskId);
		
		Iterator<RewardType> rewardTypes = EnumSet.allOf(RewardType.class).iterator();
		
		List<Tag> tags = Tag.find("parentTag is null").fetch();
		if(tags == null){
			throw new RuntimeException("Failed to read tag data from database.");
		}
		
		Reward reward = Reward.find("task_id = ?", taskId).first();
		Product product = Product.find("task_id = ?", taskId).first();
		
		render(task, reward, product, rewardTypes, tags);
	}
	
	public static void saveTaskForm(Task task, Reward reward, Product product, long tagId){
		Task dbTask = Task.findById(task.id);
		
		if(dbTask == null){
			throw new RuntimeException("Task cannot be found by task id: " + task.id);
		}
		
		Tag tag = Tag.findById(tagId);
		if(tag == null){
			throw new RuntimeException("Tag cannot be found by tag id: " + tagId);
		}
		
		task.tags = new HashSet<Tag>();
		task.tags.add(tag);
		
		dbTask.updateByTask(task);
		dbTask.rewards.iterator().next().updateByReward(reward);
		dbTask.products.iterator().next().updateByProduct(product);
		
		renderText(dbTask.id);
	}
	
	public static void uploadRewardImage(long rewardId, File blob){
		if(blob == null)
			throw new RuntimeException("Blob object cannot be null.");
		
		Reward reward = Reward.findById(rewardId);
		if(reward == null)
			throw new RuntimeException("Reward cannot be found by reward id: " + rewardId);
		
		RewardImage image;
		try {
			image = new RewardImage(reward, blob);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		}
		renderText(image.id);
	}
	
	public static void uploadProductImage(long taskId, File blob){
		if(blob == null)
			throw new RuntimeException("Blob object cannot be null.");
		
		Task task = Task.findById(taskId);
		
		if(task == null)
			throw new RuntimeException("Task cannot be found by task id: " + taskId);
		
		TaskImage image;
		try {
			image = new TaskImage(task, blob);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		}
		
		renderText(image.id);
	}
	
	public static void imageCaption(long imageId, String caption){
		TaskImage image = TaskImage.findById(imageId);
		if(image == null){
			renderText("Cannot found image.");
		}
		
		if(caption.length() > 0){
			caption = caption.replace("<!--StartFragment-->", "").replace("<!--EndFragment-->", "");
		}
		image.updateByCaption(caption);
		
		renderJSON(CommonUtil.toJson(image, 
				"*.class",
				"*.task",
				"*.id",
				"*.persistent",
				"thumbnail",
				"image",
				"file",
				"store"));
	}
	
	public static void deleteImage(long taskId, long imageId){
		Task task = Task.findById(taskId);
		if(task == null){
			throw new RuntimeException("Task cannot be found by task ID: " + taskId);
		}
		renderText(task.deleteImage(imageId));
	}
	
	public static void getImage(long imageId){
		Image image = Image.findById(imageId);
	    renderBinary(image.image.get());
	}
	
	public static void showTaskImageThumbnail(long id){
		TaskImage taskImage = TaskImage.findById(id);
		String path = Play.configuration.getProperty("images.path") + "/empty_avatar.png";
		if(taskImage != null){
			renderBinary(taskImage.thumbnail.get());
		}else{
			try {
				renderBinary(new FileInputStream(Play.getFile(path)));
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
	}
	
	public static void showRewardImageThumbnail(long id){
		RewardImage rewardImage = RewardImage.findById(id);
		String path = Play.configuration.getProperty("images.path") + "/empty_avatar.png";
		if(rewardImage != null){
			renderBinary(rewardImage.thumbnail.get());
		}else{
			try {
				renderBinary(new FileInputStream(Play.getFile(path)));
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
	}
	
	public static void allTask(int from, int max){
		User sessionUser = renderArgs.get("user", User.class);
		
		int totalCount = 0;
		int pageIndex = 0;
		
		if(sessionUser.role == Role.MERCHANT){
			Company company = Company.find("user_id = ?", sessionUser.id).first();
			totalCount = (int) Task.count("company_id = ? and is_delete = ?", company.id, false);
			List<Task> tasks = Task.find("company_id = ? and is_delete = ?", company.id, false).from(from).fetch(max);
			pageIndex = (int) Math.ceil(from / max);
			
			render(tasks, pageIndex, totalCount);
		}else{
			render(pageIndex, totalCount);
		}
	}
	
	public static void deleteTask(long taskId){
		Task task = Task.findById(taskId);
		if(task == null){
			throw new RuntimeException("Task cannot be find due to invalid task id.");
		}
		task.doDelete();
		renderText(task.id);
	}
	
	public static void taskDetail(long taskId){
		Task task = Task.findById(taskId);
		if(task == null){
			throw new RuntimeException("Task cannot be find due to invalid task id.");
		}
		
		User user = Cache.get(session.getId(), User.class);
		
		Job job = null;
		if(user != null){
			renderArgs.put("user", user);
			job = Job.find("user_id = ? and task_id = ?", user.id, task.id).first();
		}
		
		render(task, job);
	}
	
}


























