package controllers.api;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import models.Access;
import models.Deal;
import models.Job;
import models.Task;
import models.User;
import models.Voucher;
import play.mvc.Controller;
import utils.CommonUtil;

public class JobController extends Controller{
	
	public static void addJob(long taskId, String accessToken){
		Task dbTask = Task.findById(taskId);
		
		if(dbTask == null){
			renderJSON(new Error("Task cannot be found."));
		}
		
		User dbUser = User.find("byAccessToken", accessToken).first();
		if(dbUser == null){
			renderJSON(new Error("User cannot be found."));
		}
		
		if(!dbUser.hasJob(dbTask.id)){
			Job job = new Job(dbUser, dbTask);
			renderJSON(CommonUtil.toJson(job, "*.class", "*.id", "*.persistent","user", "task", "accesses", "deals", "vouchers"));
		}else{
			renderJSON(new Error("You already got the job."));
		}
	}
	
	public static void getJobsByUser(String accessToken){
		User dbUser = User.find("byAccessToken", accessToken).first();
		if(dbUser == null){
			renderJSON(new Error("Invalid access token"));
		}
		
		List<Job> jobs = Job.find("user_id = ? and is_delete = ? order by taken_datetime desc", dbUser.id, false).fetch();
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
				"task.products.task",
				"task.products.coupons.product",
				"task.jobs",
				"task.rewards.task",
				"task.rewards.vouchers",
				"task.rewards.images.thumbnail",
				"task.rewards.images.image",
				"task.rewards.images.file",
				"task.rewards.images.store",
				"task.rewards.task.images.image",
				"task.rewards.task.images.file",
				"task.rewards.task.images.store",
				"vouchers",
				"deals.job",
				"accesses"));
	}
	
	public static void deleteJob(String accessToken, long JobId){
		User dbUser = User.find("byAccessToken", accessToken).first();
		if(dbUser == null){
			renderJSON(new Error("Invalid access token"));
		}
		
		Job job = Job.findById(JobId);
		if(job == null){
			throw new RuntimeException("Job cannot be find due to invalid task id.");
		}
		job.doDelete();
		renderJSON(CommonUtil.toJson(job, "*.class", "*.id", "*.persistent","user", "task", "accesses", "deals", "vouchers"));
	}
	
	public static void getJobByTaskAndAccessToken(long taskId, String accessToken){
		Task task = Task.find("id = ? and is_delete = ? and end_date >= ?", taskId, false, new Date()).first();
		User user = User.find("is_delete = ? and access_token = ?", false, accessToken).first();
		
		if(task == null || user == null){
			renderJSON(new Error("Invalid access token or task id."));
		}
		
		Job job = Job.find("byUserAndTask", user, task).first();
		if(job == null){
			renderJSON(new Error("Job can not be found."));
		}
		
		renderJSON(CommonUtil.toJson(job, "*.class", "*.id", "*.persistent","user", "task", "accesses", "deals", "vouchers"));
	}
	
	public static void viewProduct(String jobToken){
		Job job = Job.find("byToken", jobToken).first();
		if(job == null){
			renderJSON(new Error("Job cannot be found by the job token: " + jobToken));
		}
		
		Access access = Access.find("byJobAndIp", job, request.remoteAddress).first();
		if(access == null){
			job.updateByAccess(new Access(job, request.remoteAddress));
		}
		
		Task task = job.task;
		String qrcode = jobToken + "|" + UUID.randomUUID().toString().trim(); 
		renderTemplate("/JobController/productDetail.html", task, qrcode);
	}
	
	public static void scanReciever(String qrcode, String accessToken){
		Deal deal = Deal.find("byQrcode", qrcode).first();
		if(deal != null){
			renderJSON(new Error("QR Code has been scanned!"));
		}
		
		String jobToken = qrcode.split("\\|")[0];
		Job job = Job.find("byToken", jobToken).first();
		if(job == null){
			renderJSON(new Error("Job cannot be found."));
		}
		
		if(job.task.user.accessToken.equals(accessToken)){
			job.createDeal(request.remoteAddress, qrcode);
			Set<Deal> deals = job.deals;
			renderJSON(CommonUtil.toJson(deals, "job", "*.id", "*.persistent"));
		}else{
			renderJSON(new Error("You don't have the right permission for the action."));
		}
	}
	
	public static void voucher(String accessToken, long jobId, double value){
		User dbUser = User.find("byAccessToken", accessToken).first();
		if(dbUser == null){
			renderJSON(new Error("Invalid access token"));
		}
		
		Job job = Job.findById(jobId);
		if(job == null){
			throw new RuntimeException("Job cannot be found by the job ID: " + jobId);
		}
		
		Voucher voucher = null;
		if(job.vouchers.size() == 0){
			voucher = new Voucher(job, value);
			job.vouchers.add(voucher);
		}else{
			voucher = job.vouchers.iterator().next();
			if(voucher.value != value){
				voucher.updateByValue(value);
			}
		}
	
		renderJSON(CommonUtil.toJson(voucher, 
				"*.class", 
				"*.id", 
				"*.persistent",
				"reward.images.thumbnail",
				"reward.images.image",
				"reward.images.file",
				"reward.images.store",
				"reward.vouchers",
				"reward.task",
				"job"));
	}
	
	public static void voucherScan(String accessToken, String jobToken){
		User dbUser = User.find("byAccessToken", accessToken).first();
		if(dbUser == null){
			renderJSON(new Error("Invalid access token"));
		}
		
		Job job = Job.find("byToken", jobToken).first();
		if(job == null){
			renderText("Job cannot be found by the job token: " + jobToken);
		}
		
		Voucher voucher = job.vouchers.iterator().next();
		
		if(!voucher.isValid){
			renderText("The voucher is already invalid.");
		}
		
		if(voucher.reward.expireDate.before(new Date())){
			renderText("The voucher has expired.");
		}
		
		voucher.updateVoucherByScan();
		Set<Deal> deals = job.deals;
		renderJSON(CommonUtil.toJson(deals, "job", "*.id", "*.persistent"));
	}
	
}
























