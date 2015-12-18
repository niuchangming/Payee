package controllers.api;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import models.Address;
import models.Avatar;
import models.Company;
import models.Image;
import models.Profile;
import models.Role;
import models.Task;
import models.User;
import play.db.jpa.Blob;
import play.mvc.Controller;
import utils.CommonUtil;

public class ProfileController extends Controller{
	
	public static void userInfo(String accessToken){
		User user = User.find("byAccessToken", accessToken).first();
		
		if(user == null){
			renderJSON(new Error("Invalid access token"));
		}
		
		renderJSON(CommonUtil.toJson(user, 
				"*.class",
				"*.user",
				"*.id",
				"*.profile",
				"*.company",
				"*.persistent",
				"*.companys.frontIC",
				"*.companys.backIC",
				"*.companys.businessCert",
				"*.tasks",
				"*.jobs",
				"*.password",
				"avatars.file",
				"avatars.image",
				"avatars.store",
				"boss.boss",
				"boss.cashiers",
				"boss.accessToken",
				"boss.avatars.file",
				"boss.avatars.image",
				"boss.avatars.store",
				"cashiers.boss",
				"cashiers.cashiers",
				"cashiers.accessToken",
				"cashiers.avatars.file",
				"cashiers.avatars.image",
				"cashiers.avatars.store"));
	}
	
	public static void uploadAvatar(String accessToken, Blob image){
		User user = User.find("byAccessToken", accessToken).first();
		
		if(user == null)
			renderJSON(new Error("Invalid access token"));
		
		if(image == null)
			renderJSON(new Error("File object cannot be null."));
		
		Iterator<Avatar> imageIterator = user.avatars.iterator();
		while(imageIterator.hasNext()){
			imageIterator.next().delete();
		}
		
		Avatar avatar = new Avatar(user, 0, 0, 0, 0, 0, image);
		renderJSON(CommonUtil.toJson(avatar, "*.class", "user", "task", "file", "store", "image"));
	}
	
	public static void updatePhone(String accessToken, String phone){
		User user = User.find("byAccessToken", accessToken).first();
		
		if(user == null){
			renderJSON(new Error("Invalid access token"));
		}
		
		Profile profile = null;
		Iterator<Profile> iterator = user.profiles.iterator();
		if(iterator.hasNext()){
			profile = iterator.next();
		}else{
			profile = new Profile().createProfileByUser(user);
		}
		
		profile.phone = phone;
		profile.save();
		
		renderJSON("{\"Success\": \"Update successfully.\"}");
	}
	
	public static void updateUsername(String accessToken, String firstName, String lastName){
		User user = User.find("byAccessToken", accessToken).first();
		
		if(user == null){
			renderJSON(new Error("Invalid access token"));
		}
		
		Profile profile = null;
		Iterator<Profile> iterator = user.profiles.iterator();
		if(iterator.hasNext()){
			profile = iterator.next();
		}else{
			profile = new Profile().createProfileByUser(user);
		}
		
		profile.firstName = firstName;
		profile.lastName = lastName;
		profile.save();
		
		renderJSON(CommonUtil.toJson(profile, "user", "addresses", "*.id", "*.persistent", "*.class"));
	}
	
	public static void updateAddress(String accessToken, int block, String street, String unit, String postCode){
		User user = User.find("byAccessToken", accessToken).first();
		
		if(user == null){
			renderJSON(new Error("Invalid access token"));
		}
		
		Profile profile = null;
		Iterator<Profile> profileIterator = user.profiles.iterator();
		if(profileIterator.hasNext()){
			profile = profileIterator.next();
		}else{
			profile = new Profile().createProfileByUser(user);
		}
		
		Address address = null;
		Iterator<Address> addressIterator = profile.addresses.iterator();
		if(addressIterator.hasNext()){
			address = addressIterator.next();
			address.block = block;
			address.street = street;
			address.unit = unit;
			address.postCode = postCode;
			address.save();
		}else{
			address = new Address();
			address.block = block;
			address.street = street;
			address.unit = unit;
			address.postCode = postCode;
			address.createAddressByProfile(profile);
		}
		
		renderJSON(CommonUtil.toJson(address, "profile", "company", "*.id", "*.persistent", "*.class"));
	}
	
