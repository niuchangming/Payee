package controllers;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import controllers.api.Error;

import models.Address;
import models.Avatar;
import models.Company;
import models.Country;
import models.Logo;
import models.NormalUser;
import models.Profile;
import models.Reward;
import models.RewardImage;
import models.Role;
import models.Task;
import models.TaskImage;
import models.User;
import models.VerifyStatus;
import play.Play;
import play.cache.Cache;
import play.data.Upload;
import play.db.jpa.Blob;
import play.db.jpa.JPA;
import play.mvc.Controller;
import play.mvc.With;
import utils.CommonUtil;

@With(Interceptor.class)
public class ProfileController extends Controller{
	
	public static void profile(){
		User sessionUser = renderArgs.get("user", User.class);
		Profile profile = Profile.find("user_id = ?", sessionUser.id).first();
		render(profile);
	}
	
	public static void saveProfile(Profile profile){
		User sessionUser = renderArgs.get("user", User.class);
		
		Profile dbProfile = Profile.find("user_id = ?", sessionUser.id).first();
		if(dbProfile == null){
			User dbUser = User.findById(sessionUser.id);
			profile.createProfileByUser(dbUser);
		}else{
			dbProfile.updateByProfile(profile);
		}
		
		renderText("success");
	}
	
	public static void resetPassword(){
		render();
	}
	
	public static void changePassword(String currentPassword, String newPassword){
		User sessionUser = renderArgs.get("user", User.class);
		NormalUser dbUser = NormalUser.findById(sessionUser.id);
		
		if(!dbUser.password.equals(CommonUtil.md5(currentPassword))){
			renderText("The current password is incorrect.");
		}else{
			dbUser.updateByPassword(newPassword);
			SessionController.logout();
		}
	}
	
	public static boolean isSameAsDBPassword(String currentPassword){
		NormalUser sessionUser = renderArgs.get("user", NormalUser.class);
		return sessionUser.password.equals(CommonUtil.md5(currentPassword));
	}
	
	public static void showAvatar(long userId){
		Avatar avatar = Avatar.find("user_id = ? order by uploaded_datetime desc", userId).first();
		String path = Play.configuration.getProperty("images.path") + "/empty_avatar.png";
		if(avatar != null){
			path = avatar.thumbnailPath.replace(Play.configuration.getProperty("application.baseUrl"), "");
		}
		
		try {
			renderBinary(new FileInputStream(Play.getFile(path)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void uploadAvatar(int x, int y, int width, int height, float ratio, Blob image){
		User sessionUser = renderArgs.get("user", User.class);
		User dbUser = User.findById(sessionUser.id);
		
		if(dbUser == null)
			throw new RuntimeException("User cannot be found.");
		
		if(image == null)
			throw new RuntimeException("File object cannot be null.");
		
		Iterator<Avatar> imageIterator = dbUser.avatars.iterator();
		while(imageIterator.hasNext()){
			imageIterator.next().delete();
		}
		
		Avatar avatar = new Avatar(dbUser, x, y, width, height, ratio, image);
		renderJSON(CommonUtil.toJson(avatar, "*.class", "user", "task", "file", "store", "image"));
	}
	
	public static void company(){
		User sessionUser = renderArgs.get("user", User.class);
		Company company = Company.find("user_id = ?", sessionUser.id).first();
		render(company);
	}
	
	public static void saveCompany(Company company, Address[] addresses){
		User sessionUser = renderArgs.get("user", User.class);
		Company dbCompany = Company.find("user_id = ?", sessionUser.id).first();
		if(dbCompany == null){
			User dbUser = User.findById(sessionUser.id);
			dbCompany = company.createCompanyByUser(dbUser);
		}else{
			dbCompany.updateByCompany(company);
		}
		
		if(addresses != null && addresses.length > 0){
			for(int i = 0; i < addresses.length; i++){
				if(addresses[i].block == 0 
						|| CommonUtil.isBlank(addresses[i].street) 
						|| CommonUtil.isBlank(addresses[i].unit) 
						|| addresses[i].postCode == 0)
					continue;
				
				addresses[i].createAddressByCompany(dbCompany);
			}
		}
		
		renderText("success");
	}
	
	public static void uploadCompanyLogo(int x, int y, int width, int height, float ratio, Blob image){
		User sessionUser = renderArgs.get("user", User.class);
		Company dbCompany = Company.find("user_id = ?", sessionUser.id).first();
		if(dbCompany == null){
			User user = User.findById(sessionUser.id);
			dbCompany = new Company().createCompanyByUser(user);
		}
		
		if(image == null)
			throw new RuntimeException("Logo object cannot be null.");
		
		if(!image.type().equals("image/jpeg") && !image.type().equals("image/png"))
			throw new RuntimeException("Uploaded logo must be png or jpeg.");
		
		Iterator<Logo> logoIterator = dbCompany.logos.iterator();
		while(logoIterator.hasNext()){
			logoIterator.next().delete();
		}
		
		Logo logo = new Logo(dbCompany, x, y, width, height, ratio, image);
		renderJSON(CommonUtil.toJson(logo, "*.class", "user", "company", "file", "store", "image"));
	}
	
	public static void showCompanyLogo(long companyId){
		Logo logo = Logo.find("company_id = ? order by uploaded_datetime desc", companyId).first();
		String path = Play.configuration.getProperty("images.path") + "/default_company_logo.png";
		if(logo != null){
			path = logo.thumbnailPath.replace(Play.configuration.getProperty("application.baseUrl"), "");
		}
		
		try {
			renderBinary(new FileInputStream(Play.getFile(path)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteAddress(long addressId){
		Address.delete("id = ?", addressId);
		company();
	}
	
	public static void verifyCompany(){
		User sessionUser = renderArgs.get("user", User.class);
		User dbUser = User.findById(sessionUser.id);
		
		if(dbUser.companys.size() == 0){
			new RuntimeException("Your account has not fill with you company' information yet.");
		}
		
		Company company = Company.findById(dbUser.companys.iterator().next().id);
		if(company == null){
			new RuntimeException("Company cannot be found.");
		}
		
		render(company);
	}
	
	public static void verifyUpload(long companyId, Blob frontIC, Blob backIC, Blob bizFile){
		if(frontIC == null)
			renderText("Your IC front side cannot be empty.");
		
		if(backIC == null)
			renderText("Your IC back side cannot be empty.");
		
		if(bizFile == null)
			renderText("The BizFile of your company cannot be empty.");
		
		Company company = Company.findById(companyId);
		if(company == null)
			renderText("Company cannot be found.");
		
		company.frontIC = frontIC;
		company.backIC = backIC;
		company.businessCert = bizFile;
		company.verifyStatus = VerifyStatus.PENDING;
		company.save();
		
		renderText("success");
	}
	
	public static void addCashier(String accessToken, String cashierAccessToken){
		User user = User.find("access_token = ?", accessToken).first();
		
		if(user == null){
			renderText("Invalid access token");
		}
		
		User cashier = User.find("access_token = ?", cashierAccessToken).first();
		
		if(cashier == null){
			renderText("Cannot found the cashier.");
		}
		
		cashier.updateByBoss(user);
		
		renderText("Successfully added.");
	}
	
	public static void cashiers(String accessToken){
		User user = User.find("access_token = ?", accessToken).first();
		
		if(user == null){	
			renderText("Invalid access token");
		}
		
		render(user.cashiers);
	}
	
	public static void removeCashier(long entityId){
		User cashier = User.findById(entityId);
		
		if(cashier == null){
			renderText("Cannot found the user.");
		}
		
		cashier.removeBoss();
		
		renderText("Remove successfully.");
	}
}


































