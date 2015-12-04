package controllers;

import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import models.Access;
import models.Deal;
import models.Job;
import models.RewardType;
import models.Task;
import models.User;
import models.Voucher;
import play.mvc.Controller;
import play.mvc.With;
import utils.AES;
import utils.CommonUtil;

@With(Interceptor.class)
public class JobController extends Controller{
	
	public static void addJob(long taskId){
		Task dbTask = Task.findById(taskId);
		
		if(dbTask == null){
			throw new RuntimeException("Task cannot be found by the task ID: " + taskId);
		}
		
		User sessionUser = renderArgs.get("user", User.class);
		User dbUser = User.findById(sessionUser.id);
		
		if(!dbUser.hasJob(dbTask.id)){
			Job job = new Job(dbUser, dbTask);
			renderText(job.id);
		}else{
			renderText("You have added the job already.");
		}
	}
	
	public static void myJobs(int from, int max){
		User sessionUser = renderArgs.get("user", User.class);
		
		long totalCount = Job.count("byIsDeleteAndUser", false, sessionUser);
		int pageIndex = (int) Math.ceil(from / max);
		
		List<Job> jobs = Job.find("user_id = ? and is_delete = ? order by taken_datetime desc", sessionUser.id, false).from(from).fetch(max);
		
		render(jobs, pageIndex, totalCount);
	}
	
	public static void deleteJob(long jobId){
		Job job = Job.findById(jobId);
		if(job == null){
			throw new RuntimeException("Job cannot be find due to invalid task id.");
		}
		job.doDelete();
		renderText(job.id);
	}
	
	public static void viewProduct(String jobToken){
		Job job = Job.find("byToken", jobToken).first();
		if(job == null){
			throw new RuntimeException("Job cannot be found by the job token: " + jobToken);
		}
		
		Access access = Access.find("byJobAndIp", job, request.remoteAddress).first();
		if(access == null){
			job.updateByAccess(new Access(job, request.remoteAddress));
		}
		
		Task task = job.task;
		String qrcode = jobToken + "|" + UUID.randomUUID().toString().trim(); 
		renderTemplate("/JobController/productDetail.html", task, qrcode);
	}
	
	public static void scanReciever(String qrcode, String userAccessToken){
		Deal deal = Deal.find("byQrcode", qrcode).first();
		if(deal != null){
			renderText("QR Code has been scanned!");
		}
		
		String jobToken = qrcode.split("\\|")[0];
		Job job = Job.find("byToken", jobToken).first();
		if(job == null){
			renderText("Job cannot be found by the job token: " + jobToken);
		}
		
		if(job.task.user.accessToken.equals(userAccessToken)){
			job.createDeal(request.remoteAddress, qrcode);
			renderText("success");
		}else{
			renderText("You don't have the right permission for the action.");
		}
	}
	
	public static void voucher(long jobId, double value){
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
	
		render(voucher);
	}
	
	public static void voucherScan(String jobToken){
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
		renderJSON(deals);
	}
}