	public static void updateCompanyBasicInfo(String accessToken, String companyName, String description, String contactNo, String fax){
		User user = User.find("byAccessToken", accessToken).first();
		
		if(user == null){
			renderJSON(new Error("Invalid access token"));
		}
		
		Company company = null;
		Iterator<Company> companyIterator = user.companys.iterator();
		if(companyIterator.hasNext()){
			company = companyIterator.next();
			company.name = companyName;
			company.description = description;
			company.fax = fax;
			company.save();
		}else{
			company = new Company();
			company.name = companyName;
			company.description = description;
			company.fax = fax;
			company.createCompanyByUser(user);
		}
		
		renderJSON(CommonUtil.toJson(company, "*.id", "*.persistent", "*.class", "user", "frontIC", "backIC", "businessCert"));
	}
	
	public static void uploadCompanyLogo(String accessToken, Blob image){
		User user = User.find("byAccessToken", accessToken).first();
		
		if(user == null)
			renderJSON(new Error("Invalid access token"));
		
		if(image == null)
			renderJSON(new Error("File object cannot be null."));
		
		Company company = null;
		if(!user.companys.iterator().hasNext()){
			company = new Company();
			company.createCompanyByUser(user);
		}
		
		Iterator<Avatar> imageIterator = user.avatars.iterator();
		while(imageIterator.hasNext()){
			imageIterator.next().delete();
		}
		
		Avatar avatar = new Avatar(user, 0, 0, 0, 0, 0, image);
		renderJSON(CommonUtil.toJson(avatar, "*.class", "user", "task", "file", "store", "image"));
	}
	
	public static void companyAddresses(String accessToken, long taskId){
		User user = User.find("byAccessToken", accessToken).first();
		
		if(user == null){
			renderJSON(new Error("Invalid access token"));
		}
		
		Task task = Task.findById(taskId);
		if(task == null){
			renderJSON(new Error("Task cannot be found."));
		}
		
		Set<Address> addresses = task.user.companys.iterator().next().addresses;
		
		renderJSON(CommonUtil.toJson(addresses, 
				"*.id", 
				"*.persistent", 
				"*.class", 
				"profile", 
				"company"));
	}
	
	public static void addCashier(String accessToken, String cashierAccessToken){
		User user = User.find("access_token = ?", accessToken).first();
		if(user == null){
			renderJSON(new Error("Invalid access token"));
		}
		
		if(user.role != Role.MERCHANT || accessToken.equals(cashierAccessToken)){
			renderJSON(new Error("Permission error."));
		}
		
		User cashier = User.find("access_token = ?", cashierAccessToken).first();
		if(cashier == null){
			renderJSON(new Error("Cannot found the cashier."));
		}
		
		cashier.updateByBoss(user);
		
		renderJSON("{\"success\": \"Add successfully.\"}");
	}
	
	public static void cashiers(String accessToken){
		User user = User.find("access_token = ?", accessToken).first();
		if(user == null){	
			renderJSON(new Error("Invalid access token"));
		}
		
		renderJSON(CommonUtil.toJson(user.cashiers, 
				"*.class",
				"*.id",
				".user",
				".profile",
				"*.persistent",
				"password",
				"accessToken",
				"companys",
				"tasks",
				"jobs",
				"boss",
				"cashiers",
				"avatars.file",
				"avatars.image",
				"avatars.store"));
	}
	
	public static void removeCashier(long entityId){
		User cashier = User.findById(entityId);
		if(cashier == null){
			renderJSON(new Error("Cannot found the user."));
		}
		
		cashier.removeBoss();
		
		renderJSON("{\"success\": \"Remove successfully.\"}");
	}
}









